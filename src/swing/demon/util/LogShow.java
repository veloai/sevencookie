package swing.demon.util;

public class LogShow {

    public static void logMessage(boolean isLogShow, String message) {
        if(isLogShow) {
            System.out.println(message);
        }
    }

}
