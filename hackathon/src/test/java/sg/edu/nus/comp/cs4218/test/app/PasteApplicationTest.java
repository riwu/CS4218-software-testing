package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.app.PasteApplication;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.PasteException.PASTE_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.PasteApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

@EnableRuleMigrationSupport
public class PasteApplicationTest {
    private static final int IDX_EMPTY_FILE = 3;
    private static final int IDX_NEWLINE_FILE = 4;
    private static final String[] FOLDERS = {"folder1"};
    private static final String[] FILES = {"t1.txt", "t2.txt", "t3.txt", "empty.txt",
                                           "newlines.txt"};
    private static final String[] CONTENTS = {"a" + STRING_NEWLINE + "b",
                                              "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3",
                                              "A" + STRING_NEWLINE,
                                              "",
                                              STRING_NEWLINE + STRING_NEWLINE + STRING_NEWLINE +
                                                      STRING_NEWLINE};
    private PasteApplication pasteApp;
    private OutputStream outputStream;
    private String tempFolderAbsPath;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {

        OutputStream fileOutputStream;

        // Create temp files
        for (int i = 0; i < FILES.length; i++) {
            File temp = tempFolder.newFile(FILES[i]);
            fileOutputStream = Files.newOutputStream(temp.toPath());
            fileOutputStream.write(CONTENTS[i].getBytes());
        }

        // Create subfolder in temp folder
        tempFolder.newFolder(FOLDERS[0]);

        // Set current directory to be that of tempFolder
        tempFolderAbsPath = tempFolder.getRoot().getAbsolutePath();
        Environment.currentDirectory = tempFolderAbsPath;

        pasteApp = new PasteApplication();
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Tests for mergeStdin()
     */

    @Test
    void testEmptyStdin() throws PasteException {
        String contents = "";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeStdin(inputStream);
        assertEquals(contents, results);
    }

    @Test
    void testOneArgStdin() throws PasteException {
        String contents = "a1" + STRING_NEWLINE;
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeStdin(inputStream);
        assertEquals(contents, results);
    }

    @Test
    void testNewlineOnlyStdin() throws PasteException {
        String contents = STRING_NEWLINE + STRING_NEWLINE;
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeStdin(inputStream);
        assertEquals(contents, results);
    }

    @Test
    void testNewlineWithArgsStdin() throws PasteException {
        String contents = "b1" + STRING_NEWLINE + STRING_NEWLINE + "b2";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeStdin(inputStream);
        assertEquals(contents + STRING_NEWLINE, results);
    }

    @Test
    void testMultipleStdinArgs() throws PasteException {
        String contents = "c1" + STRING_NEWLINE + "c2" + STRING_NEWLINE + "c3";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeStdin(inputStream);
        assertEquals(contents + STRING_NEWLINE, results);
    }

    @Test
    void testNullStdin() {
        Throwable exception = assertThrows(PasteException.class, () -> {
            pasteApp.mergeStdin(null);
        });
        assertEquals(PASTE_ERR_PREFIX + ERR_NO_ISTREAM, exception.getMessage());
    }

    /**
     * Tests for mergeFile()
     */

    @Test
    void testMergeNewlineFile() throws PasteException {
        String results = pasteApp.mergeFile(FILES[IDX_NEWLINE_FILE]);
        assertEquals(CONTENTS[IDX_NEWLINE_FILE], results);
    }

    @Test
    void testMergeOneEmptyFile() throws PasteException {
        String results = pasteApp.mergeFile(FILES[IDX_EMPTY_FILE]);
        assertEquals(CONTENTS[IDX_EMPTY_FILE], results);
    }

    @Test
    void testMergeOneFile() throws PasteException {
        String results = pasteApp.mergeFile(FILES[0]);
        assertEquals(CONTENTS[0], results);
    }

    @Test
    void testMergeCurrDirFile() throws PasteException {
        // paste ./t1.txt
        String results = pasteApp.mergeFile(STRING_CURR_DIR + CHAR_FILE_SEP + FILES[0]);
        assertEquals(CONTENTS[0], results);
    }

    @Test
    void testMergeParentDirFile() throws PasteException {
        // paste folder1/../t1.txt
        String results = pasteApp.mergeFile(FOLDERS[0] + CHAR_FILE_SEP + STRING_PARENT_DIR + CHAR_FILE_SEP + FILES[0]);
        assertEquals(CONTENTS[0], results);
    }

    @Test
    void testMergeTwoEmptyFiles() throws PasteException {
        String results = pasteApp.mergeFile(FILES[IDX_EMPTY_FILE], FILES[IDX_EMPTY_FILE]);
        assertEquals("", results);
    }

    @Test
    void testMergeNewLineFileWithNonEmptyFiles() throws PasteException {
        String results = pasteApp
                .mergeFile(FILES[IDX_NEWLINE_FILE], FILES[0], FILES[IDX_NEWLINE_FILE]);
        String expected = CHAR_TAB + "a" + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + "b" + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + CHAR_TAB + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    @Test
    void testMergeEmptyFileWithNonEmptyFiles() throws PasteException {
        String results = pasteApp.mergeFile(FILES[IDX_EMPTY_FILE], FILES[0], FILES[IDX_EMPTY_FILE]);
        String expected = CHAR_TAB + "a" + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + "b" + CHAR_TAB + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    @Test
    void testMergeMultipleFiles() throws PasteException {
        String results = pasteApp.mergeFile(FILES[0], FILES[1], FILES[2]);
        String expected = "a" + CHAR_TAB + "1" + CHAR_TAB + "A" + STRING_NEWLINE
                + "b" + CHAR_TAB + "2" + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + "3" + CHAR_TAB + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    @Test
    void testMergeDirectory() throws PasteException {
        String results = pasteApp.mergeFile(tempFolderAbsPath);
        assertEquals("", results);
    }

    @Test
    void testMergeDirectoryWithFile() throws PasteException {
        String results = pasteApp.mergeFile(tempFolderAbsPath, FILES[0], tempFolderAbsPath);
        String expected = CHAR_TAB + "a" + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + "b" + CHAR_TAB + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    @Test
    void testMergeNonExistentFile() {
        Throwable exception = assertThrows(PasteException.class, () -> {
            pasteApp.mergeFile("xyz.txt");
        });
        assertEquals(PASTE_ERR_PREFIX + String.format(ERR_NO_SUCH_FILE, "xyz.txt"), exception.getMessage());
    }

    @Test
    void testMergeNonExistentAndValidFiles() {
        Throwable exception = assertThrows(PasteException.class, () -> {
            pasteApp.mergeFile(FILES[0], "xyz" + CHAR_FILE_SEP + "abc", FILES[1]);
        });
        assertEquals(PASTE_ERR_PREFIX + String.format(ERR_NO_SUCH_FILE, "xyz" + CHAR_FILE_SEP + "abc"),
                     exception.getMessage());
    }

    @Test
    void testMergeOneAbsFile() throws PasteException {
        String results = pasteApp.mergeFile(tempFolderAbsPath + CHAR_FILE_SEP + FILES[1]);
        String expected = CONTENTS[1];
        assertEquals(expected, results);
    }

    @Test
    void testMergeCurrDirAbsFile() throws PasteException {
        // paste /tempfolder/./t2.txt
        String results = pasteApp.mergeFile(
                tempFolderAbsPath + CHAR_FILE_SEP + STRING_CURR_DIR + CHAR_FILE_SEP + FILES[1]);
        String expected = CONTENTS[1];
        assertEquals(expected, results);
    }

    @Test
    void testMergeParentDirAbsFile() throws PasteException {
        // paste /tempfolder/folder1/../folder1/../t1.txt
        String results = pasteApp.mergeFile(
                tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP +
                        STRING_PARENT_DIR + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP +
                        STRING_PARENT_DIR + CHAR_FILE_SEP + FILES[0]);
        String expected = CONTENTS[0];
        assertEquals(expected, results);
    }

    /**
     * Tests for mergeFileAndStdin()
     */

    @Test
    void testMergeEmptyFileAndEmptyStdin() throws PasteException {
        String contents = "";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeFileAndStdin(inputStream, FILES[IDX_EMPTY_FILE]);
        assertEquals("", results);
    }

    @Test
    void testMergeFileAndStdin() throws PasteException {
        String contents = "d1" + STRING_NEWLINE;
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String results = pasteApp.mergeFileAndStdin(inputStream, FILES[0], FILES[2]);
        String expected = "d1" + CHAR_TAB + "a" + CHAR_TAB + "A" + STRING_NEWLINE
                + CHAR_TAB + "b" + CHAR_TAB + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    /**
     * Tests for run()
     */

    @Test
    void testRunStdinOnly() throws PasteException {
        String[] args = {"-"};
        String contents = "c1" + STRING_NEWLINE + "c2" + STRING_NEWLINE + "c3";
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String expected = contents + STRING_NEWLINE;

        pasteApp.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunFilesOnly() throws PasteException {
        String[] args = {FILES[0], FILES[1], FILES[2]};
        InputStream inputStream = new ByteArrayInputStream("".getBytes());
        String expected = "a" + CHAR_TAB + "1" + CHAR_TAB + "A" + STRING_NEWLINE
                + "b" + CHAR_TAB + "2" + CHAR_TAB + STRING_NEWLINE
                + CHAR_TAB + "3" + CHAR_TAB + STRING_NEWLINE;

        pasteApp.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunFilesAndStdin() throws PasteException {
        String[] args = {FILES[0], FILES[2], "-"};
        String contents = "d1" + STRING_NEWLINE;
        InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
        String expected = "d1" + CHAR_TAB + "a" + CHAR_TAB + "A" + STRING_NEWLINE
                + CHAR_TAB + "b" + CHAR_TAB + STRING_NEWLINE;

        pasteApp.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testRunNullArgs() {
        Throwable exception = assertThrows(PasteException.class, () -> {
            pasteApp.run(null, System.in, outputStream);
        });
        assertEquals(PASTE_ERR_PREFIX + ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    void testRunNullOutputStream() {
        Throwable exception = assertThrows(PasteException.class, () -> {
            pasteApp.run(new String[0], System.in, null);
        });
        assertEquals(PASTE_ERR_PREFIX + ERR_NO_OSTREAM, exception.getMessage());
    }
}
