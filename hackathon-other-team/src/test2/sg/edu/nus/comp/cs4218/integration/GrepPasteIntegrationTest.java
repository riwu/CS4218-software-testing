package sg.edu.nus.comp.cs4218.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class GrepPasteIntegrationTest {
    private static ShellImpl shell;
    private OutputStream stdout;
    private static final String ORIGINAL = "This is testtest" + System.lineSeparator() + "this was not test" + System.lineSeparator();
    private static final String DIR_NAME = "grepPasteTestDir";
    private static final String FILENAME1 = "grepPasteFile1";
    private File dir;
    private File file1;


    @Before
    public void setUp() throws Exception {
        shell = new ShellImpl();
        String currentDir = Environment.currentDirectory;
        dir = new File(currentDir + File.separator + DIR_NAME);
        dir.mkdir();

        file1 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME1);
        Files.write(file1.toPath(), ORIGINAL.getBytes());

        stdout = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        file1.delete();
        dir.delete();
        stdout.close();
    }

    @Test
    public void whenGrepAndPasteExpectPasteGrepOutput() throws Exception {
        String expected = "This is testtest" + System.lineSeparator();
        String argument = "grep testt " + file1.toString() + " | paste";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }

    @Test
    public void whenGrepAndPasteWithArgumentExpectPasteFile() throws Exception {
        String argument = "grep testt " + file1.toString() + " | paste " + file1.toString();
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(ORIGINAL, stdout.toString());
    }
}
