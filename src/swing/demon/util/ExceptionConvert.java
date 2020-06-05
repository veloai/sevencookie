package swing.demon.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionConvert {
    public static String getMessage(Exception e) {
        StringBuilder sb = new StringBuilder();

        StackTraceElement[] ste = e.getStackTrace();
        String className = ste[0].getClassName();
        String methodName = ste[0].getMethodName();
        int lineNumber = ste[0].getLineNumber();
        String fileName = ste[0].getFileName();
        sb.append("Exeception : ").append(e.toString()).append("\n");
        //sb.append(className).append(".").append(methodName).append(" ").append(fileName).append(" ").append(lineNumber).append(" line");
        sb.append(ste[0].toString()).append("\n");
        sb.append(ste[ste.length-1]);
        return sb.toString();
    }

    public static String TraceAllError(Exception e) {
        ByteArrayOutputStream out=  new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        e.printStackTrace(printStream);
        return out.toString();
    }
}
