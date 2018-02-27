package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CatApplicationTest {

    private CatApplication catApplication = new CatApplication();
    private static Path EMPTY_FILE = Paths.get("empty");
    private static Path FILE_1 = Paths.get("CatApplicationTest1file 1");
    private static Path FILE_2 = Paths.get("CatApplicationTest2FILE_2");
    private static Path FILE_WITH_SPACE = Paths.get("CatApplicationTest3  file- 3");
    private static Path NON_EXISTENT_FILE = Paths.get("CatApplicationTestnon-existent");
    private static String FILE_1_CONTENT = "FILE_1-1" + System.lineSeparator() + "FILE_1-2";
    private static String FILE_2_CONTENT = "FILE_2-1" + System.lineSeparator();
    private static String FILE_WITH_SPACE_CONTENT = "FILE_WITH_SPACE-1" + System.lineSeparator() + "FILE_WITH_SPACE-2" + System.lineSeparator();

    private OutputStream outputStream;

    private void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @Before
    public void setUp() throws Exception {
        Files.createFile(EMPTY_FILE);
        writeToFile(FILE_1, FILE_1_CONTENT);
        writeToFile(FILE_2, FILE_2_CONTENT);
        writeToFile(FILE_WITH_SPACE, FILE_WITH_SPACE_CONTENT);
        outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(EMPTY_FILE);
        Files.delete(FILE_1);
        Files.delete(FILE_2);
        Files.delete(FILE_WITH_SPACE);
    }

    @Test
    public void Should_ReturnEmptyString_When_GivenEmptyFile() throws CatException {
        assertEquals("", new String(catApplication.getContent(EMPTY_FILE)));
    }

    @Test
    public void Should_ReturnFileContent_When_GivenFileWithContent() throws CatException {
        assertEquals(FILE_1_CONTENT, new String(catApplication.getContent(FILE_1)));
    }

    @Test(expected = CatException.class)
    public void Should_ThrowException_When_GivenNonExistingFile() throws CatException {
        catApplication.getContent(Paths.get("Non-existent"));
    }

    @Test
    public void Should_ReadFromStdin_When_NoFiles() throws CatException {
        InputStream inputStream = new ByteArrayInputStream(FILE_1_CONTENT.getBytes(StandardCharsets.UTF_8));
        catApplication.run(new String[]{}, inputStream, outputStream);
        assertEquals(FILE_1_CONTENT, outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContent_When_GivenFileName() throws CatException {
        catApplication.run(new String[]{FILE_1.toString()}, System.in, outputStream);
        assertEquals(FILE_1_CONTENT, outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContents_When_GivenFileNames() throws CatException {
        catApplication.run(new String[]{FILE_1.toString(), FILE_2.toString(), FILE_WITH_SPACE.toString()}, System.in, outputStream);
        assertEquals(FILE_1_CONTENT + FILE_2_CONTENT + FILE_WITH_SPACE_CONTENT, outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContentsInCorrectOrder_When_GivenFileNames() throws CatException {
        catApplication.run(new String[]{FILE_WITH_SPACE.toString(), FILE_2.toString()}, System.in, outputStream);
        assertEquals(FILE_WITH_SPACE_CONTENT + FILE_2_CONTENT, outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContent_When_RedirectedInput() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat < " + FILE_2.toString());
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_2_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test(expected = ShellException.class)
    public void Should_ThrowException_When_InputFileDoesNotExist() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat " + " < " + NON_EXISTENT_FILE.toString());
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
    }

    @Test
    public void Should_OutputToFile_When_RedirectedOutput() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat " + FILE_2.toString() + " > " + FILE_1.toString());
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_2_CONTENT, new String(catApplication.getContent(FILE_1)));
    }

    @Test
    public void Should_CreateAndOutputToFile_When_RedirectedOutputDoesNotExist() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat " + FILE_2.toString() + " > " + NON_EXISTENT_FILE.toString());
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_2_CONTENT, new String(catApplication.getContent(NON_EXISTENT_FILE)));
        Files.delete(NON_EXISTENT_FILE);
    }

    @Test
    public void Should_ReturnEmptyResult_When_SpaceInFileName() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat " + FILE_1.toString());
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals("", pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnFileContent_When_DoubleQuotedFileNameWithSpace() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat \"" + FILE_WITH_SPACE.toString() + "\"");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_WITH_SPACE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnFileContent_When_SingleQuotedFileNameWithSpace() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat '" + FILE_WITH_SPACE.toString() + "'");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_WITH_SPACE_CONTENT, pipeCommand.getResultStream().toString());
    }

    @Test
    public void Should_ReturnFileContent_When_CommandSubstituted() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("cat `echo " + FILE_1.toString() + "`");
        pipeCommand.parse();
        pipeCommand.evaluate(System.in, new ByteArrayOutputStream());
        assertEquals(FILE_1_CONTENT, pipeCommand.getResultStream().toString());
    }
}
