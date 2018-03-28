//package sg.edu.nus.comp.cs4218.impl.cmd;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import sg.edu.nus.comp.cs4218.exception.ShellException;
//
//import java.io.*;
//
//import static org.junit.Assert.*;
//
//public class PipeCommandTest {
//
//    private InputStream inputStream;
//    private OutputStream outputStream;
//
//    @Before
//    public void setUp() throws Exception {
//        this.inputStream = new ByteArrayInputStream("".getBytes());
//        this.outputStream = new ByteArrayOutputStream();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        this.inputStream.close();
//        this.outputStream.close();
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenValidCommandGiven() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo string_with_pipe | cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_with_pipe"  + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test(expected = ShellException.class)
//    public void shouldThrowShellExceptionWhenInvalidCommandGiven() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo string_with_pipe ||| cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenCommandHasLeadingAndTrailingSpaces() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("            echo string_with_pipe | cat                ");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_with_pipe"  + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenPipeBetweenCommandsHasNoSpaces() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo string_with_pipe|cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_with_pipe" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenSingleQuoteCommandGiven() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo 'string_with_single_quote' | cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_with_single_quote" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenDoubleQuoteCommandGiven() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo \"string_with_double_quote\" | cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_with_double_quote" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenBacktickCommandGiven() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo `echo string_with_backtick` | cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_with_backtick" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldRunCorrectlyWhenPipingMultipleCommand() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo string_pass_through_multipipes | cat | cat | cat");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string_pass_through_multipipes" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldEvaluatePipesWhenPipingBacktickArgs() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo \"`echo the_string | cat`\"");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "the_string" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldNotEvaluatePipeLiterallyWhenPipingSingleQuoteArgs() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo 'echo the_string | cat'");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "echo the_string | cat" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldEvaluatePipeLiterallyWhenPipingDoubleQuoteArgs() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo \"echo the_string | cat\"");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "echo the_string | cat" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldEvaluateSingleQuoteLiterallyWhenPipingDoubleQuoteArgs() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo \"echo the_string's | cat \"");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "echo the_string's | cat" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//
//    @Test
//    public void shouldEvaluateQuotesIndependentlyWhenParsing() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo `echo string1` 'string2' \"string3\"");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = "string1 string2 string3" + System.lineSeparator();
//        String evaluated = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, evaluated);
//    }
//}
