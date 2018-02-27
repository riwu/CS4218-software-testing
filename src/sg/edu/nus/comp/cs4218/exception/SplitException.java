package sg.edu.nus.comp.cs4218.exception;

public class SplitException extends AbstractApplicationException {

	private static final long serialVersionUID = -3764970374365153192L;

	public SplitException(String message) {
        super("split: " + message);
    }
}