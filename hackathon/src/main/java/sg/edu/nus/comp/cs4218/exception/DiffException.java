package sg.edu.nus.comp.cs4218.exception;

public class DiffException extends AbstractApplicationException {
    private static final long serialVersionUID = 2333796686823942499L;

    public DiffException(String message) {
        super("diff: " + message);
    }

    public DiffException(String message, Throwable cause) {
        super("diff: " + message, cause);
    }
}
