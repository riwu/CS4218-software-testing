package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;

import java.io.ByteArrayOutputStream;

// Utility class for tests to get output of a given command.
// This is used so that the underlying command parser (currently PipeCommand) can be changed easily here
public class CommandTestUtil {

    public static String getCommandOutput(String command) throws Exception {
        PipeCommand pipeCommand = new PipeCommand(command);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        return pipeCommand.getResultStream().toString();
    }
}
