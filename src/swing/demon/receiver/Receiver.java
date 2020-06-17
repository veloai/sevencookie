package swing.demon.receiver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;

import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {

    Socket socket;
    Props props;
    boolean isLogShow = false;
    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    public Receiver(Socket socket, Props props, boolean isLogShow) {
        this.socket = socket;
        this.props = props;
        this.isLogShow = isLogShow;
    }

    @Override
    public void run() {

        try (OutputStream os = socket.getOutputStream();
             InputStream is = socket.getInputStream()
             ){
            String key = getKey(is);
            String keyVal = props.getString(key);

//            System.out.println("- CLIENT : " + socket.getInetAddress());
            logger.info("- KEY : {} - KEY-VALUE : {}", key, keyVal);

            if (keyVal == null) {
                logger.info("Raised error!!! You should make directory![ {} ] key", key);
            } else {
                sendReady(os);
                receiveFile(key, keyVal, is, os);
            }

        } catch (IOException e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        } catch (Exception e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.info(ExceptionConvert.TraceAllError(e));
            }
        }
    }

    private void receiveFile(String key, String path, InputStream is, OutputStream os) throws Exception {
        String 	fname;
        long	fsize = -1;
        int		rbytes = -1;
        int BUFFER_SIZE = props.getInt("rcv.buffersize");
        byte[] buffer;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        while (true) {

            buffer = new byte[BUFFER_SIZE];

            is.read(buffer, 0, buffer.length);
            String temp = (new String(buffer)).trim();

            if (!temp.equals("ENDOFTRANSFER")) {
                long start = System.currentTimeMillis();
                fname = temp.split("\\|")[0];
                fsize = Long.parseLong(temp.split("\\|")[1]);

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
                while (acc < fsize) {
                    if ((rbytes = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, rbytes);
                        acc += rbytes;
                    }
                }
                bos.flush();
                //logger.info("-[" + RcvMain.getNow() + "] File Received : " + path + File.separator + fname + " successfully!");
                logger.info("-[ {} ] File Received : {}{}{} successfully!",  RcvMain.getNow(), path, File.separator, fname);

                sendReady(os);
                bos.close();

                long end = System.currentTimeMillis();
                logger.info(end-start + " ms");
            } else {
//                    System.out.println("received file successfully!");
                break;
            }
        }
        if(bis != null) bis.close();
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
