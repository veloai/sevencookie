package swing.demon.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLog {
    final static SimpleDateFormat fomati = new SimpleDateFormat("yyyyMMdd");
    public void setFileLog(String filePath, String fileName) {
        try {
            StringBuilder sb = new StringBuilder();
            if(filePath != null) {
                sb.append(filePath).append(File.separator).append(fileName).append("_").append(fomati.format(new Date())).append(".log");
                FileOutputStream fOut = new FileOutputStream(sb.toString(), true);

                MultiOutputStream multiOut = new MultiOutputStream(System.out, fOut);

                PrintStream stdout = new PrintStream(multiOut);

                System.setOut(stdout);
            } else {
                System.out.println("로그경로가 null 입니다.");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
