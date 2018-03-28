package sg.edu.nus.comp.cs4218.exception;

public class GrepException extends AbstractApplicationException {
    private static final long serialVersionUID = 690613118569875767L;
    public static final String GREP_ERR_PREFIX = "grep: ";

    public GrepException(String message) {
        super(GREP_ERR_PREFIX + message);
    }

    public GrepException(String message, Throwable cause) {
        super(GREP_ERR_PREFIX + message, cause);
    }
}
