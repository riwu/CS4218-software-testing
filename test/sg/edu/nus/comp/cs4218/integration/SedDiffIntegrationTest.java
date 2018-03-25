package sg.edu.nus.comp.cs4218.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class SedDiffIntegrationTest {
    private static final ShellImpl shell = new ShellImpl();
    private static final String ORIGINAL = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator();
    private static final String REPLACED_SECOND = "This\tis testt" + System.lineSeparator() + "the\ttest text t" + System.lineSeparator();
    private static final String DIR_NAME = "sedTestDir";
    private static final String FILENAME1 = "sedFile1";
    private static final String FILENAME2 = "sedFile2";
    private File dir;
    private File file1;
    private File file2;
    private InputStream stdin;
    private OutputStream stdout;


    @Before
    public void setUp() throws Exception {
        String currentDir = Environment.currentDirectory;
        dir = new File(currentDir + File.separator + DIR_NAME);
        dir.mkdir();

        file1 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME1);
        Files.write(file1.toPath(), ORIGINAL.getBytes());
        file2 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME2);

        stdin = new ByteArrayInputStream(ORIGINAL.getBytes());
        stdout = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        stdin.close();
        file1.delete();
        file2.delete();
        dir.delete();
        stdin.close();
        stdout.close();
    }

    @Test
    public void whenReplaceOriginalExpectDiff() throws Exception {
        String expected = "< This\tis testtest\n> This\tis test";
        String argument = "sed s/is test/is / " + file1.toString() + " | diff " + file1.toString() + " -";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }

    @Test
    public void whenReplaceOriginalWithqFlagExpectDiff() throws Exception {
        String expected = "Files " + file1.toString() + " and - differ";
        String argument = "sed s/is test/is / " + file1.toString() + " | diff -q " + file1.toString() + " -";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }

    @Test
    public void whenReplaceIsSameWithsFlagExpectIdentical() throws Exception {
        String expected = "Files " + file2.toString() + " and - are identical";
        Files.write(file2.toPath(), REPLACED_SECOND.getBytes());
        String argument = "sed s/test/t/2 " + file1.toString() + " | diff -s " + file2.toString() + " -";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }
}
