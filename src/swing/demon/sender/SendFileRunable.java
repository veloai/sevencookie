package swing.demon.sender;

import swing.demon.util.props.Props;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SendFileRunable implements Runnable {
    private Props props;
    private String thisKey;


    public SendFileRunable(Props props, String thisKey) {
        this.props = props;
        this.thisKey = thisKey;
    }

    public void run() {
		OutputStream os = null;
		InputStream is = null;
        InetSocketAddress rcvr = null;
        Socket socket = null;
        try {
            int timeout = props.getInt("timeout");
            int port = props.getInt("receive.port");
            String addr = props.getString("receive.addr");

            rcvr = new InetSocketAddress(addr, port);
            socket = new Socket();

            socket.connect(rcvr, timeout);

            os = socket.getOutputStream();
            is = socket.getInputStream();

            System.out.println("- " + thisKey);

            byte[] key = thisKey.getBytes();
            os.write(key, 0, key.length);

            byte[] rs = new byte[5];
            is.read(rs, 0, rs.length);
            String reply = new String(rs);
            System.out.println("- " + reply);

            if (reply.equals("READY")) {
                sendFile(new String(key), os, is, props);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
    }

    private void sendFile(String key, OutputStream os, InputStream is, Props props) throws IOException {
        String path = props.getString(key);
        File dir = new File(path);
        List<File> files = new ArrayList<>();

        files = getFileList(dir);
        String eofExt = "." + (String) props.get("file.eof.ext");
        Boolean isRemove = props.getString("dirs.remove") != null && props.getString("dirs.remove").equalsIgnoreCase("y") ? true : false;
        System.out.println("- Path :" + path);

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println(file.getAbsoluteFile() + " is directory. skipped!!!");
                continue;
            }

            if (!file.getName().contains(eofExt)) {
                System.out.println("- Transfer file :" + file.getName());
                System.out.println("- File Size :" + file.length());

                int rBytes = 0;
                byte[] buffer = new byte[props.getInt("send.buffersize")];
                String temp = file.getName() + "|" + String.valueOf(file.length());
                byte[] finfo = temp.getBytes();

                os.write(finfo, 0, finfo.length);


                byte[] rs = new byte[5];
                is.read(rs, 0, rs.length);
                String reply = new String(rs);
                System.out.println("- " + reply);

                if (reply.equals("READY")) {
                    FileInputStream fis = new FileInputStream(file); //117

                    while ((rBytes = fis.read(buffer)) >= 0) {
                        System.out.println("- Send : " + rBytes + "bytes");
                        os.write(buffer, 0, rBytes);
                    }

                    fis.close();

                    if (isRemove) {
                        file.delete();
                        System.out.println(file.getAbsoluteFile() + " is deleted, after sending!");
                    }
                }

                is.read(rs, 0, rs.length);
                reply = new String(rs);
                System.out.println("- " + reply);

                if (!reply.equals("READY")) {
                    break;
                }
            }
        }

        byte[] msg = "ENDOFTRANSFER".getBytes();
        os.write(msg, 0, msg.length);
    }

	private  List<File> getFileList(File dir) {
		File[] aFiles = dir.listFiles();
		List<File> lFiles = aFiles != null ? Arrays.asList(aFiles) : null;
		List<File> ret = null;

		if (lFiles != null) {
			ret = new ArrayList<File>();

			ret.addAll(lFiles);
		}

		return ret;
	}
}
