package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CmdSubstitutionTest {

    private static String FILE_NAME = "CmdSubstitutionTestFile";
    private static String FILE_CONTENT = FILE_NAME + System.lineSeparator() + "l 2" + System.lineSeparator() + "l3";

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
    public void Should_ReturnFileContent_When_EchoFileName() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat `echo " + FILE_NAME + "`");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnFileContent_When_DoubleQuotedEcho() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat \"`echo " + FILE_NAME + "`\"");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnString_When_SingleQuotedEcho() throws Exception {
        String catCommand = "`cat " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("echo '" + catCommand + "'");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(catCommand, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnFileContents_When_EchoMultipleTimes() throws Exception {
        String echoCommand = "`echo " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("cat " + echoCommand + " " + echoCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT + FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnFileContents_When_EchoMultipleFiles() throws Exception {
        String echoCommand = "`echo " + FILE_NAME + " " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("cat " + echoCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT + FILE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReplaceNewLinesWithSpaces_When_Substituted() throws Exception {
        String catCommand = "`cat " + FILE_NAME + "`";
        PipeCommand pipeCommand = new PipeCommand("echo " + catCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_CONTENT.replace(System.lineSeparator(), " "), pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnNestedStr_When_GivenNestedCommand() throws Exception {
        String catCommand = "cat " + FILE_NAME;
        String echoCommand = "`echo `" + catCommand + "``";
        PipeCommand pipeCommand = new PipeCommand("echo " + echoCommand);
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(catCommand, pipeCommand.getResultStream().toString());
    }
}
