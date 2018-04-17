package sg.edu.nus.comp.cs4218.exception;

public class PasteException extends AbstractApplicationException {
    private static final long serialVersionUID = -1899255051468905192L;
    public static final String PASTE_ERR_PREFIX = "paste: ";

    public PasteException(String message) {
        super(PASTE_ERR_PREFIX + message);
    }

    public PasteException(String message, Throwable cause) {
        super(PASTE_ERR_PREFIX + message, cause);
    }
}