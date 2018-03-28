package sg.edu.nus.comp.cs4218.exception;

public class CdException extends AbstractApplicationException {
    private static final long serialVersionUID = -5127470754542073972L;
    public static final String CD_ERR_PREFIX = "cd: ";

    public CdException(String message) {
        super(CD_ERR_PREFIX + message);
    }

    public CdException(String message, Throwable cause) {
        super(CD_ERR_PREFIX + message, cause);
    }
}
