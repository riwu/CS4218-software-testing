package sg.edu.nus.comp.cs4218.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.app.EchoInterface;
import sg.edu.nus.comp.cs4218.exception.EchoException;

/**
 * The echo command writes its arguments separated by spaces and terminates by a
 * newline on the standard output.
 * 
 * <p>
 * <b>Command format:</b> <code>echo [ARG]...</code>
 * </p>
 */
public class EchoApplication implements EchoInterface {

	/**
	 * Runs the echo application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application.
	 * @param stdin
	 *            An InputStream, not used.
	 * @param stdout
	 *            An OutputStream. Elements of args will be output to stdout,
	 *            separated by a space character.
	 * 
	 * @throws EchoException
	 *             If an I/O exception occurs.
	 */
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws EchoException {
		if (args == null) {
			throw new EchoException("Null arguments");
		}
		if (stdout == null) {
			throw new EchoException("OutputStream not provided");
		}
		try {

            stdout.write(evaluate(args).getBytes());

		} catch (IOException e) {
			throw new EchoException("IOException");
		}
	}

	public String evaluate(String[] args){
		Pattern spacePattern = Pattern.compile("\\s+");
		boolean hasSpaces = Arrays.stream(args).map( arg -> spacePattern.matcher(arg).matches())
							.reduce(false, (result, bool) -> result || bool);

        return hasSpaces? Arrays.stream(args)
				.reduce("", (result, arg) -> result + " " + arg) + System.lineSeparator():
				Arrays.stream(args)
                .reduce("", (result, arg) -> result + " " + arg)
                .trim() + System.lineSeparator();
    }

}
