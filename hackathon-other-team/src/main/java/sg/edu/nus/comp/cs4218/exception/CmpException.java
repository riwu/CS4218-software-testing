package sg.edu.nus.comp.cs4218.exception;

public class CmpException extends AbstractApplicationException {
    public CmpException(String message) {
        super(message);
    }

    public CmpException(String message, Throwable cause) {
        super(message, cause);
    }
}
