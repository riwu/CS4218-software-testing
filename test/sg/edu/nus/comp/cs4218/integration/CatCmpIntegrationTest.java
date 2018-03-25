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

public class CatCmpIntegrationTest {
    private static ShellImpl shell;
    private static final String ORIGINAL = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator();
    private static final String REPLACED_SECOND = "This\tis testt" + System.lineSeparator() + "the\ttest text t" + System.lineSeparator();
    private static final String DIR_NAME = "catCmpTestDir";
    private static final String FILENAME1 = "catCmpFile1";
    private static final String FILENAME2 = "catCmpFile2";
    private File dir;
    private File file1;
    private File file2;
    private OutputStream stdout;


    @Before
    public void setUp() throws Exception {
        shell = new ShellImpl();
        String currentDir = Environment.currentDirectory;
        dir = new File(currentDir + File.separator + DIR_NAME);
        dir.mkdir();

        file1 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME1);
        Files.write(file1.toPath(), ORIGINAL.getBytes());
        file2 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME2);
        Files.write(file2.toPath(), REPLACED_SECOND.getBytes());
        stdout = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        file1.delete();
        file2.delete();
        dir.delete();
        stdout.close();
    }

    @Test
    public void whenCatAndCmpDiffFileExpectDiffer() throws Exception {
        String expected = file2.toString() + " - differ: char 14, line 1" + System.lineSeparator();
        String argument = "cat " + file1.toString() + " | cmp - " + file2.toString();
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }

    @Test
    public void whenCatAndCmpSameFileExpectDiffer() throws Exception {
        String expected = "";
        String argument = "cat " + file1.toString() + " | cmp - " + file1.toString();
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }
}
