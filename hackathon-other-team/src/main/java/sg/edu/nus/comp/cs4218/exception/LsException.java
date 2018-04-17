package sg.edu.nus.comp.cs4218.exception;

public class LsException extends AbstractApplicationException {
    public static final String LS_ERR_PREFIX = "ls: ";
    public LsException(String message) {
        super(LS_ERR_PREFIX + message);
    }

    public LsException(String message, Throwable cause) {
        super(LS_ERR_PREFIX + message, cause);
    }
}
