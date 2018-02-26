package sg.edu.nus.comp.cs4218.exception;

public class DiffException extends AbstractApplicationException {

	public DiffException(String message) {
		super("diff: " + message);
	}
}