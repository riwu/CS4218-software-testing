package sg.edu.nus.comp.cs4218.test.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.app.CatApplication;
import sg.edu.nus.comp.cs4218.test.TestEnvironment;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.CatException.CAT_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.CatApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

class CatApplicationTest extends TestEnvironment {
    private static final String TEST_DIR = "cat-test";

    private CatApplication sut;
    private final OutputStream outputStream = new ByteArrayOutputStream();
    private InputStream inputStream = null;
    private String tempFolderAbsPath;

    // Folder, filename and content for each file type
    private static final String FOLDER = "folder";

    private static final String FILENAME_VALID_1 = "valid1.txt";
    private static final String FILENAME_VALID_2 = "valid2.txt";
    private static final String FILENAME_INVALID = "invalid.txt";
    private static final String FILENAME_UNREAD = "unreadable.txt";
    private static final String FILENAME_EMPTY = "empty.txt";

    private static final String CONTENT_VALID_1 = "Content from valid1.txt" + STRING_NEWLINE;
    private static final String CONTENT_VALID_2 = "Content from valid2.txt" + STRING_NEWLINE;
    private static final String CONTENT_STDIN = "From stdin.";

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        sut = new CatApplication();

        // Set unreadable file permissions
        Path unreadablePath = Paths.get(tempFolder.getRoot().getAbsolutePath(), ENV_ROOT, TEST_DIR,
                                        FILENAME_UNREAD);
        File unreadable = new File(unreadablePath.toString());
        unreadable.setReadable(false);

        // Set current directory to TEST_DIR
        tempFolderAbsPath = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                      ENV_ROOT,
                                      TEST_DIR).toString();
        Environment.currentDirectory = tempFolderAbsPath;
    }

    /**
     * Basic test for a normal usage of `cat`.
     *
     * Command executed:
     * - `cat a.txt`
     *
     * @throws CatException
     */
    @Test
    void testReadableRelFile() throws CatException {
        String[] args = new String[1];
        args[0] = FILENAME_VALID_1;
        sut.run(args, inputStream, outputStream);

        assertEquals(CONTENT_VALID_1, outputStream.toString());
    }

    /**
     * Basic test for a normal usage of `cat`.
     *
     * Command executed:
     * - `cat /tempFolder/a.txt`
     *
     * @throws CatException
     */
    @Test
    void testReadableAbsFile() throws CatException {
        String[] args = new String[1];
        args[0] = tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_VALID_1;
        sut.run(args, inputStream, outputStream);

        assertEquals(CONTENT_VALID_1, outputStream.toString());
    }

    /**
     * SUT should throw an exception when the file being read is unreadable.
     *
     * Command executed:
     * - `cat unreadable.txt`
     * - `unreadable.txt` has no permissions for the current user to read.
     */
    @Test
    void testUnreadableRelFile() {
        String[] args = new String[1];
        args[0] = FILENAME_UNREAD;

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_READING_FILE, exception.getMessage());
    }

    /**
     * SUT should throw an exception when the file being read is unreadable.
     *
     * Command executed:
     * - `cat unreadable.txt`
     * - `unreadable.txt` has no permissions for the current user to read.
     */
    @Test
    void testUnreadableAbsFile() {
        String[] args = new String[1];
        args[0] = tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_UNREAD;

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_READING_FILE, exception.getMessage());
    }

    /**
     * SUT should throw an exception when the file being read does not exist.
     *
     * Command executed:
     * - `cat non-existent.txt`
     * - `non-existent.txt` does not exist in the directory
     */
    @Test
    void testNonExistentRelFile() {
        String[] args = new String[1];
        args[0] = FILENAME_INVALID;

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_READING_FILE, exception.getMessage());
    }

    /**
     * SUT should throw an exception when the file being read does not exist.
     *
     * Command executed:
     * - `cat non-existent.txt`
     * - `non-existent.txt` does not exist in the directory
     */
    @Test
    void testNonExistentAbsFile() {
        String[] args = new String[1];
        args[0] = tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_INVALID;

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_READING_FILE, exception.getMessage());
    }

    /**
     * SUT should throw an exception when arg is a directory
     *
     * Command executed:
     * - `cat ./`
     */
    @Test
    void testRelDirectory() {
        String[] args = {FOLDER};

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_IS_DIR, exception.getMessage());
    }

    /**
     * SUT should throw an exception when arg is a directory
     *
     * Command executed:
     * - `cat /tempFolder/`
     */
    @Test
    void testAbsDirectory() {
        String[] args = {tempFolderAbsPath};

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_IS_DIR, exception.getMessage());
    }

    /**
     * SUT should return return empty string if file is empty
     *
     * Command executed:
     * - `cat empty.txt`
     * - `empty.txt` is a empty file
     */
    @Test
    void testEmptyRelFile() throws CatException {
        String[] args = new String[1];
        args[0] = FILENAME_EMPTY;
        sut.run(args, inputStream, outputStream);

        assertEquals("", outputStream.toString());
    }

    /**
     * SUT should return return empty string if file is empty
     *
     * Command executed:
     * - `cat empty.txt`
     * - `empty.txt` is a empty file
     */
    @Test
    void testEmptyAbsFile() throws CatException {
        String[] args = new String[1];
        args[0] = tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_EMPTY;
        sut.run(args, inputStream, outputStream);

        assertEquals("", outputStream.toString());
    }

    /**
     * Tests for `cat` of multiple files.
     *
     * Command executed:
     * - `cat valid1.txt valid2.txt`
     * - Both files are assumed to be valid
     */
    @Test
    void testMultipleRelFiles() throws CatException {
        String[] args = {FILENAME_VALID_1, FILENAME_VALID_2};
        String expected = CONTENT_VALID_1 + CONTENT_VALID_2;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` of multiple files. (abs path used)
     *
     * Command executed:
     * - `cat valid1.txt valid2.txt`
     * - Both files are assumed to be valid
     */
    @Test
    void testMultipleAbsFiles() throws CatException {
        String[] args = {tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_VALID_1,
                         tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_VALID_2};
        String expected = CONTENT_VALID_1 + CONTENT_VALID_2;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` of multiple files. (some specified with abs path, some specified with rel
     * path)
     *
     * Command executed:
     * - `cat valid1.txt valid2.txt`
     * - Both files are assumed to be valid
     */
    @Test
    void testMultipleMixedFiles() throws CatException {
        String[] args = {tempFolderAbsPath + CHAR_FILE_SEP + FILENAME_VALID_1, FILENAME_VALID_2};
        String expected = CONTENT_VALID_1 + CONTENT_VALID_2;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` of paths that contain `.`
     *
     * Command executed:
     * - `cat ././valid1.txt`
     */
    @Test
    void testCurrDirRelFile() throws CatException {
        String[] args = {
                STRING_CURR_DIR + CHAR_FILE_SEP + STRING_CURR_DIR + CHAR_FILE_SEP +
                        FILENAME_VALID_1};
        String expected = CONTENT_VALID_1;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` of paths that contain `.`
     *
     * Command executed:
     * - `cat /tempfolder/././valid1.txt`
     */
    @Test
    void testCurrDirAbsFile() throws CatException {
        String[] args = {
                tempFolderAbsPath + CHAR_FILE_SEP + STRING_CURR_DIR + CHAR_FILE_SEP +
                        STRING_CURR_DIR + CHAR_FILE_SEP + FILENAME_VALID_1};
        String expected = CONTENT_VALID_1;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` of paths that contain `..`
     *
     * Command executed:
     * - `cat folder1/../valid1.txt`
     */
    @Test
    void testParentDirRelFile() throws CatException {
        String[] args = {FOLDER + CHAR_FILE_SEP + STRING_PARENT_DIR + CHAR_FILE_SEP + FILENAME_VALID_1};
        String expected = CONTENT_VALID_1;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` of paths that contain `..`
     *
     * Command executed:
     * - `cat /tempfolder/folder1/../valid1.txt`
     */
    @Test
    void testParentDirAbsFile() throws CatException {
        String[] args = {tempFolderAbsPath + CHAR_FILE_SEP + FOLDER + CHAR_FILE_SEP + STRING_PARENT_DIR + CHAR_FILE_SEP + FILENAME_VALID_1};
        System.out.println(args[0]);
        String expected = CONTENT_VALID_1;

        sut.run(args, inputStream, outputStream);

        assertEquals(expected, outputStream.toString());
    }

    /**
     * Tests for `cat` from an input stream
     *
     * Command executed:
     * - `cat`
     * - Assume that content exists in InputStream - perhaps from a previous pipe.
     *
     * @throws CatException
     */
    @Test
    void testFromStdin() throws CatException {
        byte[] buffer = CONTENT_STDIN.getBytes();
        inputStream = new ByteArrayInputStream(buffer);

        String[] args = new String[0];

        sut.run(args, inputStream, outputStream);
        assertEquals(CONTENT_STDIN, outputStream.toString());
    }

    /**
     * Tests for `cat` from an input stream that is empty
     *
     * Command executed:
     * - `cat`
     * - Assume that content exists in InputStream - perhaps from a previous pipe.
     *
     * @throws CatException
     */
    @Test
    void testEmptyStdin() throws CatException {
        inputStream = new ByteArrayInputStream("".getBytes());

        String[] args = new String[0];

        sut.run(args, inputStream, outputStream);
        assertEquals("", outputStream.toString());
    }

    /**
     * Tests for null args when stdin or stdout are null as well.
     * NOTE: This case is not likely to happen AT ALL. But just for good measure.
     */
    @Test
    void testNullArgs() {
        String[] args = null;

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, null, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_NULL_STREAMS, exception.getMessage());

        exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, null);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_NULL_STREAMS, exception.getMessage());
    }

    /**
     * Test for empty args when stdin or stdout are null as well.
     * NOTE: This case is not likely to happen AT ALL. But just for good measure.
     */
    @Test
    void testEmptyArgs() {
        String[] args = new String[0];

        Throwable exception = assertThrows(CatException.class, () -> {
            sut.run(args, null, outputStream);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_NULL_STREAMS, exception.getMessage());

        exception = assertThrows(CatException.class, () -> {
            sut.run(args, inputStream, null);
        });
        assertEquals(CAT_ERR_PREFIX + ERR_NULL_STREAMS, exception.getMessage());
    }

    /**
     * Test case for when both stdin and args are present.
     * Contents from args should take precedence.
     */
    @Test
    void testStdinAndArgs() throws CatException {
        String[] args = new String[1];
        args[0] = FILENAME_VALID_1;

        byte[] buffer = CONTENT_STDIN.getBytes();
        inputStream = new ByteArrayInputStream(buffer);

        sut.run(args, inputStream, outputStream);

        assertEquals(CONTENT_VALID_1, outputStream.toString());
    }
}