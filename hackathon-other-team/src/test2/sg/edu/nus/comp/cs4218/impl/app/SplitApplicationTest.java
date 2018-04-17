package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SplitException;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SplitApplicationTest {
    private static final String XAC = "xac";
    private static final String XAB = "xab";
    private static final String XAA = "xaa";
    private SplitApplication splitApplication = new SplitApplication();
    private static final String DEFAULT_DIR = Environment.currentDirectory;
    private static final String CURRENT_DIR = Environment.currentDirectory + File.separator + "SplitApplicationTest";
    private static final String RESOURCE_FOLDER = "testresource";
    private static final String FILENAME = CURRENT_DIR + File.separator + "newfile";
    private File testDir;
    private File file;
    boolean isImplemented = false;

    @Before
    public void setUp() throws Exception {
        Environment.currentDirectory = CURRENT_DIR;
        testDir = new File(CURRENT_DIR);
        testDir.mkdir();
        file = new File(FILENAME);
    }

    @After
    public void tearDown() throws Exception {
        for (File file : testDir.listFiles()) {
            file.delete();
        }
        testDir.delete();
        Environment.currentDirectory = DEFAULT_DIR;
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    private String generateString(int lines) {
        String str = "a" + System.lineSeparator();
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < lines; i++) {
            strBuilder.append(str);
        }

        return strBuilder.toString().trim();
    }

    private String generateStringByte(int bytes) {
        String str = "a";
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < bytes; i++) {
            strBuilder.append(str);
        }

        return strBuilder.toString();
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNoStdout() throws Exception {
        String[] args = {FILENAME};
        splitApplication.run(args, null, null);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNoInput() throws Exception {
        splitApplication.run(new String[]{}, null, null);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenInputDir() throws Exception {
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

        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File nonExistentFile = new File(CURRENT_DIR + File.separator + XAB);
        assertTrue(firstFile.exists());
        assertFalse(nonExistentFile.exists());
        firstFile.delete();


        inputString = generateString(1000);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto2FilesWhenInputLineBetween1kAnd2k() throws Exception {
        String inputString = generateString(1001);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File secondFile = new File(CURRENT_DIR + File.separator + XAB);
        File nonExistentFile = new File(CURRENT_DIR + File.separator + XAC);
        assertTrue(firstFile.exists());
        assertTrue(secondFile.exists());
        assertFalse(nonExistentFile.exists());
        firstFile.delete();
        secondFile.delete();


        inputString = generateString(2000);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(secondFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto5FilesWhen10LineInput() throws Exception {
        String inputString = generateString(10);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {"-l", "2", FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "xae");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "xaf");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }


    @Test
    public void shouldSplitInto4FilesWhen10LineInput() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "xad");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "xae");

        String inputString = generateString(10);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-l", "3", FILENAME};
        splitApplication.run(args, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldHavexbaNameWhenMoreThan26Line() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "xba");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "xbb");

        String inputString = generateString(27);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-l", "1", FILENAME};
        splitApplication.run(args, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldHavezaaNameWhenMoreThan676Line() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "x~aaa");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "x~aab");

        String inputString = generateString(677);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-l", "1", FILENAME};
        splitApplication.run(args, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto2FilesWhen10ByteInput() throws Exception {
        String inputString = generateStringByte(10);
        Files.write(file.toPath(), inputString.getBytes());

        String[] args = {"-b", "5", FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + XAB);
        File nonExistentFile = new File(CURRENT_DIR + File.separator + XAC);
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto4FilesWhen10ByteInput() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "xad");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "xae");

        String inputString = generateStringByte(10);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {"-b", "3", FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldHavexbaNameWhenMoreThan26Byte() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "xba");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "xbb");

        String inputString = generateStringByte(27);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-b", "1", FILENAME};
        splitApplication.run(args, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldHavezaaNameWhenMoreThan676Byte() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + "x~aaa");
        File nonExistentFile = new File(CURRENT_DIR + File.separator + "x~aab");

        String inputString = generateStringByte(677);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-b", "1", FILENAME};
        splitApplication.run(args, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto2FilesWhen1bInput() throws Exception {
        String inputString = generateStringByte(1024);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args = {"-b", "1b", FILENAME};
        splitApplication.run(args, null, System.out);

        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + XAB);
        File nonExistentFile = new File(CURRENT_DIR + File.separator + XAC);
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(513);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args1 = {"-b", "1b", FILENAME};
        splitApplication.run(args1, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto2FilesWhen1kInput() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + XAB);
        File nonExistentFile = new File(CURRENT_DIR + File.separator + XAC);

        String inputString = generateStringByte(2048);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args2 = {"-b", "1k", FILENAME};
        splitApplication.run(args2, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());

        inputString = generateStringByte(1025);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args3 = {"-b", "1k", FILENAME};
        splitApplication.run(args3, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }

    @Test
    public void shouldSplitInto2FilesWhen1mInput() throws Exception {
        File firstFile = new File(CURRENT_DIR + File.separator + XAA);
        File lastFile = new File(CURRENT_DIR + File.separator + XAB);
        File nonExistentFile = new File(CURRENT_DIR + File.separator + XAC);

        String inputString = generateStringByte(2097152);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args4 = {"-b", "1m", FILENAME};
        splitApplication.run(args4, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
        firstFile.delete();
        lastFile.delete();

        inputString = generateStringByte(1048577);
        Files.write(file.toPath(), inputString.getBytes());
        String[] args5 = {"-b", "1m", FILENAME};
        splitApplication.run(args5, null, System.out);

        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistentFile.exists());
    }
}
