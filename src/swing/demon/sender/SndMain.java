package swing.demon.sender;

import org.apache.commons.io.FilenameUtils;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.FileLog;
import swing.demon.util.LogShow;
import swing.demon.util.props.Props;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class SndMain {

    static Props props;
    private static GenericExtFilter filter = new GenericExtFilter(".eof");
    static boolean isLogShow = false;

    public void sndStart (String propPath) {
        System.out.println("SndMain.sndStart");
        //prop 호출
        props = new Props(propPath);

        isLogShow = props.getBoolean("is.log.show");
        //isExceptionShow = props.getBoolean("is.Exception.show");

        if(isLogShow) {
            String logPath = props.getString("log.file.path");
            FileLog fileLog = new FileLog();
            fileLog.setFileLog(logPath, "sender");
        }

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                send();
            }
        };
        //- 초단위 변경
        int interval = props.getInt("schedule.interval") * 1000;

        //System.out.println("Scheduling " + props.getInt("schedule.interval") + " second.");

        timer.schedule(timerTask, 5000, interval);
    }

    static void send () {
        LogShow.logMessage(isLogShow, "##########   START Sync Sender  ##########");

        Socket socket = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            int timeout = props.getInt("timeout");
            int port = props.getInt("receive.port");
            String addr = props.getString("receive.addr");
            String[] dirs = props.getString("dirs").split("\\|");

            for (int i = 0; i < dirs.length; i++) {
                //- true 일 경우 eof 파일이 있을 경우, 그 시점에 모들 파일을 읽어서 전송
                boolean isDirEof = props.getString(dirs[i] + ".eof") != null && props.getString(dirs[i] + ".eof").equalsIgnoreCase("y");
                if (isDirEof) {
                    if (!existEof(props.getString(dirs[i]))) {
                        LogShow.logMessage(isLogShow, dirs[i] + "[" + props.getString(dirs[i]) + "] eof condition, not exist eof file!");
                        continue;
                    } else {
//                        new File(props.getString(dirs[i])).deleteOnExit();
//                        System.out.println("eof file deleted!");
                    }
                }

                InetSocketAddress rcvr = new InetSocketAddress(addr, port);
                socket = new Socket();

                socket.connect(rcvr, timeout);

                os = socket.getOutputStream();
                is = socket.getInputStream();

                //System.out.println("- " + dirs[i]);

                byte[] key = dirs[i].getBytes();
                os.write(key, 0, key.length);

                byte[] rs = new byte[5];
                is.read(rs, 0, rs.length);
                String reply = new String(rs);
                //System.out.println("- " + reply);

                if (reply.equals("READY")) {
                    sendFile(new String(key), os, is, props);
                }

            }
            if(os != null) os.close();
            if(is != null) is.close();
            if(socket != null) socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
            LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
        }

    }
    static boolean existEof(String fileName) {

        String[] list = new File(fileName).list(filter);

        new File(fileName).deleteOnExit();
//        System.out.println("eof file deleted!");
        return (list != null ? list.length : 0) > 0;
    }

    static void sendFile(String key, OutputStream os, InputStream is, Props props) throws IOException {
        String path = props.getString(key);
        File dir = new File(path);
        List<File> files;
        boolean isChangeLocNm = props.getBoolean("is.changeLoc.name");
        boolean isConvertNm = props.getBoolean("is.convert.name");
        files = getFileList(dir);

        Boolean isRemove = props.getString("dirs.remove") != null && props.getString("dirs.remove").equalsIgnoreCase("y");
        LogShow.logMessage(isLogShow, "- Path :" + path);

        StringBuilder sb;
        //= chkEof 사용하지 않음 모조건 보냄
        for (File file : files) {
            if (file.isDirectory()) {
                LogShow.logMessage(isLogShow, file.getAbsoluteFile() + " is directory. skipped!!!");
                continue;
            }
            LogShow.logMessage(isLogShow, String.format("- Transfer file : %s  - File Size : %d", file.getName(), file.length()));
            int rBytes = 0;
            byte[] buffer = new byte[props.getInt("send.buffersize")];

            String fileNm = file.getName();
            String onlyNm = FilenameUtils.getBaseName(file.getName());
            String ext = FilenameUtils.getExtension(file.getName());

            //여기다가 위치 변환 넣어줘야함.
            if(isChangeLocNm && !ext.toLowerCase().equals("eof")) {

                //fileNm = changeLocNameAndConvert(onlyNm, ext, isConvertNm);
                fileNm = changeLocNmAndConvert(onlyNm, ext, isConvertNm);

                if(fileNm == null) {
                    continue;
                }

            }

            String temp = fileNm + "|" + String.valueOf(file.length());
            byte[] finfo = temp.getBytes();

            os.write(finfo, 0, finfo.length);

            byte[] rs = new byte[5];
            is.read(rs, 0, rs.length);
            String reply = new String(rs);
            //System.out.println("- " + reply);

            if (reply.equals("READY")) {
                FileInputStream fis = new FileInputStream(file); //117

                while ((rBytes = fis.read(buffer)) >= 0) {
                    //System.out.println("- Send : " + rBytes + "bytes");
                    os.write(buffer, 0, rBytes);
                }

                os.flush();

                fis.close();

                if (isRemove) {
                    if(file.delete()){
                        LogShow.logMessage(isLogShow, file.getAbsoluteFile() + " is deleted, after sending!");
                    }
                }
            }

            is.read(rs, 0, rs.length);
            reply = new String(rs);
            //System.out.println("- " + reply);

            if (!reply.equals("READY")) {
                break;
            }
        }

        byte[] msg = "ENDOFTRANSFER".getBytes();
        os.write(msg, 0, msg.length);
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

    private static String changeLocNmAndConvert(String onlyNm, String ext, boolean isConvertNm) {

        String locBf = props.getString("changeLoc.before");
        String locAf = props.getString("changeLoc.after");
        String locConvert = props.getString("convert.loc");

        int len = 0;
        String[] nmSplit = onlyNm.split("_");   //파일명 나눈거
        String[] locBfSplit = locBf.split("_");  //바꿀위치 특정
        String[] locAfSplit = null;  //바꿀위치 특정
        String[] locCvSplit = locConvert.split(","); //치환 위치

        Map<String, String> map = new HashMap<String, String>();

        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        if(nmSplit.length == locBfSplit.length) {
            len = nmSplit.length;
            for (int i = 0; i < len; i++) {
                map.put(String.valueOf(i+1), nmSplit[i]);
            }
        } else {
            LogShow.logMessage(isLogShow, "설정값과 파일이름 매핑 개수가 다릅니다. : " + onlyNm);
            return null;
        }
        if(isConvertNm) {
            StringBuilder cvStr = new StringBuilder();
            len = locCvSplit.length;
            for (int i = 0; i < len; i++) {
                String val = locCvSplit[i];
                cvStr.setLength(0);
                if(val.indexOf("_") > -1) {

                    String[] valSplit = val.split("_");
                    String replaceVal = val.replace("_","^");
                    len = valSplit.length;
                    for (int j = 0; j < len; j++) {
                        cvStr.append(map.get(valSplit[j]));
                        if(j != len-1){
                            cvStr.append("_");
                        }
                    }

                    String convertNm = props.getString(cvStr.toString());
                    if(convertNm != null) {
                        map.put(replaceVal, convertNm);
                    } else {
                        LogShow.logMessage(isLogShow, "문자열 치환 실패(제대로 등록해 주세요.) : " + cvStr.toString());
                        return null;
                    }

                    if(locAf.indexOf(val) > -1) {
                        locAf = locAf.replace(val, replaceVal);
                    }

                } else {
                    try {
                        String tm = map.get(val);
                        String convertNm = props.getString(tm);
                        if(convertNm != null){
                            //System.out.println(completeLocChng[Integer.parseInt(t[i])-1] + "변환 성공");
                            map.put(val, convertNm);
                        } else {
                            LogShow.logMessage(isLogShow, "문자열 치환 실패(제대로 등록해 주세요.) : " + tm);
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        LogShow.logMessage(isLogShow, "convert 위치 특수문자 빼고 제대로 입력해 주세요.");
                        return null;
                    }
                }

            }
        }

        locAfSplit = locAf.split("_");
        len = locAfSplit.length;

        for (int i = 0; i < len; i++) {
            //System.out.println(map.get(locAfSplit[i]));
            sb.append(map.get(locAfSplit[i]));
            if(i != len-1){
                sb.append("_");
            }
        }

        sb.append(".").append(ext);  // 제일마지막에 확장자 붙여주기
        return sb.toString();
    }

    public void sndStop () {
        System.out.println("SndMain.sndStop");


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