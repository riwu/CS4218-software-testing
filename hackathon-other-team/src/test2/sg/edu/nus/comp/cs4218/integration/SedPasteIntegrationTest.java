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

public class SedPasteIntegrationTest {
    private static ShellImpl shell;
    private OutputStream stdout;
    private static final String ORIGINAL = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator();
    private static final String REPLACED_SECOND = "This\tis testt" + System.lineSeparator() + "the\ttest text t" + System.lineSeparator();
    private static final String DIR_NAME = "sedPasteTestDir";
    private static final String FILENAME1 = "sedPasteFile1";
    private static final String FILENAME2 = "sedPasteFile2";
    private File dir;
    private File file1;
    private File file2;


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
    public void whenSedAndPasteExpectPasteSedOutput() throws Exception {
        String argument = "sed s/test/t/2 " + file1.toString() + " | paste";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(REPLACED_SECOND, stdout.toString());
    }

    @Test
    public void whenSedAndPasteWithArgumentExpectPasteFile() throws Exception {
        String argument = "sed s/test/to/2 " + file1.toString() + " | paste " + file2.toString();
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(REPLACED_SECOND, stdout.toString());
    }
}
