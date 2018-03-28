package sg.edu.nus.comp.cs4218.exception;

public class SplitException extends AbstractApplicationException {
    public SplitException(String message) {
        super("split: " + message);
    }

    public SplitException(String message, Throwable cause) {
        super("split: " + message, cause);
    }
}
