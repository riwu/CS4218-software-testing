package sg.edu.nus.comp.cs4218.test.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import sg.edu.nus.comp.cs4218.test.stub.ApplicationRunnerStub;
import sg.edu.nus.comp.cs4218.test.stub.CallCommandStub;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class SequenceCommandTest {
    private static final InputStream STDIN = System.in;
    private static final String[] APP_OUTPUT = {"output 1", "output 2", "output 3"};
    private static final String EMPTY_OUTPUT = "";
    private final List<String> argsList = Arrays.asList("app", "arg1", "arg2", "arg3");
    private final ApplicationRunnerStub appRunnerStub = new ApplicationRunnerStub();

    private OutputStream outputStream;
    private List<Command> commands;
    private SequenceCommand sut;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        commands = new LinkedList<>();
    }

    @AfterEach
    void tearDown() {
        CallCommandStub.numTerminated = 0;
    }

    @Test
    void testSequenceWithTwoCalls() throws Exception {
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[0]));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[1]));

        sut = new SequenceCommand(commands);
        sut.evaluate(STDIN, outputStream);

        String expectedData = String.join("",
                                          APP_OUTPUT[0],
                                          APP_OUTPUT[1]);
        assertEquals(expectedData, outputStream.toString());
    }

    @Test
    void testSequenceWithMultipleCalls() throws Exception {
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[0]));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[1]));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[2]));

        sut = new SequenceCommand(commands);
        sut.evaluate(STDIN, outputStream);

        String expectedData = String.join("",
                                          APP_OUTPUT[0],
                                          APP_OUTPUT[1],
                                          APP_OUTPUT[2]);
        assertEquals(expectedData, outputStream.toString());
    }

    @Test
    void testSequenceWithEmptyOutputCalls() throws Exception {
        commands.add(new CallCommandStub(argsList, appRunnerStub, EMPTY_OUTPUT));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[0]));
        commands.add(new CallCommandStub(argsList, appRunnerStub, STRING_NEWLINE));
        commands.add(new CallCommandStub(argsList, appRunnerStub, EMPTY_OUTPUT));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[1]));
        commands.add(new CallCommandStub(argsList, appRunnerStub, EMPTY_OUTPUT));

        sut = new SequenceCommand(commands);
        sut.evaluate(STDIN, outputStream);

        String expectedData = String.join("",
                                          APP_OUTPUT[0],
                                          STRING_NEWLINE,
                                          APP_OUTPUT[1]);
        assertEquals(expectedData, outputStream.toString());
    }

    @Test
    void testSequenceWithInducedFailure() throws Exception {
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[0]));
        commands.add(new CallCommandStub(argsList, appRunnerStub,
                                         CallCommandStub.InducedException.FAIL));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[1]));

        sut = new SequenceCommand(commands);
        sut.evaluate(STDIN, outputStream);

        String expectedData = String.join("",
                                          APP_OUTPUT[0],
                                          "shell: Induced failure", STRING_NEWLINE,
                                          APP_OUTPUT[1]);
        assertEquals(expectedData, outputStream.toString());
        assertEquals(0, CallCommandStub.numTerminated);
    }

    @Test
    void testSequenceWithInducedExit() throws Exception {
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[0]));
        commands.add(new CallCommandStub(argsList, appRunnerStub,
                                         CallCommandStub.InducedException.EXIT));
        commands.add(new CallCommandStub(argsList, appRunnerStub, APP_OUTPUT[1]));

        sut = new SequenceCommand(commands);

        Throwable exception = assertThrows(ExitException.class, () -> {
            sut.evaluate(STDIN, outputStream);
        });
        assertEquals("exit: Induced exit", exception.getMessage());

        String expectedData = APP_OUTPUT[0];
        assertEquals(expectedData, outputStream.toString());
    }
}
