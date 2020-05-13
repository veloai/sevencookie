package demon.receiver.src.main.java.com.jkcorea.ftrnsfr.receiver;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.credif.util.props.Props;
import com.credif.util.props.PropsException;

@SuppressWarnings("resource")
public class ReceiverMain {
	public static final int DEFAULT_BUFFER_SIZE = 1024;

	static Props props;
	/**
	 * main class
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("##########   START FTrnsfr Receiver  ##########");
		
		try {
			String				cname	= args.length <= 0 || args[0] == null ? "./receiver.config.properties" : args[0];
			props 	= new Props(cname);
			int			 		port 	= props.getInt("receive.port");
			ServerSocket 		server 	= new ServerSocket();
			InetSocketAddress	inet 	= new InetSocketAddress(port);
			
			server.bind(inet);
			System.out.println("- PORT : " + port);
			
			while (true) {
	            Socket 			client 		= server.accept();
	            OutputStream 	os 			= client.getOutputStream(); 
	            InputStream 	is 			= client.getInputStream();
	            String			key			= ReceiverMain.getKey(is);
	            String			keyVal		= props.getString(key); 
	             
	            System.out.println("- CLIENT : " + client.getInetAddress());
	            System.out.println("- KEY : " + key);
	            System.out.println("- KEY-VALUE : " + keyVal);

	            if (keyVal == null) {
					System.out.println("Raised error!!! You should make directory![" + key + "] key");
	            	continue;
				}


	            ReceiverMain.sendReady(os);
	            ReceiverMain.receiveFile(key, keyVal, is, os);
	            
	            is.close();
	            os.close();
			}
		} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PropsException e) {
        	System.out.println(e.getMessage());
			e.printStackTrace();
		}
			
	}
	
	/**
	 * 파일 수신 처리
	 * @param key
	 * @param path
	 * @param is
	 * @param os
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

					ReceiverMain.sendReady(os);

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

					ReceiverMain.sendReady(os);

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
}
