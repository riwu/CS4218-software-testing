package sg.edu.nus.comp.cs4218.test.cmd;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.test.FileSystemTest;
import sg.edu.nus.comp.cs4218.test.stub.ApplicationRunnerStub;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_SYNTAX;

class CallCommandTest extends FileSystemTest {
    private static final String APP = "app";
    private static final String[] ARGS = {"arg1", "arg2", "arg3"};
    private static final InputStream STDIN = System.in;
    private static final OutputStream STDOUT = System.out;
    private static final String MSG_SYNTAX = "shell: " + ERR_SYNTAX;

    private final ApplicationRunnerStub appRunnerStub = new ApplicationRunnerStub();
    private List<String> argsList;
    private CallCommand sut;

    /**
     * Tests for calls with no special behavior
     *
     * Compares the expected arguments with the actual arguments that are passed into runApp()
     */
    @Test
    void testEvaluateCallWithArgs() throws Exception {
        argsList = Arrays.asList(APP, ARGS[0], ARGS[1], ARGS[2]);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{ARGS[0], ARGS[1], ARGS[2]}, appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    @Test
    void testEvaluateCallWithNoArgs() throws Exception {
        argsList = Arrays.asList(APP);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    // Defensive check: CallCommand will not receive a null / empty argsList
    @Test
    void testEvaluateEmptyCall() {
        argsList = new LinkedList<>();

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }
}
