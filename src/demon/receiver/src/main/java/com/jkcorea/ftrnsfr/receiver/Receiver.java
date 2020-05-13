package demon.receiver.src.main.java.com.jkcorea.ftrnsfr.receiver;

import com.credif.util.props.Props;

import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {

    Socket socket;
    Props props;

    public Receiver(Socket socket, Props props) {
        this.socket = socket;
        this.props = props;
    }

    public void run() {

        OutputStream os = null;
        InputStream is = null;
        try {
            os = socket.getOutputStream();

            is = socket.getInputStream();
            String key = getKey(is);
            String keyVal = props.getString(key);

//            System.out.println("- CLIENT : " + socket.getInetAddress());
            System.out.println("- KEY : " + key);
            System.out.println("- KEY-VALUE : " + keyVal);

            if (keyVal == null) {
                System.out.println("Raised error!!! You should make directory![" + key + "] key");
            }

            sendReady(os);
            receiveFile(key, keyVal, is, os);

            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveFile(String key, String path, InputStream is, OutputStream os) throws Exception {
        String 	fname = null;
        long	fsize = -1;
        int		rbytes = -1;

        while (true) {

            try {
                byte[] buffer = new byte[props.getInt("rcv.buffersize")];

                is.read(buffer, 0, buffer.length);
                String temp = (new String(buffer)).trim();

//				System.out.println(temp);

                if (!temp.equals("ENDOFTRANSFER")) {
//                    System.out.println(temp);

                    try {
                        fname = temp.split("\\|")[0];
                        fsize = Long.parseLong(temp.split("\\|")[1]);

//                        System.out.println("- File Name : " + fname);
//                        System.out.println("- File Size : " + fsize + "bytes");
                    } catch (Exception e) {
//                        System.out.println("checking error fileName, size[" + temp + "]");
                        throw new Exception("checking error fileName, size[" + temp + "]");
                    } finally {

                    }

                    sendReady(os);

                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    long acc = 0;
                    FileOutputStream fos = new FileOutputStream(path + File.separator + fname, false);

                    while (acc < fsize) {
                        rbytes = is.read(buffer);
                        acc += rbytes;

                        if (rbytes != -1) {
//                            System.out.println("- Receive : " + acc + "bytes");
                            fos.write(buffer, 0, rbytes);
                        }
                    }

                    fos.close();
                    System.out.println("-[" + MultiReceiverMain.getNow() + "] File Received : " + path + File.separator + fname + " successfully!") ;

                    sendReady(os);

//                    System.out.println("after send READY...........");
                } else {
//                    System.out.println("received file successfully!");
                    break;
                }
            } catch (Exception e) {
//                System.out.println("unexpected Exception!!!" + e.getMessage());
                throw new Exception("unexpected Exception!!!" + e.getMessage());
            }
        }

    }

    /**
     * socket에서 key 추출
     * @param is
     * @return
     * @throws IOException
     */
    private synchronized String getKey(InputStream is) throws IOException {
        byte[] 	buffer 	= new byte[1024];
        is.read(buffer, 0, buffer.length);

        return (new String(buffer)).trim();
    }

    /**
     * READY 시그널 전송
     * @param os
     * @throws IOException
     */
    private synchronized void sendReady(OutputStream os) throws IOException {
        byte[] ready = "READY".getBytes();

        os.write(ready, 0, ready.length);
    }
}
