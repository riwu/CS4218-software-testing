package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.app.GrepApplication;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.GrepException.GREP_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

@EnableRuleMigrationSupport
public class GrepApplicationTest {
    private final static String[] FOLDERS = {"folder1"};
    private final static String[] FILES = {"file1.txt", "file2.txt", "file3.txt", "empty.txt"};

    // because PMD keep complaining, so i'm doing this
    private static final String STRING_ABC = "abc";
    private static final String STRING_BAC = "bac";
    private static final String STRING_BBC = "bbc";
    private static final String STRING_CCC = "ccc";
    private static final String STRING_CAB = "cab";
    private static final String STRING_DEF = "def";
    private static final String STRING_123 = "123";
    private static final String STRING_456 = "456";
    private static final String STRING_789 = "789";

    private final static String[] CONTENTS = {
            STRING_ABC + STRING_NEWLINE + STRING_BAC + STRING_NEWLINE + STRING_BBC,
            STRING_CCC + STRING_NEWLINE + STRING_CAB + STRING_NEWLINE + STRING_DEF,
            STRING_123 + STRING_NEWLINE + STRING_456 + STRING_NEWLINE + STRING_789,
            ""};

    private GrepApplication grepApp;
    private OutputStream outputStream;
    private String tempFolderAbsPath;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        grepApp = new GrepApplication();
        outputStream = new ByteArrayOutputStream();

        OutputStream fileOutputStream;

        // Create 3 temp files
        for (int i = 0; i < FILES.length; i++) {
            File temp = tempFolder.newFile(FILES[i]);
            fileOutputStream = Files.newOutputStream(temp.toPath());
            fileOutputStream.write(CONTENTS[i].getBytes());
        }

        // Create temp folder
        tempFolder.newFolder(FOLDERS[0]);

        // Set current directory to be that of tempFolder
        tempFolderAbsPath = tempFolder.getRoot().getAbsolutePath();
        Environment.currentDirectory = tempFolderAbsPath;
    }

    /**
     * Tests for grepFromStdin()
     **/
    @Test
    void testStdinInvert() throws GrepException {
        String pattern = "bc";
        String contents = CONTENTS[0];
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String result = grepApp.grepFromStdin(pattern, true, inputStream);
        assertEquals(STRING_BAC + STRING_NEWLINE, result);
    }

    @Test
    void testStdinNoMatch() throws GrepException {
        String pattern = "he";
        String contents = CONTENTS[0];
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String result = grepApp.grepFromStdin(pattern, false, inputStream);
        assertEquals("", result);
    }

    @Test
    void testStdinOneMatch() throws GrepException {
        String pattern = "bb";
        String contents = CONTENTS[0];
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String result = grepApp.grepFromStdin(pattern, false, inputStream);
        assertEquals(STRING_BBC + STRING_NEWLINE, result);
    }

    @Test
    void testStdinMultipleMatch() throws GrepException {
        String pattern = "bc";
        String contents = CONTENTS[0];
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String result = grepApp.grepFromStdin(pattern, false, inputStream);
        assertEquals(STRING_ABC + STRING_NEWLINE + STRING_BBC + STRING_NEWLINE, result);
    }

    @Test
    void testStdinEmptyStream() throws GrepException {
        String pattern = "a";
        String contents = "";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String result = grepApp.grepFromStdin(pattern, false, inputStream);
        assertEquals("", result);
    }

    @Test
    void testStdinNullStream() {
        Throwable exception = assertThrows(GrepException.class, () -> {
            grepApp.grepFromStdin("k", false, null);
        });
        assertEquals(GREP_ERR_PREFIX + ERR_NO_ISTREAM, exception.getMessage());
    }

    @Test
    void testStdinInvalidPattern() {
        String pattern = "[bc";
        String contents = CONTENTS[0];
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        Throwable exception = assertThrows(GrepException.class, () -> {
            grepApp.grepFromStdin(pattern, false, inputStream);
        });
        assertEquals(GREP_ERR_PREFIX + ERR_BAD_REGEX, exception.getMessage());
    }

    /**
     * Tests for grepFromMultipleFiles()
     **/

    @Test
    void testOneRelFileInvert() throws GrepException {
        String pattern = "c";
        String result = grepApp.grepFromMultipleFiles(pattern, true, FILES[1]);
        assertEquals(STRING_DEF + STRING_NEWLINE, result);
    }

    @Test
    void testOneRelFileNoMatch() throws GrepException {
        String pattern = "[0-9]";
        String result = grepApp.grepFromMultipleFiles(pattern, false, FILES[1]);
        assertEquals("", result);
    }

    @Test
    void testOneRelFileMultipleMatch() throws GrepException {
        String pattern = "[74]";
        String result = grepApp.grepFromMultipleFiles(pattern, false, FILES[2]);
        assertEquals(STRING_456 + STRING_NEWLINE + STRING_789 + STRING_NEWLINE, result);
    }

    @Test
    void testMultipleRelFilesMultipleMatch() throws GrepException {
        String pattern = "[a]";
        String result = grepApp.grepFromMultipleFiles(pattern, false, FILES);
        String expected = String.format(FMT_W_FILENAME, FILES[0], STRING_ABC) + STRING_NEWLINE
                + String.format(FMT_W_FILENAME, FILES[0], STRING_BAC) + STRING_NEWLINE
                + String.format(FMT_W_FILENAME, FILES[1], STRING_CAB) + STRING_NEWLINE;
        assertEquals(expected, result);
    }

    @Test
    void testOneFileInvalidPattern() {
        String pattern = "++";
        Throwable exception = assertThrows(GrepException.class, () -> {
            grepApp.grepFromMultipleFiles(pattern, false, FILES[0]);
        });
        assertEquals(GREP_ERR_PREFIX + ERR_BAD_REGEX, exception.getMessage());
    }

    @Test
    void testEmptyRelFile() throws GrepException {
        String pattern = "z";
        String result = grepApp.grepFromMultipleFiles(pattern, false, FILES[3]);
        assertEquals("", result);
    }

    @Test
    void testOneAbsFile() throws GrepException {
        String pattern = "4";
        String result = grepApp
                .grepFromMultipleFiles(pattern, false,
                                       tempFolderAbsPath + CHAR_FILE_SEP + FILES[2]);
        assertEquals(STRING_456 + STRING_NEWLINE, result);
    }

    @Test
    void testCurrDirRelFile() throws GrepException {
        String pattern = "8";
        String result = grepApp.grepFromMultipleFiles(pattern, false,
                                                      STRING_CURR_DIR + CHAR_FILE_SEP +
                                                              STRING_CURR_DIR + CHAR_FILE_SEP +
                                                              FILES[2]);
        assertEquals(STRING_789 + STRING_NEWLINE, result);
    }

    @Test
    void testParentDirRelFile() throws GrepException {
        String pattern = "d";
        String result = grepApp.grepFromMultipleFiles(pattern, false,
                                                      FOLDERS[0] + CHAR_FILE_SEP +
                                                              STRING_PARENT_DIR + CHAR_FILE_SEP +
                                                              FOLDERS[0] + CHAR_FILE_SEP +
                                                              STRING_PARENT_DIR + CHAR_FILE_SEP +
                                                              FILES[1]);
        assertEquals(STRING_DEF + STRING_NEWLINE, result);
    }

    @Test
    void testCurrDirAbsFile() throws GrepException {
        String pattern = "7";
        String result = grepApp
                .grepFromMultipleFiles(pattern, false,
                                       tempFolderAbsPath + CHAR_FILE_SEP + STRING_CURR_DIR +
                                               CHAR_FILE_SEP + STRING_CURR_DIR + CHAR_FILE_SEP +
                                               FILES[2]);
        assertEquals(STRING_789 + STRING_NEWLINE, result);
    }

    @Test
    void testParentDirAbsFile() throws GrepException {
        String pattern = "4";
        String result = grepApp.grepFromMultipleFiles(pattern, false,
                                                      tempFolderAbsPath + CHAR_FILE_SEP +
                                                              FOLDERS[0] + CHAR_FILE_SEP +
                                                              STRING_PARENT_DIR + CHAR_FILE_SEP
                                                              + FOLDERS[0] + CHAR_FILE_SEP +
                                                              STRING_PARENT_DIR + CHAR_FILE_SEP +
                                                              FILES[2]);
        assertEquals(STRING_456 + STRING_NEWLINE, result);
    }

    @Test
    void testFileDoesNotExist() throws GrepException {
        String pattern = "[ab]+";
        String result = grepApp.grepFromMultipleFiles(pattern, false, "foo.txt");
        assertEquals(GREP_ERR_PREFIX + String.format(ERR_NO_SUCH_FILE, "foo.txt") + STRING_NEWLINE,
                     result);
    }

    @Test
    void testDirectoryAsArg() throws GrepException {
        String pattern = "12";
        String result = grepApp
                .grepFromMultipleFiles(pattern, false, String.valueOf(CHAR_FILE_SEP));
        assertEquals(GREP_ERR_PREFIX + String.format(ERR_IS_DIR, CHAR_FILE_SEP) + STRING_NEWLINE,
                     result);
    }

    /**
     * Tests for run()
     **/

    @Test
    void testRunStdin() throws GrepException {
        String[] args = {"-v", "a"};
        String contents = "ah" + STRING_NEWLINE + "he" + STRING_NEWLINE + "ha";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());

        grepApp.run(args, inputStream, outputStream);
        assertEquals("he" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    void testRunFileNames() throws GrepException {
        String[] args = {"[1d]", FILES[0], FILES[1], FILES[2]};
        String expected = String.format(FMT_W_FILENAME, FILES[1], STRING_DEF) + STRING_NEWLINE
                + String.format(FMT_W_FILENAME, FILES[2], STRING_123) + STRING_NEWLINE;

        grepApp.run(args, null, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunNullArgs() {
        Throwable exception = assertThrows(GrepException.class, () -> {
            grepApp.run(null, System.in, outputStream);
        });
        assertEquals(GREP_ERR_PREFIX + ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    void testRunNullOutputStream() {
        Throwable exception = assertThrows(GrepException.class, () -> {
            grepApp.run(new String[0], System.in, null);
        });
        assertEquals(GREP_ERR_PREFIX + ERR_NO_OSTREAM, exception.getMessage());
    }

    @Test
    void testRunNoStdinNoFileNames() {
        Throwable exception = assertThrows(GrepException.class, () -> {
            String[] args = {"[ab]"};
            grepApp.run(args, null, outputStream);
        });
        assertEquals(GREP_ERR_PREFIX + ERR_NO_INPUT, exception.getMessage());
    }
}
