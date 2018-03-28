package sg.edu.nus.comp.cs4218.test.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.DiffApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.test.FileSystemTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DiffApplicationTest extends FileSystemTest {
    private final static String TOKEN_STDIN = "-";
    private final static String FILENAME_A = "fileA";
    private final static String FILENAME_B = "fileB";
    private final static String DIRECTORY_A = "dirA";
    private final static String DIRECTORY_B = "dirB";
    private final static String[] DIR_A_CONTENT = { "1", "2", "3", "4" };
    private final static String[] DIR_B_CONTENT = { "1", "3", "5", "6" };
    private final static String OUTPUT_FORMAT = "Only in %s: %s";
    private final static String OUTPUT_SIMPLE = "Files %s and %s differ";
    private final static String OUTPUT_SAME = "Files %s %s are identical";

    private String contentA;
    private String contentB;
    private String expected;

    private File dirA;
    private File dirB;

    private DiffApplication sut;
    private InputStream inputStream = null;
    private final OutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() throws Exception {
        Environment.currentDirectory = tempFolder.getRoot().getAbsolutePath();
        dirA = tempFolder.newFolder(DIRECTORY_A);
        dirB = tempFolder.newFolder(DIRECTORY_B);

        sut = new DiffApplication();
    }

    @Test
    void runTwoFilesTest0() throws Exception {
        createMultilineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        createTestFileWithContents(FILENAME_B, contentB, true);

        String[] args = { FILENAME_A, FILENAME_B };
        sut.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void runFileAndStdinTest0() throws Exception {
        createMultilineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        byte[] buffer = contentB.getBytes();
        inputStream = new ByteArrayInputStream(buffer);

        String[] args = { FILENAME_A, TOKEN_STDIN };
        sut.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void runDirectoryTest0() throws Exception {
        populateDirs();

        String[] args = { DIRECTORY_A, DIRECTORY_B };
        sut.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void emptyFilesTest() throws Exception {
        createTestFileWithContents(FILENAME_A, "", true);
        createTestFileWithContents(FILENAME_B, "", true);

        String result;
        result = sut.diffTwoFiles(FILENAME_A, FILENAME_B,
                                  false, false, false);
        assertEquals("", result);
    }

    @Test
    void twoFilesTest() throws Exception {
        createMultilineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        createTestFileWithContents(FILENAME_B, contentB, true);

        String result = sut.diffTwoFiles(FILENAME_A, FILENAME_B,
                                         false, false, false);
        assertEquals(expected, result);
    }

    @Test
    void noBlankLinesTest() throws Exception {
        createMultilineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        createTestFileWithContents(FILENAME_B, contentB, true);

        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append("<line2")
                       .append(StringUtils.STRING_NEWLINE)
                       .append(">line6");

        expected = expectedBuilder.toString();

        String result = sut.diffTwoFiles(FILENAME_A, FILENAME_B,
                                     false, true, false);

        assertEquals(expected, result);
    }

    @Test
    void isSimpleTest() throws Exception {
        createMultilineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        createTestFileWithContents(FILENAME_B, contentB, true);

        expected = String.format(OUTPUT_SIMPLE, FILENAME_A, FILENAME_B);

        String result = sut.diffTwoFiles(FILENAME_A, FILENAME_B,
                                         false, false, true);
        assertEquals(expected, result);
    }

    @Test
    void isShowSameTest() throws Exception {
        createSameTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        createTestFileWithContents(FILENAME_B, contentB, true);

        expected = String.format(OUTPUT_SAME, FILENAME_A, FILENAME_B);

        String result = sut.diffTwoFiles(FILENAME_A, FILENAME_B,
                                         true, false, false);
        assertEquals(expected, result);
    }

    @Test
    void twoSingleLineFilesTest() throws Exception {
        createSingleLineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        createTestFileWithContents(FILENAME_B, contentB, true);

        String result = sut.diffTwoFiles(FILENAME_A, FILENAME_B,
                                         false, false, false);
        assertEquals(expected, result);
    }

    @Test
    void emptyStdinAndFileTest() throws Exception {
        createTestFileWithContents(FILENAME_A, "", true);
        byte[] buffer = "".getBytes();
        inputStream = new ByteArrayInputStream(buffer);

        String result = sut.diffFileAndStdin(FILENAME_A, inputStream, false,
                                             false, false);


        assertEquals("", result);
    }

    @Test
    void stdinAndFileTest() throws Exception {
        createMultilineTestContents();
        createTestFileWithContents(FILENAME_A, contentA, true);
        byte[] buffer = contentB.getBytes();
        inputStream = new ByteArrayInputStream(buffer);

        String result = sut.diffFileAndStdin(FILENAME_A, inputStream, false,
                                             false, false);
        assertEquals(expected, result);
    }

    @Test
    void twoEmptyDirsTest() throws Exception {
        expected = "";
        String result = sut.diffTwoDir(DIRECTORY_A, DIRECTORY_B, false,
                                       false, false);
        assertEquals(expected, result);
    }

    @Test
    void twoDirsTest() throws Exception {
        populateDirs();
        String result = sut.diffTwoDir(DIRECTORY_A, DIRECTORY_B, false,
                                       false, false);
        assertEquals(expected, result);
    }

    private void createSameTestContents() {
        String content = "same";
        StringBuilder builder = new StringBuilder(256);
        builder.append(content)
                .append(StringUtils.STRING_NEWLINE)
                .append(content)
                .append(StringUtils.STRING_NEWLINE)
                .append(content)
                .append(StringUtils.STRING_NEWLINE)
                .append(content);

        contentA = builder.toString();
        contentB = builder.toString();

        expected = "";
    }

    private void createMultilineTestContents() {
        StringBuilder builderA = new StringBuilder(256);
        builderA.append("line1")
                .append(StringUtils.STRING_NEWLINE)
                .append("") // Blank line
                .append(StringUtils.STRING_NEWLINE)
                .append("line2")
                .append(StringUtils.STRING_NEWLINE)
                .append("line3")
                .append(StringUtils.STRING_NEWLINE)
                .append("line5");

        StringBuilder builderB = new StringBuilder(256);
        builderB.append("line1")
                .append(StringUtils.STRING_NEWLINE)
                .append("line3")
                .append(StringUtils.STRING_NEWLINE)
                .append("") // Blank line
                .append(StringUtils.STRING_NEWLINE)
                .append("line5")
                .append(StringUtils.STRING_NEWLINE)
                .append("line6");

        contentA = builderA.toString();
        contentB = builderB.toString();

        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append('<')
                       .append(StringUtils.STRING_NEWLINE)
                       .append("<line2")
                       .append(StringUtils.STRING_NEWLINE)
                       .append('>')
                       .append(StringUtils.STRING_NEWLINE)
                       .append(">line6");

        expected = expectedBuilder.toString();
    }

    private void createSingleLineTestContents() {
        contentA = "1235";
        contentB = "1356";

        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append("<1235")
                .append(StringUtils.STRING_NEWLINE)
                .append(">1356");

        expected = expectedBuilder.toString();
    }

    private void populateDirs() throws Exception {
        String path;

        path = dirA.getAbsolutePath();
        for (String content : DIR_A_CONTENT) {
            Files.createFile(Paths.get(path, content));
        }

        path = dirB.getAbsolutePath();
        for (String content: DIR_B_CONTENT) {
            Files.createFile(Paths.get(path, content));
        }

        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append(String.format(OUTPUT_FORMAT, DIRECTORY_A, "2"))
                       .append(StringUtils.STRING_NEWLINE)
                       .append(String.format(OUTPUT_FORMAT, DIRECTORY_A, "4"))
                       .append(StringUtils.STRING_NEWLINE)
                       .append(String.format(OUTPUT_FORMAT, DIRECTORY_B, "5"))
                       .append(StringUtils.STRING_NEWLINE)
                       .append(String.format(OUTPUT_FORMAT, DIRECTORY_B, "6"));

        expected = expectedBuilder.toString();
    }
}
