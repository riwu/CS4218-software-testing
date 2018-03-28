package sg.edu.nus.comp.cs4218.test.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.test.stub.ApplicationRunnerStub;
import sg.edu.nus.comp.cs4218.test.stub.CallCommandStub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipeCommandTest {
    private static final String INITIAL_DATA = "initial data";
    private static final String INJECTED_DATA = "injected data";
    private final List<String> argsList = Arrays.asList("app", "arg1", "arg2", "arg3");
    private final ApplicationRunnerStub appRunnerStub = new ApplicationRunnerStub();

    private InputStream inputStream;
    private OutputStream outputStream;
    private List<CallCommand> callCommands;
    private PipeCommand sut;

    @BeforeEach
    void setUp() {
        inputStream = new ByteArrayInputStream(INITIAL_DATA.getBytes());
        outputStream = new ByteArrayOutputStream();
        callCommands = new LinkedList<>();
    }

    @AfterEach
    void tearDown() {
        CallCommandStub.numTerminated = 0;
    }

    @Test
    void testPipeWithTwoCalls() throws Exception {
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));

        sut = new PipeCommand(callCommands);
        sut.evaluate(inputStream, outputStream);

        assertTrue(INITIAL_DATA.equals(outputStream.toString()));
    }

    @Test
    void testPipeWithMultipleCalls() throws Exception {
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub, INJECTED_DATA));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));

        sut = new PipeCommand(callCommands);
        sut.evaluate(inputStream, outputStream);

        assertTrue(INJECTED_DATA.equals(outputStream.toString()));
    }

    @Test
    void testPipeWithInducedFailure() {
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub,
                                             CallCommandStub.InducedException.FAIL));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));

        sut = new PipeCommand(callCommands);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(inputStream, outputStream);
        });
        assertEquals("shell: Induced failure", exception.getMessage());
        assertEquals(3, CallCommandStub.numTerminated);
        assertTrue("".equals(outputStream.toString()));
    }

    @Test
    void testPipeWithInducedExit() {
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub,
                                             CallCommandStub.InducedException.EXIT));
        callCommands.add(new CallCommandStub(argsList, appRunnerStub));

        sut = new PipeCommand(callCommands);

        Throwable exception = assertThrows(ExitException.class, () -> {
            sut.evaluate(inputStream, outputStream);
        });
        assertEquals("exit: Induced exit", exception.getMessage());
        assertEquals(1, CallCommandStub.numTerminated);
        assertTrue("".equals(outputStream.toString()));
    }
}
