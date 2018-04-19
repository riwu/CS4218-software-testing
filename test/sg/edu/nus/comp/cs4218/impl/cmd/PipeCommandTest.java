package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

public class PipeCommandTest {

    private InputStream inputStream;
    private OutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        this.inputStream = new ByteArrayInputStream("".getBytes());
        this.outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        this.inputStream.close();
        this.outputStream.close();
    }

    // Not using the one in CommandTestUtil as that might be changed to other commands in the future
    // while this tests specifically PipeCommand
    public String getCommandOutput(String command) throws Exception {
        PipeCommand pipeCommand = new PipeCommand(command);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        return pipeCommand.getResultStream().toString();
    }

    @Test
    public void shouldRunCorrectlyWhenValidCommandGiven() throws Exception {
        String expected = "string_with_pipe" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo string_with_pipe | cat"));
    }

    @Test(expected = ShellException.class)
    public void shouldThrowShellExceptionWhenInvalidCommandGiven() throws Exception {
        getCommandOutput("echo string_with_pipe ||| cat");
    }

    @Test
    public void shouldRunCorrectlyWhenCommandHasLeadingAndTrailingSpaces() throws Exception {
        String expected = "string_with_pipe" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("            echo string_with_pipe | cat                "));
    }

    @Test
    public void shouldRunCorrectlyWhenPipeBetweenCommandsHasNoSpaces() throws Exception {
        String expected = "string_with_pipe" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo string_with_pipe|cat"));
    }

    @Test
    public void shouldRunCorrectlyWhenSingleQuoteCommandGiven() throws Exception {
        String expected = "string_with_single_quote" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo 'string_with_single_quote' | cat"));
    }

    @Test
    public void shouldRunCorrectlyWhenDoubleQuoteCommandGiven() throws Exception {
        String expected = "string_with_double_quote" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo \"string_with_double_quote\" | cat"));
    }

    @Test
    public void shouldRunCorrectlyWhenBacktickCommandGiven() throws Exception {
        String expected = "string_with_backtick" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo `echo string_with_backtick` | cat"));
    }

    @Test
    public void shouldRunCorrectlyWhenPipingMultipleCommand() throws Exception {
        String expected = "string_pass_through_multipipes" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo string_pass_through_multipipes | cat | cat | cat"));

    }

    @Test
    public void shouldEvaluatePipesWhenPipingBacktickArgs() throws Exception {
        String expected = "the_string" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo \"`echo the_string | cat`\""));
    }

    @Test
    public void shouldNotEvaluatePipeLiterallyWhenPipingSingleQuoteArgs() throws Exception {
        String expected = "echo the_string | cat" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo 'echo the_string | cat'"));
    }

    @Test
    public void shouldEvaluatePipeLiterallyWhenPipingDoubleQuoteArgs() throws Exception {
        String expected = "echo the_string | cat" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo \"echo the_string | cat\""));
    }

    @Test
    public void shouldEvaluateSingleQuoteLiterallyWhenPipingDoubleQuoteArgs() throws Exception {
        String expected = "echo the_string's | cat" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo \"echo the_string's | cat \""));
    }

    @Test
    public void shouldEvaluateQuotesIndependentlyWhenParsing() throws Exception {
        String expected = "string1 string2 string3" + System.lineSeparator();
        assertEquals(expected, getCommandOutput("echo `echo string1` 'string2' \"string3\""));
    }
}
