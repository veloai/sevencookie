package swing.demon.sender;

import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class SenderMain {
    static Props props;
    final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private static GenericExtFilter filter = new GenericExtFilter(".eof");
    public static void main(String[] args) {
        final String cname = args.length <= 0 || args[0] == null || args[0].isEmpty() ? null : args[0];

        try {
            props = new Props(cname == null ? "./sender.config.properties" : cname);

        } catch (IOException | PropsException io) {
            io.printStackTrace();
            System.exit(-1);
        }


        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                send(cname);
//				sayHello();
            }
        };
        //- 초단위 변경
        int interval = props.getInt("schedule.interval") * 1000;

        System.out.println("Scheduling " + props.getInt("schedule.interval") + " second.");

        timer.schedule(timerTask, 5000, interval);
    }

    static void sayHello() {
        System.out.println("hello" + new Date());
        try {
            Thread.sleep(1000 * 4 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getNow() {
        return dFrmt.format(new Date());
    }

    static boolean existEof(String fileName) {

        String[] list = new File(fileName).list(filter);

        new File(fileName).deleteOnExit();
//        System.out.println("eof file deleted!");
        return list.length > 0? true : false;
    }


    static void send(String cname) {
        System.out.println("##########   START Sync Sender  ##########");

        try {
            int timeout = props.getInt("timeout");
            int port = props.getInt("receive.port");
            String addr = props.getString("receive.addr");
            String[] dirs = props.getString("dirs").split("\\|");

            for (int i = 0; i < dirs.length; i++) {
                //- true 일 경우 eof 파일이 있을 경우, 그 시점에 모들 파일을 읽어서 전송
                boolean isDirEof = (props.getString(dirs[i] + ".eof") != null && props.getString(dirs[i] + ".eof").equalsIgnoreCase("y")) ? true : false;
                if (isDirEof) {
                    if (!existEof(props.getString(dirs[i]))) {
                        System.out.println(dirs[i] + "[" + props.getString(dirs[i]) + "] eof condition, not exist eof file!");
                        continue;
                    } else {
//                        new File(props.getString(dirs[i])).deleteOnExit();
//                        System.out.println("eof file deleted!");
                    }
                }

                InetSocketAddress rcvr = new InetSocketAddress(addr, port);
                Socket socket = new Socket();

                socket.connect(rcvr, timeout);

                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();

                System.out.println("- " + dirs[i]);

                byte[] key = dirs[i].getBytes();
                os.write(key, 0, key.length);

                byte[] rs = new byte[5];
                is.read(rs, 0, rs.length);
                String reply = new String(rs);
                System.out.println("- " + reply);

                if (reply.equals("READY")) {
                    sendFile(new String(key), os, is, props);
                }

                os.close();
                is.close();
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    private static List<File> getFileList(File dir) {
        File[] aFiles = dir.listFiles();
        List<File> lFiles = aFiles != null ? Arrays.asList(aFiles) : null;
        List<File> ret = null;

        if (lFiles != null) {
            ret = new ArrayList<File>();

            ret.addAll(lFiles);
        }

        return ret;
    }

    static void sendFile(String key, OutputStream os, InputStream is, Props props) throws IOException {
        String path = props.getString(key);
        File dir = new File(path);
        List<File> files = new ArrayList<>();

        files = getFileList(dir);

        Boolean isRemove = props.getString("dirs.remove") != null && props.getString("dirs.remove").equalsIgnoreCase("y") ? true : false;
        System.out.println("- Path :" + path);

        //= chkEof 사용하지 않음 모조건 보냄
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println(file.getAbsoluteFile() + " is directory. skipped!!!");
                continue;
            }

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

        byte[] msg = "ENDOFTRANSFER".getBytes();
        os.write(msg, 0, msg.length);
    }

}

class GenericExtFilter implements FilenameFilter {

    private String ext;

    public GenericExtFilter(String ext) {
        this.ext = ext;
    }

    public boolean accept(File dir, String name) {
        return (name.endsWith(ext));
    }
}