package swing.demon.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLog {
    final static SimpleDateFormat fomati = new SimpleDateFormat("yyyyMMdd");
    public void setFileLog(String filePath, String fileName) {
        try {
            StringBuilder sb = new StringBuilder();
            if(filePath != null) {
                //sb.append(filePath).append(File.separator).append(fileName).append("_").append(fomati.format(new Date())).append(".log");
                sb.append(filePath).append(File.separator).append(fileName).append(".log");
                FileOutputStream fOut = new FileOutputStream(sb.toString(), true);

                MultiOutputStream multiOut = new MultiOutputStream(System.out, fOut);

                PrintStream stdout = new PrintStream(multiOut, true, StandardCharsets.UTF_8.toString());

                System.setOut(stdout);
            } else {
                System.out.println("로그경로가 null 입니다.");
            }

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public File getFile(String filePath, String fileName) {
        StringBuilder sb = new StringBuilder();
        //sb.append(filePath).append(File.separator).append(fileName).append("_").append(fomati.format(new Date())).append(".log");
        sb.append(filePath).append(File.separator).append(fileName).append(".log");
        File file = new File(sb.toString());

        return file;
    }
}
