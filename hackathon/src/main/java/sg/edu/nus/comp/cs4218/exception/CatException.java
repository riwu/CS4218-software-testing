package sg.edu.nus.comp.cs4218.exception;

public class CatException extends AbstractApplicationException {
    private static final long serialVersionUID = 2333796686823942499L;
    public static final String CAT_ERR_PREFIX = "cat: ";

    public CatException(String message) {
        super(CAT_ERR_PREFIX + message);
    }

    public CatException(String message, Throwable cause) {
        super(CAT_ERR_PREFIX + message, cause);
    }
}
