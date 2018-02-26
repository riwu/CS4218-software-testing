package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class SemicolonTest {

    private static String FILE_CONTENT = "file1content";
    private static Path FILE = Paths.get("file1");
    private static Path NON_EXISTENT_FILE = Paths.get("non-existent");

    private void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @Before
    public void setUp() throws Exception {
        writeToFile(FILE, FILE_CONTENT);
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(FILE);
    }


    @Test
    public void Should_ExecuteCommands_When_NoSpacesNearSemicolon() throws Exception {
        Shell shell = new ShellImpl();
        OutputStream outputStream = new ByteArrayOutputStream();
        String fileContent = "echoToFileContent";
        shell.parseAndEvaluate("echo " + fileContent + " > " + FILE.toString() + ";cat " + FILE.toString(),
                outputStream);
        assertEquals(fileContent, outputStream.toString());
    }

    @Test
    public void Should_ExecuteCommands_When_SpacesNearSemicolon() throws Exception {
        Shell shell = new ShellImpl();
        OutputStream outputStream = new ByteArrayOutputStream();
        String fileContent = "echoToFileContent";
        shell.parseAndEvaluate("echo " + fileContent + " > " + FILE.toString() + " ; cat " + FILE.toString(),
                outputStream);
        assertEquals(fileContent, outputStream.toString());
    }

    @Test
    public void Should_ExecuteSecondCommand_When_FirstCommandFailed() throws Exception {
        Shell shell = new ShellImpl();
        OutputStream outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate("cat " + NON_EXISTENT_FILE.toString() + ";cat " + FILE.toString(),
                outputStream);
        assertEquals(FILE_CONTENT, outputStream.toString());
    }
}
