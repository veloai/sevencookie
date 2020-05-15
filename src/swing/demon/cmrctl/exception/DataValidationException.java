package swing.demon.cmrctl.exception;

public class DataValidationException extends Exception {
    public DataValidationException(){
        super();
    }

    public DataValidationException(String message){
        super(message);
    }
}