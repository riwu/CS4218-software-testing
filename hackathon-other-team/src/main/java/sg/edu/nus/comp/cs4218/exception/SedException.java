package sg.edu.nus.comp.cs4218.exception;

public class SedException extends AbstractApplicationException {
    private static final long serialVersionUID = 690613118569875767L;
    public static final String SED_ERROR_PREFIX = "sed: ";

    public SedException(String message) {
        super(SED_ERROR_PREFIX + message);
    }

    public SedException(String message, Throwable cause) {
        super(SED_ERROR_PREFIX + message, cause);
    }
}
