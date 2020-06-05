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
	static Thread thread;
	static ServerSocket server = null;
	/**
	 * start
	 * @param propPath
	 */
	public void rcvStart (String propPath) {

		System.out.println("=========== RcvMain.rcvStart ==========");
		//prop 호출
		try {
			server = new ServerSocket();
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
			if(thread == null) {
				thread = new ReceiverThread(server, props, isLogShow);
				thread.setName("receiverMain");
				thread.start();
			} else {
				LogShow.logMessage(isLogShow, "이미 실행중 입니다.");
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
		if(thread != null) {
			thread.interrupt();
			LogShow.logMessage(isLogShow, "정상적으로 receiver thread 종료");
			try {
				server.close();
				thread = null;
			} catch (IOException e) {
				LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
			}
		} else {
			LogShow.logMessage(isLogShow, "실행중인 receiver thread 없습니다.");
		}
	}

	public String getLogPath() {
		if(props != null) {
			return props.getString("log.file.path");
		}
		return null;
	}

	public static String getNow() {
		return  dFrmt.format(new Date());
	}
}
