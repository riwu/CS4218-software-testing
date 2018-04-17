package sg.edu.nus.comp.cs4218.exception;

public class MkdirException extends AbstractApplicationException {
    // TODO: how to get serialVersionUID?

    public MkdirException(String message) {
        super("mkdir: " + message);
    }

    public MkdirException(String message, Throwable cause) {
        super("mkdir: " + message, cause);
    }
}
