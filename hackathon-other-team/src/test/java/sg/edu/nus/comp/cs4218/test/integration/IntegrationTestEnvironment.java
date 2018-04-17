package sg.edu.nus.comp.cs4218.test.integration;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.test.TestEnvironment;
import sg.edu.nus.comp.cs4218.test.integration.params.PipeTestParams;
import sg.edu.nus.comp.cs4218.test.integration.params.PipeTwoTestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base class that all integration tests should implement.
 */
public class IntegrationTestEnvironment extends TestEnvironment {
    protected final OutputStream stdout = new ByteArrayOutputStream();
    private final ApplicationRunner appRunner = new ApplicationRunner();
    protected InputStream stdin = null;

    /**
     * Creates a new command given the app string.
     *
     * @param app  String of the application as defined in ApplicationRunner
     * @param args Arguments to pass into the application
     *
     * @return CallCommand that can be executed
     */
    protected CallCommand createCommand(String app, String... args) {
        List<String> completeArgs = new ArrayList<>();
        completeArgs.add(app);
        completeArgs.addAll(Arrays.asList(args));
        return new CallCommand(completeArgs, appRunner);
    }

    /**
     * Creates the PipeCommand to be tested using the test parameters.
     *
     * @param param Test parameters
     *
     * @return PipeCommand
     */
    protected PipeCommand createPipeCommand(PipeTwoTestParam param) {
        String[] args1 = param.getArgs1().toArray(new String[0]);
        String[] args2 = param.getArgs2().toArray(new String[0]);
        CallCommand cmd1 = createCommand(param.getApp1(), args1);
        CallCommand cmd2 = createCommand(param.getApp2(), args2);

        return pipeCommands(cmd1, cmd2);
    }

    /**
     * Creates a PipeCommand that consist of several commands piped together.
     *
     * @param param
     * @return
     */
    protected PipeCommand createMultiPipedCommand(PipeTestParams param) {
        int numApps = param.getApps().size();
        List<CallCommand> callCommands = new ArrayList<>();

        for (int i = 0; i < numApps; i++) {
            String[] args = param.getArgs(i).toArray(new String[0]);
            CallCommand newCmd = createCommand(param.getApp(i), args);
            callCommands.add(newCmd);
        }

        return pipeCommands(callCommands.toArray(new CallCommand[0]));
    }

    /**
     * Returns a new pipe command given a list of CallCommand. The commands will be piped in that
     * order.
     *
     * @param commands
     *
     * @return
     */
    protected PipeCommand pipeCommands(CallCommand... commands) {
        return new PipeCommand(Arrays.asList(commands));
    }

    /**
     * Returns a new SequenceCommand given a list of commands to chain up.
     *
     * @param commands
     *
     * @return
     */
    protected SequenceCommand sequenceCommands(Command... commands) {
        return new SequenceCommand(Arrays.asList(commands));
    }

    /**
     * Writes a string into the input stream. To be called BEFORE a test is executed.
     *
     * @param input
     */
    protected void writeToStdin(String input) {
        stdin = new ByteArrayInputStream(input.getBytes());
    }

    /**
     * Reads data from the output stream, to be called AFTER a command is executed.
     *
     * @return String that reflects the written output to STDOUT.
     */
    protected String readFromStdout() {
        return stdout.toString();
    }

    /**
     * Assert that the command will not throw an Exception during execution, and also that the
     * outputs from STDOUT matches that of what we expected.
     *
     * @param param Test parameters
     */
    protected void assertValidCommand(PipeTwoTestParam param) {
        PipeCommand cmd = createPipeCommand(param);
        writeToStdin("");
        try {
            cmd.evaluate(stdin, stdout);
        } catch (Exception e) {
            fail("Should not throw exception");
        }
        assertEquals(param.getExpected(), readFromStdout());
    }

    /**
     * Assert that the test parameters would throw an Exception during execution.
     *
     * @param param Test parameters
     */
    protected void assertInvalidCommand(PipeTwoTestParam param) {
        PipeCommand cmd = createPipeCommand(param);
        writeToStdin("");
        try {
            cmd.evaluate(stdin, stdout);
            fail("Should throw exception");
        } catch (Exception e) {
            // TODO: Check exception message
        }
    }

    /**
     * Assert that the test parameters of a multiple piped test case will execute and output matches
     * the expected value.
     *
     * @param param Test parameters
     */
    protected void assertValidMultiPipedCommand(PipeTestParams param) {
        PipeCommand cmd = createMultiPipedCommand(param);
        writeToStdin("");
        try {
            cmd.evaluate(stdin, stdout);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        assertEquals(param.getExpected(), readFromStdout());
    }
}
