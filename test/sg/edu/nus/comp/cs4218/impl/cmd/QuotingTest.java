package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.CommandTestUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QuotingTest {

    private void testEchoCommand(String mark, String text, String expected) throws Exception {
        CommandTestUtil.testCommand(expected + System.lineSeparator(), "echo " + mark + text + mark);
    }

    private void testEchoCommand(String mark, String text) throws Exception {
        testEchoCommand(mark, text, text);
    }

    @Test
    public void shouldNotOutputDoubleQuotes() throws Exception {
        testEchoCommand("\"", "Hello World");
    }

    @Test
    public void shouldNotOutputSingleQuotes() throws Exception {
        testEchoCommand("'", "Hello World");
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotIgnoreBackticks() throws Exception {
        testEchoCommand("`", "Hello World");
    }

    @Test
    public void shouldIgnoreGlobingWithSingleQuote() throws Exception {
        testEchoCommand("'", "*");
    }

    @Test
    public void shouldIgnoreGlobingWithDoubleQuote() throws Exception {
        testEchoCommand("\"", "*");
    }

    @Test
    public void shouldOutputBacktickWithSingleQuote() throws Exception {
        testEchoCommand("'", "`");
    }

    @Test(expected = ShellException.class)
    public void shouldNotOutputBacktickWithDoubleQuote() throws Exception {
        testEchoCommand("\"", "`");
    }

    @Test
    public void shouldOutputTabWhenSingleQuote() throws Exception {
        testEchoCommand("'", "\t", "\\t");
    }

    @Test
    public void shouldOutputTabWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", "\t", "\\t");
    }

    @Test
    public void shouldOutputSingleQuoteWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", "'");
    }

    @Test(expected = ShellException.class)
    public void shouldNotOutputDoubleQuoteWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", "\"");
    }

    @Test
    public void shouldOutputDoubleQuoteWhenSingleQuote() throws Exception {
        testEchoCommand("'", "\"");
    }

    @Test(expected = ShellException.class)
    public void shouldNotOutputSingleQuoteWhenSingleQuote() throws Exception {
        testEchoCommand("'", "'");
    }

    @Test
    public void shouldOutputPipeWhenSingleQuote() throws Exception {
        testEchoCommand("'", "|");
    }

    @Test
    public void shouldOutputPipeWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", "|");
    }

    @Test
    public void shouldOutputInputRedirectionWhenSingleQuote() throws Exception {
        testEchoCommand("'", "<");
    }

    @Test
    public void shouldOutputInputRedirectionWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", "<");
    }

    @Test
    public void shouldOutputOutputRedirectionWhenSingleQuote() throws Exception {
        testEchoCommand("'", ">");
    }

    @Test
    public void shouldOutputOutputRedirectionWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", ">");
    }

    @Test
    public void shouldOutputSemicolonWhenSingleQuote() throws Exception {
        testEchoCommand("'", ";");
    }

    @Test
    public void shouldOutputSemicolonWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", ";");
    }

    @Test
    public void shouldOutputSpaceWhenSingleQuote() throws Exception {
        testEchoCommand("'", " ");
    }

    @Test
    public void shouldOutputSpaceWhenDoubleQuote() throws Exception {
        testEchoCommand("\"", " ");
    }

    private void shouldOutputFileContentWhenFileNameWithSpaceSurroundedWithMark(String mark) throws Exception {
        Path file = Paths.get("QuotingTest File");
        String content = "* | < > content; ' ` \"";
        try {
            Files.createFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Files.write(file, content.getBytes());
        CommandTestUtil.testCommand(content, "cat " + mark + file.toString() + mark);
        Files.delete(file);
    }

    @Test
    public void shouldOutputFileContentWhenFileNameWithSpaceSingleQuoted() throws Exception {
        shouldOutputFileContentWhenFileNameWithSpaceSurroundedWithMark("'");
    }

    @Test
    public void shouldOutputFileContentWhenFileNameWithSpaceDoubleQuoted() throws Exception {
        shouldOutputFileContentWhenFileNameWithSpaceSurroundedWithMark("\"");
    }
}
