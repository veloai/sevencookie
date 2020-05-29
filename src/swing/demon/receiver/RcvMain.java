package swing.demon.receiver;

import swing.demon.util.ExceptionConvert;
import swing.demon.util.FileLog;
import swing.demon.util.LogShow;
import swing.demon.util.props.Props;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("resource")
public class RcvMain {

	static Props props;
	private static ExecutorService threadPool;
	final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	static boolean isLogShow = false;

	/**
	 * start
	 * @param propPath
	 */
	public void rcvStart (String propPath) {
		System.out.println("RcvMain.rcvStart");

		//prop 호출
		try (ServerSocket server = new ServerSocket()){
			props = new Props(propPath);

			int port = props.getInt("receive.port");
			int THREAD_CNT = props.getInt("thread.pool.cnt");
			threadPool = Executors.newFixedThreadPool(THREAD_CNT);
			isLogShow = props.getBoolean("is.log.show");
			if(isLogShow) {
				String logPath = props.getString("log.file.path");
				FileLog fileLog = new FileLog();
				fileLog.setFileLog(logPath, "receiver");
			}
			InetSocketAddress	inet 	= new InetSocketAddress(port);
			server.bind(inet);
			LogShow.logMessage(isLogShow, "##########   START FTrnsfr RcvMain  ##########");
			LogShow.logMessage(isLogShow, "- PORT : " + port);

			while (true) {
				try (Socket client = server.accept()){
					threadPool.execute(new Receiver(client, props, isLogShow));
				}
			}

		} catch (IOException io) {
			LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(io));
		} catch (Exception e) {
			LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
		}
	}

	/**
	 * stop
	 */
	public void rcvStop () {
		System.out.println("RcvMain.rcvStop");

	}

	public static String getNow() {
		return  dFrmt.format(new Date());
	}
}
