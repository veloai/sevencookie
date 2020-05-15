package swing.demon.receiver;

/**
 *  Class Name : ReceiverMain.java
 *  Description : 파일을 수신하는 클래스
 *  Modification Information
 *  @author 강형모
 *  @since 2018.08.2
 *  @version 1.0.0
 *  @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *  수정일		수정자		수정내용
 *  -------    	--------    ---------------------------
 *  2018.08.22	강형모 		최초 생성
 *
 * </pre>
 */



import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("resource")
public class MultiReceiverMain {
	public static final int DEFAULT_BUFFER_SIZE = 1024;
	/// 스레드 풀의 최대 스레드 개수를 지정합니다.
	private static int THREAD_CNT;
	private static ExecutorService threadPool;
	static Props props;
	final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * main class
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("##########   START FTrnsfr Multi Receiver  ##########");
		ServerSocket 		server = null;
		try {
			String				cname	= args.length <= 0 || args[0] == null ? "./receiver.config.properties" : args[0];
			props 	= new Props(cname);
			int			 		port 	= props.getInt("receive.port");
				THREAD_CNT = props.getInt("thread.pool.cnt");
				threadPool = Executors.newFixedThreadPool(THREAD_CNT);

			System.out.println(threadPool);
			server 	= new ServerSocket();
			InetSocketAddress	inet 	= new InetSocketAddress(port);
			
			server.bind(inet);
			System.out.println("- PORT : " + port);

			while (true) {

				try {
					Socket client = server.accept();
//					Receiver receiver = new Receiver(client, props);
//					receiver.start();
					System.out.println(threadPool);

					threadPool.execute(new Receiver(client, props));

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PropsException e) {
        	System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	/**
	 * 파일 수신 처리
	 * @param key
	 * @param path
	 * @param is
	 * @param os
	 *
	 * @throws IOException
	 */
	static void receiveFile(String key, String path, InputStream is, OutputStream os) throws IOException {
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
					System.out.println(temp);

					try {
						fname = temp.split("\\|")[0];
						fsize = Long.parseLong(temp.split("\\|")[1]);

						System.out.println("- File Name : " + fname);
						System.out.println("- File Size : " + fsize + "bytes");
					} catch (Exception e) {
							System.out.println("checking error fileName, size[" + temp + "]");
							break;
					} finally {

					}

					MultiReceiverMain.sendReady(os);

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
							System.out.println("- Receive : " + acc + "bytes");
							fos.write(buffer, 0, rbytes);
						}
					}

					fos.close();
					System.out.println("- File Write : " + fname);

					MultiReceiverMain.sendReady(os);

					System.out.println("after send READY...........");
				} else {
					System.out.println("finished Sending file ...........");
					break;
				}
			} catch (Exception e) {
				System.out.println("unexpected Exception!!!" + e.getMessage());
				e.printStackTrace();
			}
		} 

	}
	
	/**
	 * socket에서 key 추출
	 * @param is
	 * @return
	 * @throws IOException
	 */
	static String getKey(InputStream is) throws IOException {
		byte[] 	buffer 	= new byte[1024];
		is.read(buffer, 0, buffer.length);		
		
		return (new String(buffer)).trim();
	}
	
	/**
	 * READY 시그널 전송
	 * @param os
	 * @throws IOException
	 */
	static void sendReady(OutputStream os) throws IOException {
		byte[] ready = "READY".getBytes();
		
		os.write(ready, 0, ready.length);
	}

	public static String getNow() {
		return  dFrmt.format(new Date());
	}
}
