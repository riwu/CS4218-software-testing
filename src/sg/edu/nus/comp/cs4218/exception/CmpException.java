package sg.edu.nus.comp.cs4218.exception;

public class CmpException extends AbstractApplicationException {

	private static final long serialVersionUID = 3846682179885789127L;
	
	public static final String EOF_MSG = "EOF on ";
	
	public CmpException(String message) {
		super("cmp: " + message);
	}
}
