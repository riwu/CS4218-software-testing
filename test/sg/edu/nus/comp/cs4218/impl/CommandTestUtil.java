package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class CommandTestUtil {

    public static String getCommandOutput(String command) throws Exception {
        PipeCommand pipeCommand = new PipeCommand(command);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        return pipeCommand.getResultStream().toString();
    }

    public static void testCommand(String expected, String command) throws Exception {
        assertEquals(expected, getCommandOutput(command));
    }
}
