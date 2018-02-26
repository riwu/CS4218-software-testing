package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SplitException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SplitApplicationTest {
    private SplitApplication splitApplication = new SplitApplication();
    private static String CURRENT_DIR;
    private static String RESOURCE_FOLDER = "testresource";
    private static String FILENAME = CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "newfile";
    private File testDir = null;
    private File file = null;

    @Before
    public void setUp() throws Exception {
        CURRENT_DIR = Environment.currentDirectory;
        testDir = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER);
        testDir.mkdir();
        file = new File(FILENAME);
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
        testDir.delete();
    }

    private String generateString(int lines) {
        String s = "a" + System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<lines; i++) {
            sb.append(s);
        }

        return sb.toString().trim();
    }

    private String generateStringByte(int bytes) {
        String s = "a";
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<bytes; i++) {
            sb.append(s);
        }

        return sb.toString();
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNoStdout() throws Exception {
        String[] args = {FILENAME};
        splitApplication.run(args, null, null);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNoInput() throws Exception {
        splitApplication.run(null, null, null);
    }

    @Test(expected = SplitException.class)
    public void Should_ThrowSplitException_When_InputDir() throws Exception {
        String[] args = {RESOURCE_FOLDER};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenCapitalLOption() throws Exception {
        String[] args = {"-L", "1", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenCapitalBOption() throws Exception {
        String[] args = {"-B", "1", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenlbOption() throws Exception {
        String[] args = {"-b", "1", "-l", "1", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenEmptyLOption() throws Exception {
        String[] args = {"-l", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenEmptyBOption() throws Exception {
        String[] args1 = {"-b", FILENAME};
        splitApplication.run(args1, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenInvalidLOption() throws Exception {
        String[] args2 = {"-l", "x", FILENAME};
        splitApplication.run(args2, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenInvalidBOption() throws Exception {
        String[] args3 = {"-b", "1g", FILENAME};
        splitApplication.run(args3, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenZeroByteOption() throws Exception {
        String[] args = {"-b", "0", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenZeroLineOption() throws Exception {
        String[] args = {"-l", "0", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNegativeByteOption() throws Exception {
        String[] args = {"-b", "-5", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNegativeLineOption() throws Exception {
        String[] args = {"-l", "-5", FILENAME};
        splitApplication.run(args, null, System.out);
    }
    
    @Test
    public void shouldSplitInto1FilesWhenInputLineLessThan1001() throws Exception {
        String inputString = generateString(1);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xaa");
        File nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xab");
        assertTrue(firstFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();


        inputString = generateString(1000);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
    }

    @Test
    public void shouldSplitInto2FilesWhenInputLineBetween1kAnd2k() throws Exception {
        String inputString = generateString(1001);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xaa");
        File secondFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xab");
        File thirdFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xac");
        assertTrue(firstFile.exists());
        assertTrue(secondFile.exists());
        assertFalse(thirdFile.exists());
        firstFile.delete();
        secondFile.delete();


        inputString = generateString(2000);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(secondFile.exists());
        assertFalse(thirdFile.exists());
    }


    @Test
    public void shouldSplitIntoMultipleFilesWhenCorrectLineInput() throws Exception {
        String inputString = generateString(10);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {"-l", "2", FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xaa");
        File lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xae");
        File nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xaf");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateString(10);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {"-l", "3", FILENAME};
        splitApplication.run(args1, null, System.out);

        lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xad");
        nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xae");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateString(27);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args2 = {"-l", "1", FILENAME};
        splitApplication.run(args2, null, System.out);

        lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xba");
        nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xbb");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateString(677);
        Files.write(file.toPath(), inputString.getBytes());
        splitApplication.run(args2, null, System.out);

        lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "zaa");
        nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "zab");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();
    }

    @Test
    public void shouldSplitIntoMultipleFilesWhenCorrectByteInput() throws Exception {
        String inputString = generateStringByte(10);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {"-b", "5", FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xaa");
        File lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xab");
        File nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xac");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(10);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {"-l", "3", FILENAME};
        splitApplication.run(args1, null, System.out);

        lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xad");
        nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xae");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(27);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args2 = {"-l", "1", FILENAME};
        splitApplication.run(args2, null, System.out);

        lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xba");
        nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xbb");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(677);
        Files.write(file.toPath(), inputString.getBytes());
        splitApplication.run(args2, null, System.out);

        lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "zaa");
        nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "zab");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();
    }

    @Test
    public void shouldSplitIntoMultipleFilesWhenCorrectByteTypeInput() throws Exception {
        String inputString = generateStringByte(1024);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-b", "1b", FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xaa");
        File lastFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xab");
        File nonExistantFile = new File(CURRENT_DIR + File.separator + RESOURCE_FOLDER + File.separator + "xac");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(513);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {"-l", "1b", FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(2048);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args2 = {"-l", "1k", FILENAME};
        splitApplication.run(args2, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(1025);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args3 = {"-l", "1k", FILENAME};
        splitApplication.run(args3, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(2097152);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args4 = {"-l", "1m", FILENAME};
        splitApplication.run(args4, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(1048577);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args5 = {"-l", "1m", FILENAME};
        splitApplication.run(args5, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();
    }
}
