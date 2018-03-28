package sg.edu.nus.comp.cs4218;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.OutputStream;

/**
 * A Shell is a command interpreter and forms the backbone of the entire program. Its responsibility
 * is to interpret commands that the user types and to run programs specified by the user.
 */
public interface Shell {
    /**
     * Parses and evaluates user's command line.
     */
    public void parseAndEvaluate(String commandString, OutputStream stdout)
            throws AbstractApplicationException, ShellException;
}
