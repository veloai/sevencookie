package swing.demon.receiver;


import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;

import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {

    Socket socket;
    Props props;
    boolean isLogShow = false;

    public Receiver(Socket socket, Props props, boolean isLogShow) {
        this.socket = socket;
        this.props = props;
        this.isLogShow = isLogShow;
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
            if(isLogShow) {
                System.out.println("- KEY : " + key + " - KEY-VALUE : " + keyVal);
            }

            if (keyVal == null) {
                if(isLogShow) {
                    System.out.println("Raised error!!! You should make directory![" + key + "] key");
                }
            } else {
                sendReady(os);

                receiveFile(key, keyVal, is, os);
            }

            is.close();
            os.close();

        } catch (IOException e) {
            if(isLogShow) {
                System.out.println(ExceptionConvert.getMessage(e));
            }
        } catch (Exception e) {
            if(isLogShow) {
                System.out.println(ExceptionConvert.getMessage(e));
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                if(isLogShow) {
                    System.out.println(ExceptionConvert.getMessage(e));
                }
            }
        }
    }

    private void receiveFile(String key, String path, InputStream is, OutputStream os) throws Exception {
        String 	fname = null;
        long	fsize = -1;
        int		rbytes = -1;
        int     flen = 0;
        int BUFFER_SIZE = props.getInt("rcv.buffersize");
        FileOutputStream fos;
        byte[] buffer;
        BufferedInputStream bis;
        BufferedOutputStream bos;
        while (true) {

            buffer = new byte[BUFFER_SIZE];

            is.read(buffer, 0, buffer.length);
            String temp = (new String(buffer)).trim();

            if (!temp.equals("ENDOFTRANSFER")) {
                long start = System.currentTimeMillis();
                fname = temp.split("\\|")[0];
                fsize = Long.parseLong(temp.split("\\|")[1]);
                flen = (int) fsize;

                if(isLogShow) {
                    System.out.format("- File Name : %s  - File Size : %d bytes\n", fname, fsize);
                }

                sendReady(os);

                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if(fname.toLowerCase().indexOf(".eof")> -1) {
                    sendReady(os);
                    continue;
                }

                long acc = 0;
                //fos = new FileOutputStream(path + File.separator + fname, false);
                StringBuilder sb = new StringBuilder();
                sb.append(path).append(File.separator).append(fname);
                bis = new BufferedInputStream(is);
                bos = new BufferedOutputStream(new FileOutputStream(sb.toString(), false));

                buffer = new byte[BUFFER_SIZE*2];
                /*while (acc < fsize) {
                    if ((rbytes = is.read(buffer)) != -1) {
//                            System.out.println("- Receive : " + acc + "bytes");
                        fos.write(buffer, 0, rbytes);
                        acc += rbytes;
                    }
                }*/
                long s1 = System.currentTimeMillis();
                while (acc < fsize) {
                    if ((rbytes = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, rbytes);
                        acc += rbytes;
                        long e1 = System.currentTimeMillis();
                        System.out.println("mid1 : "+(e1-s1) + " ms");
                    }
                }
                bos.flush();
                if(isLogShow) {
                    System.out.println("-[" + MultiReceiverMain.getNow() + "] File Received : " + path + File.separator + fname + " successfully!") ;
                }

                sendReady(os);
                bos.close();
                //fos.close();
                long end = System.currentTimeMillis();
                System.out.println(end-start + " ms");
//                    System.out.println("after send READY...........");
            } else {
//                    System.out.println("received file successfully!");
                break;
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
