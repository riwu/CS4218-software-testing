package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CmdSubstitutionTest {
	private boolean isImplemented = false;
    private static final String FILE_NAME = "CmdSubstitutionTestFile";
    private static final String FILE_CONTENT = FILE_NAME + System.lineSeparator() + "l 2" + System.lineSeparator() + "l3";

    private static void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        writeToFile(Paths.get(FILE_NAME), FILE_CONTENT);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Files.delete(Paths.get(FILE_NAME));
    }

    @Test
    public void shouldReturnFileContentWhenEchoFileName() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat `echo " + FILE_NAME + "`");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void shouldReturnFileContentWhenDoubleQuotedEcho() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat \"`echo " + FILE_NAME + "`\"");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void shouldReturnStringWhenSingleQuotedEcho() throws Exception {
    	Assume.assumeTrue(isImplemented);
        String catCommand = "`cat " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("echo '" + catCommand + "'");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(catCommand, pipeCommand.getResultStream().toString());
    }

    @Test
    public void shouldReturnFileContentsWhenEchoMultipleTimes() throws Exception {
        String echoCommand = "`echo " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("cat " + echoCommand + " " + echoCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT + FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void shouldReturnFileContentsWhenEchoMultipleFiles() throws Exception {
    	Assume.assumeTrue(isImplemented);
        String echoCommand = "`echo " + FILE_NAME + " " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("cat " + echoCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT + FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void shouldReplaceNewLinesWithSpacesWhenSubstituted() throws Exception {
    	Assume.assumeTrue(isImplemented);
        String catCommand = "`cat " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("echo " + catCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT.replace(System.lineSeparator(), " "), pipeCommand.getResultStream().toString());
    }

    @Test
    public void shouldReturnNestedStrWhenGivenNestedCommand() throws Exception {
    	Assume.assumeTrue(isImplemented);
        String catCommand = "cat " + FILE_NAME;
        String echoCommand = "`echo `" + catCommand + "``";
        PipeCommand pipeCommand = new PipeCommand("echo " + echoCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(catCommand, pipeCommand.getResultStream().toString());
    }
}
