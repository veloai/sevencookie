package swing.demon.receiver;

import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("resource")
public class RcvMain {

	public static final int DEFAULT_BUFFER_SIZE = 1024;
	static Props props;
	private static int THREAD_CNT;
	private static ExecutorService threadPool;
	final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	static boolean isLogShow = false;

	/**
	 * start
	 * @param propPath
	 */
	public void rcvStart (String propPath) {
		System.out.println("RcvMain.rcvStart");
		ServerSocket server = null;
		StringBuffer sf = new StringBuffer();

		long startlogT = System.currentTimeMillis();
		//prop 호출
		try {
			props = new Props(propPath);

			int port = props.getInt("receive.port");
			THREAD_CNT = props.getInt("thread.pool.cnt");
			threadPool = Executors.newFixedThreadPool(THREAD_CNT);
			isLogShow = props.getBoolean("is.log.show");

			//System.out.println(threadPool);
			server 	= new ServerSocket();
			InetSocketAddress inet 	= new InetSocketAddress(port);

			server.bind(inet);
			if(isLogShow) {
				System.out.println("##########   START FTrnsfr Multi Receiver  ##########");
				System.out.println("- PORT : " + port);
			}
			while (true) {
				Socket client = null;
				try {
					client = server.accept();
//					Receiver receiver = new Receiver(client, props);
//					receiver.start();

					threadPool.execute(new Receiver(client, props, isLogShow));

				} catch (Exception e) {
					if(isLogShow) {
						System.out.println(ExceptionConvert.getMessage(e));
					}
				}
			}


		} catch (IOException e) {
			if(isLogShow) {
				System.out.println(ExceptionConvert.getMessage(e));
			}
		} catch (PropsException e) {
			if(isLogShow) {
				System.out.println(ExceptionConvert.getMessage(e));
			}
		} catch (Exception e) {
			if(isLogShow) {
				System.out.println(ExceptionConvert.getMessage(e));
			}
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				if(isLogShow) {
					System.out.println(ExceptionConvert.getMessage(e));
				}
			}
		}
		//Path path = Paths.get(props.getString("watching.dir"));
	}
	public static String getNow() {
		return  dFrmt.format(new Date());
	}

	/**
	 * stop
	 */
	public void rcvStop () {
		System.out.println("RcvMain.rcvStop");

	}
}
