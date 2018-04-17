package sg.edu.nus.comp.cs4218.test.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;
import sg.edu.nus.comp.cs4218.impl.app.EchoApplication;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.app.EchoApplication.ERR_NO_STREAM;
import static sg.edu.nus.comp.cs4218.impl.app.EchoApplication.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class EchoApplicationTest {
    private EchoApplication echoApp;
    private OutputStream outputStream;

    @BeforeEach
    public void setUp() {
        echoApp = new EchoApplication();
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests for constructResult()
     **/
    @Test
    public void testConstructResultNoArgs() throws EchoException {
        String[] args = new String[0];
        String result = echoApp.constructResult(args);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void testConstructResultOneArg() throws EchoException {
        String[] args = {"arg1"};
        String result = echoApp.constructResult(args);
        assertEquals("arg1" + STRING_NEWLINE, result);
    }

    @Test
    public void testConstructResultMultipleArgs() throws EchoException {
        String[] args = {"arg1", "arg2", "arg3"};
        String result = echoApp.constructResult(args);
        assertEquals("arg1 arg2 arg3" + STRING_NEWLINE, result);
    }

    @Test
    public void testConstructResultArgsWithSpace() throws EchoException {
        String[] args = {"   arg1 ", "arg2", "ar  g3  "};
        String result = echoApp.constructResult(args);
        assertEquals("   arg1  arg2 ar  g3  " + STRING_NEWLINE, result);
    }

    @Test
    public void testConstructResultNullArgs() throws EchoException {
        Throwable exception = assertThrows(EchoException.class, () -> {
            String[] args = null;
            echoApp.constructResult(args);
        });
        assertEquals("echo: " + ERR_NULL_ARGS, exception.getMessage());
    }

    /**
     * Tests for run()
     **/
    @Test
    public void testRunNoArgs() throws EchoException {
        String[] args = new String[0];
        echoApp.run(args, System.in, outputStream);
        assertEquals(STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testRunOneArg() throws EchoException {
        String[] args = {"test1"};
        echoApp.run(args, System.in, outputStream);
        assertEquals("test1" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    void testRunMultipleArgs() throws EchoException {
        String[] args = {"test1", "test2", "test3", "test4"};
        echoApp.run(args, System.in, outputStream);
        assertEquals("test1 test2 test3 test4" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testRunArgsWithSpace() throws EchoException {
        String[] args = {"   arg1 ", "arg2", "ar  g3  "};
        echoApp.run(args, System.in, outputStream);
        assertEquals("   arg1  arg2 ar  g3  " + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testRunNullArgs() {
        Throwable exception = assertThrows(EchoException.class, () -> {
            echoApp.run(null, System.in, outputStream);
        });
        assertEquals("echo: " + ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testRunNullOutputStream() {
        Throwable exception = assertThrows(EchoException.class, () -> {
            echoApp.run(new String[0], System.in, null);
        });
        assertEquals("echo: " + ERR_NO_STREAM, exception.getMessage());
    }
}

