package sg.edu.nus.comp.cs4218;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Command is a generic grammar for the user to specify various operations to be performed.
 *
 * <p>
 * <b>Command format:</b> <code>&lt;Call&gt; or &lt;Sequence&gt; or &lt;Pipe&gt;</code>
 * </p>
 */
public interface Command {
    /**
     * Evaluates command using data provided through stdin stream. Writes result to stdout stream.
     *
     * @param stdin  InputStream to get data from.
     * @param stdout OutputStream to write data to.
     *
     * @throws AbstractApplicationException If an exception happens while running the application(s)
     *                                      specified in the command.
     * @throws ShellException               If an exception happens while evaluating the command.
     */
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException;

    /**
     * Terminates current execution of the command.
     */
    public void terminate();
}
