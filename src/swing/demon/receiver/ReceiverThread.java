package swing.demon.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverThread extends Thread {
    ServerSocket socket = null;
    boolean isLogShow = false;
    Props props;
    Socket client = null;
    static Thread t;
    private static Logger logger = LoggerFactory.getLogger(ReceiverThread.class);

    ReceiverThread(ServerSocket server, Props props, boolean isLogShow) {
        this.socket = server;
        this.props = props;
        this.isLogShow = isLogShow;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                client = socket.accept();

                t = new Thread(new Receiver(client, props, isLogShow));
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException io) {
            logger.info(ExceptionConvert.TraceAllError(io));
        } catch (Exception e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        } finally {
            try {
                t.interrupt();
                client.close();
            } catch (IOException e) {
                logger.info(ExceptionConvert.TraceAllError(e));
            }
        }


    }
}
