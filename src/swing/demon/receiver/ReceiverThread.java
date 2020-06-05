package swing.demon.receiver;

import swing.demon.util.ExceptionConvert;
import swing.demon.util.LogShow;
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
            LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(io));
        } catch (Exception e) {
            LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
        } finally {
            try {
                t.interrupt();
                client.close();
            } catch (IOException e) {
                LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
            }
        }


    }
}
