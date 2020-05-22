package swing.demon.sender;

import org.apache.commons.io.FilenameUtils;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class SenderMain {
    static Props props;
    private static GenericExtFilter filter = new GenericExtFilter(".eof");
    static boolean isLogShow = false;
    //static boolean isExceptionShow = false;

    public static void main(String[] args) {
        final String cname = args.length <= 0 || args[0] == null || args[0].isEmpty() ? null : args[0];

        try {
            props = new Props(cname == null ? "./sender.config.properties" : cname);
            isLogShow = props.getBoolean("is.log.show");
            //isExceptionShow = props.getBoolean("is.Exception.show");
        } catch (IOException | PropsException io) {
            //io.printStackTrace();
            if(isLogShow) {
                System.out.println(ExceptionConvert.getMessage(io));
            }
            System.exit(-1);
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                send(propPath);
            }
        };
        //- 초단위 변경
        int interval = props.getInt("schedule.interval") * 1000;

        //System.out.println("Scheduling " + props.getInt("schedule.interval") + " second.");

        timer.schedule(timerTask, 5000, interval);

    /*static void sayHello() {
        System.out.println("hello" + new Date());
        try {
            Thread.sleep(1000 * 4 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    static void send(String cname) {
        if(isLogShow) {
            System.out.println("##########   START Sync Sender  ##########");
        }
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
                boolean isDirEof = (props.getString(dirs[i] + ".eof") != null && props.getString(dirs[i] + ".eof").equalsIgnoreCase("y")) ? true : false;
                if (isDirEof) {
                    if (!existEof(props.getString(dirs[i]))) {
                        if(isLogShow){
                            System.out.println(dirs[i] + "[" + props.getString(dirs[i]) + "] eof condition, not exist eof file!");
                        }
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
            if(isLogShow) {
                System.out.println(ExceptionConvert.getMessage(e));
            }
        }

    }

    static void sendFile(String key, OutputStream os, InputStream is, Props props) throws IOException {
        String path = props.getString(key);
        File dir = new File(path);
        List<File> files = new ArrayList<>();
        boolean isChangeLocNm = props.getBoolean("is.changeLoc.name");
        boolean isConvertNm = props.getBoolean("is.convert.name");
        files = getFileList(dir);

        Boolean isRemove = props.getString("dirs.remove") != null && props.getString("dirs.remove").equalsIgnoreCase("y") ? true : false;
        if(isLogShow) {
            System.out.println("- Path :" + path);
        }
        StringBuilder sb;
        //= chkEof 사용하지 않음 모조건 보냄
        for (File file : files) {
            if (file.isDirectory()) {
                if(isLogShow) {
                    System.out.println(file.getAbsoluteFile() + " is directory. skipped!!!");
                }
                continue;
            }
            if(isLogShow) {
                System.out.format("- Transfer file : %s  - File Size : %d \n", file.getName(), file.length());
            }
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
                    file.delete();
                    if(isLogShow) {
                        System.out.println(file.getAbsoluteFile() + " is deleted, after sending!");
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
            if(isLogShow) {
                System.out.format("설정값과 파일이름 매핑 개수가 다릅니다. : %s\n", onlyNm);
            }
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
                        if(isLogShow) {
                            System.out.format("문자열 치환 실패(제대로 등록해 주세요.) : %s\n", cvStr.toString());
                        }
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
                            if(isLogShow) {
                                System.out.format("문자열 치환 실패(제대로 등록해 주세요.) : %s\n", tm);
                            }
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        if(isLogShow) {
                            System.out.println("convert 위치 특수문자 빼고 제대로 입력해 주세요.");
                        }
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

    /*public static String changeLocNameAndConvert(String onlyNm, String ext, boolean isConvertNm) {

        String locBf = props.getString("changeLoc.before");
        String locAf = props.getString("changeLoc.after");
        String locConvert = props.getString("convert.loc");

        String[] nmSplit = onlyNm.split("_");   //파일명 나눈거
        String[] locAfSplit = locAf.split("_");  //바꿀위치 특정
        String[] locCvSplit = locConvert.split(","); //치환 위치
        StringBuilder sb = new StringBuilder();
        int len = 0;
        if(nmSplit.length == locAfSplit.length) {

            len = locAfSplit.length;
            int[] intAfSplit = stringToIntArray(locAfSplit, len);

            for (int j = 0; j < len; j++) {
                String tmp = nmSplit[intAfSplit[j]-1];
                *//*if(isConvertNm) {
                    //변환 위치 찾아서 변경
                    if(Arrays.asList(locCvSplit).contains(String.valueOf(j+1))) {
                        String convertNm = props.getString(tmp);
                        if(convertNm != null){
                            sb.append(convertNm);
                        } else {
                            System.out.format("문자열 치환 실패(제대로 등록해 주세요.) : %s\n", tmp);
                            return null;
                        }
                    } else {
                        sb.append(tmp);
                    }

                } else {
                    sb.append(tmp);
                }*//*
                sb.append(tmp);

                if(j != len-1) {
                    sb.append("_");
                } 
            }
        } else {
            if(isLogShow) {
                System.out.format("설정값과 파일이름 매핑 개수가 다릅니다. : %s\n", onlyNm);
            }
            return null;
        }

        //여기서 isConvertNm true면 치환
        if(isConvertNm) {
            len = locCvSplit.length;
            long start = System.currentTimeMillis();

            String[] completeLocChng = sb.toString().split("_");
            List<String> sbList = new ArrayList<>(Arrays.asList(completeLocChng));
            List<Integer> rmIdx = new ArrayList<Integer>();

            for (int i = 0; i < len; i++) {
                if(locCvSplit[i].contains("_")){
                    int[] tmpSplit = stringToIntArray(locCvSplit[i].split("_"), locCvSplit[i].split("_").length);
                    int tmpLen = tmpSplit.length;
                    StringBuilder cntStr = new StringBuilder();
                    for (int j = 0; j < tmpLen; j++) {
                        cntStr.append(completeLocChng[tmpSplit[j]-1]);
                        if(j != tmpLen -1) {
                            cntStr.append("_");
                        }
                    }
                    String convertNm = props.getString(cntStr.toString());
                    if(convertNm != null){

                        for (int j = 0; j < tmpLen; j++) {
                            if (j==0) {
                                sbList.set(tmpSplit[j]-1, convertNm);
                            } else {
                                //list.remove(tmpSplit[j]-1);
                                rmIdx.add(tmpSplit[j]-1);
                            }
                        }

                    } else {
                        if(isLogShow) {
                            System.out.format("문자열 치환 실패(제대로 등록해 주세요.) : %s\n", cntStr.toString());
                        }
                        return null;
                    }
                } else {
                    try {
                        String tm = completeLocChng[Integer.parseInt(locCvSplit[i])-1];
                        String convertNm = props.getString(tm);
                        if(convertNm != null){
                            sbList.set(Integer.parseInt(locCvSplit[i])-1, convertNm);
                            //System.out.println(completeLocChng[Integer.parseInt(t[i])-1] + "변환 성공");
                        } else {
                            if(isLogShow) {
                                System.out.format("문자열 치환 실패(제대로 등록해 주세요.) : %s\n", tm);
                            }
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        if(isLogShow) {
                            System.out.println("convert 위치 특수문자 빼고 제대로 입력해 주세요.");
                        }
                        return null;
                    }
                }
            }
            len = rmIdx.size();
            for (int i = 0; i < len; i++) {
                sbList.remove(rmIdx.get(i).intValue());
            }
            long end = System.currentTimeMillis();
            System.out.println((end-start) + "mss1");
            sb.setLength(0);
            sb.append(String.join("_", sbList));
        }

        sb.append(".").append(ext);  // 제일마지막에 확장자 붙여주기
        return sb.toString();
    }

    private static int[] stringToIntArray(String[] stringSplit, int len) {
        int[] intSplit = new int[len];

        for (int i=0; i < len; i++) {
            intSplit[i] = Integer.parseInt(stringSplit[i]);
        }
        return intSplit;
    }*/

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
