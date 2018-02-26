package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;

import static org.junit.Assert.*;

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

    @Test
    public void Should_RunCorrectly_When_ValidCommandGiven() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo string_with_pipe | cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_with_pipe"  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test(expected = ShellException.class)
    public void Should_ThrowShellException_When_InvalidCommandGiven() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo string_with_pipe ||| cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);
    }

    @Test
    public void Should_RunCorrectly_When_CommandHasLeadingAndTrailingSpaces() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("            echo string_with_pipe | cat                ");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_with_pipe"  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_RunCorrectly_When_PipeBetweenCommandsHasNoSpaces() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo string_with_pipe|cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_with_pipe" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_RunCorrectly_When_SingleQuoteCommandGiven() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo 'string_with_single_quote' | cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_with_single_quote" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_RunCorrectly_When_DoubleQuoteCommandGiven() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo \"string_with_double_quote\" | cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_with_double_quote" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_RunCorrectly_When_BacktickCommandGiven() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo `echo string_with_backtick` | cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_with_backtick" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_RunCorrectly_When_PipingMultipleCommand() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo string_pass_through_multipipes | cat | cat | cat");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string_pass_through_multipipes" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_EvaluatePipes_When_PipingBacktickArgs() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo \"`echo the_string | cat`\"");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "the_string" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void ShouldNot_EvaluatePipeLiterally_When_PipingSingleQuoteArgs() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo 'echo the_string | cat'");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "echo the_string | cat" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_EvaluatePipeLiterally_When_PipingDoubleQuoteArgs() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo \"echo the_string | cat\"");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "echo the_string | cat" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_EvaluateSingleQuoteLiterally_When_PipingDoubleQuoteArgs() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo \"echo the_string's | cat \"");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "echo the_string's | cat" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void Should_EvaluateQuotesIndependently_When_Parsing() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("echo `echo string1` 'string2' \"string3\"");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "string1 string2 string3" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }
}
