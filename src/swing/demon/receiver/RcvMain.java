package swing.demon.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RcvMain {

	static Props props;
	private static ExecutorService threadPool;
	final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	static boolean isLogShow = false;
	static Thread thread;
	static ServerSocket server = null;
	private static Logger logger = LoggerFactory.getLogger(RcvMain.class);
	/**
	 * start
	 * @param propPath
	 */
	public void rcvStart (String propPath) {
		//prop 호출
		try {
			server = new ServerSocket();
			props = new Props(propPath);

			int port = props.getInt("receive.port");
			int THREAD_CNT = props.getInt("thread.pool.cnt");
			threadPool = Executors.newFixedThreadPool(THREAD_CNT);
			isLogShow = props.getBoolean("is.log.show");

			InetSocketAddress	inet 	= new InetSocketAddress(port);
			server.bind(inet);
			logger.info("##########   START FTrnsfr RcvMain  ##########");
			logger.info("- PORT : {}", port);
			if(thread == null) {
				thread = new ReceiverThread(server, props, isLogShow);
				thread.setName("receiverMain");
				thread.start();
			} else {
				logger.info("이미 실행중 입니다.");
			}

		} catch (IOException io) {
			logger.info(ExceptionConvert.TraceAllError(io));
		} catch (Exception e) {
			logger.info(ExceptionConvert.TraceAllError(e));
		}
	}

	/**
	 * stop
	 */
	public void rcvStop () {
		System.out.println("RcvMain.rcvStop");
		if(thread != null) {
			thread.interrupt();
			logger.info("정상적으로 receiver thread 종료");
			try {
				server.close();
				thread = null;
			} catch (IOException e) {
				logger.info(ExceptionConvert.TraceAllError(e));
			}
		} else {
			logger.info("실행중인 receiver thread 없습니다.");
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
