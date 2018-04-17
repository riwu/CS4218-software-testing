package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.app.LsApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.LsException.LS_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.LsApplication.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.app.LsApplication.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@EnableRuleMigrationSupport
public class LsApplicationTest {
    private final static List<String> FILES = new ArrayList<>(
            Arrays.asList("a.txt", "b.txt", "c.txt", "d.txt"));
    private final static List<String> FOLDERS = new ArrayList<>(
            Arrays.asList("fA", "fB", "fC", "fD"));
    private final static List<String> EMPTY_FOLDERS = new ArrayList<>(Arrays.asList("empty1"));
    private final static String FOLDER_INVALID = "invalid";

    private LsApplication sut;
    private String tempFolderAbsPath;
    private final OutputStream outputStream = new ByteArrayOutputStream();

    /**
     * Don't need to manually clear these files after the test is over, JUnit guarantees to run
     * the cleanup procedures for us.
     *
     * See: http://junit.org/junit4/javadoc/latest/org/junit/rules/TemporaryFolder.html
     */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize our temporary file directory
        for (String file : FILES) {
            tempFolder.newFile(file);
        }

        // Each folder should have the four dummy test files too
        for (String folder : FOLDERS) {
            File newFolder = tempFolder.newFolder(folder);
            for (String file : FILES) {
                tempFolder.newFile(newFolder.getName() + CHAR_FILE_SEP + file);
            }
        }

        // Create empty folder
        for (String folder : EMPTY_FOLDERS) {
            tempFolder.newFolder(folder);
        }

        // Set current directory to be that of tempFolder
        tempFolderAbsPath = tempFolder.getRoot().getAbsolutePath();
        Environment.currentDirectory = tempFolderAbsPath;

        // Initialize software-under-test (SUT)
        sut = new LsApplication();
    }

    @Test
    public void testListFolderContent() throws Exception {
        String result = sut.listFolderContent(false, false);
        String expected = vanillaLsResults();
        assertEquals(expected, result);
    }

    @Test
    public void testRelListMultipleFolderContent() throws Exception {
        String result = sut.listFolderContent(false, false,
                                              FOLDERS.get(0), FOLDERS.get(1),
                                              FOLDERS.get(2), FOLDERS.get(3));
        String expected = multipleLsResults();
        assertEquals(expected, result);
    }

    @Test
    public void testAbsListMultipleFolderContent() throws Exception {
        String result = sut.listFolderContent(false, false,
                                              tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS.get(0),
                                              tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS.get(1),
                                              tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS.get(2),
                                              tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS.get(3));
        String expected = multipleLsResults();
        assertEquals(expected, result);
    }

    @Test
    public void testRelListEmptyFolderContent() throws Exception {
        String result = sut.listFolderContent(false, false, EMPTY_FOLDERS.get(0));
        String expected = EMPTY_FOLDERS.get(0) + ":";
        assertEquals(expected, result);
    }

    @Test
    public void testAbsListEmptyFolderContent() throws Exception {
        String result = sut.listFolderContent(false, false, tempFolderAbsPath + CHAR_FILE_SEP + EMPTY_FOLDERS.get(0));
        String expected = EMPTY_FOLDERS.get(0) + ":";
        assertEquals(expected, result);
    }

    @Test
    public void testRelCurrDirContent() throws Exception {
        String result = sut.listFolderContent(false, false, STRING_CURR_DIR);
        String expected = STRING_CURR_DIR + CHAR_FILE_SEP + ':'+ STRING_NEWLINE + vanillaLsResults();
        assertEquals(expected, result);
    }

    @Test
    public void testAbsCurrDirContent() throws Exception {
        String result = sut.listFolderContent(false, false, tempFolderAbsPath + CHAR_FILE_SEP + STRING_CURR_DIR + CHAR_FILE_SEP);
        String expected = STRING_CURR_DIR + CHAR_FILE_SEP + ':' + STRING_NEWLINE + vanillaLsResults();
        assertEquals(expected, result);
    }

    @Test
    public void testLsCwd() throws LsException {
        String args[] = new String[0];
        sut.run(args, null, outputStream);
        assertEquals(vanillaLsResults() + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testLsInvalidDirectory() throws LsException {
        String args[] = {FOLDER_INVALID};
        sut.run(args, null, outputStream);
        assertEquals("ls: cannot access 'invalid': No such file or directory" + StringUtils.STRING_NEWLINE,
                     outputStream.toString());
    }

    @Test
    public void testLsFolderOnly() throws LsException {
        String args[] = {"-d"};
        sut.run(args, null, outputStream);
        assertEquals(folderOnlyLsResults() + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testLsRecursive() throws LsException {
        String args[] = {"-R"};
        sut.run(args, null, outputStream);
        assertEquals(recursiveLsResults() + StringUtils.STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void testInvalidFlags() {
        String args[] = {"-a"};
        Throwable exception = assertThrows(LsException.class, () -> {
            sut.run(args, null, outputStream);
        });
        assertEquals("ls: illegal option -- a", exception.getMessage());
    }

    @Test
    public void testNullArgs() {
        String args[] = null;
        Throwable exception = assertThrows(LsException.class, () -> {
            sut.run(args, null, outputStream);
        });
        assertEquals(LS_ERR_PREFIX + ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testNullOutputStream() {
        String args[] = new String[0];
        Throwable exception = assertThrows(LsException.class, () -> {
            sut.run(args, null, null);
        });
        assertEquals(LS_ERR_PREFIX + ERR_NO_OSTREAM, exception.getMessage());
    }

    /**
     * @return Expected result of an empty `ls` command
     */
    private String vanillaLsResults() {
        // Add all elements of the expected results into a list
        List<String> result = new ArrayList<>();
        for (String file : FILES) {
            result.add(file);
        }

        for (String folder : FOLDERS) {
            result.add(folder);
        }

        for (String folder : EMPTY_FOLDERS) {
            result.add(folder);
        }

        // Sort the results
        Collections.sort(result);

        // Build the string
        StringBuilder builder = new StringBuilder();
        for (String item : result) {
            builder.append(item).append(STRING_NEWLINE);
        }

        return builder.toString().trim();
    }

    /**
     * @return Expected result of `ls fA fB fC fD`
     */
    private String multipleLsResults() {
        StringBuilder builder = new StringBuilder();
        for (String folder : FOLDERS) {
            builder.append(folder).append(':').append(STRING_NEWLINE);

            List<String> contents = new ArrayList<>();
            for (String file : FILES) {
                contents.add(file);
            }

            Collections.sort(contents);

            for (String item : contents) {
                builder.append(item).append(STRING_NEWLINE);
            }

            builder.append(STRING_NEWLINE);
        }

        return builder.toString().trim();
    }

    private String folderOnlyLsResults() {
        List<String> result = new ArrayList<>();
        for (String folder : FOLDERS) {
            result.add(folder);
        }

        for (String folder : EMPTY_FOLDERS) {
            result.add(folder);
        }

        // Sort the results
        Collections.sort(result);

        // Build the string
        StringBuilder builder = new StringBuilder();
        for (String item : result) {
            builder.append(item).append(STRING_NEWLINE);
        }

        return builder.toString().trim();
    }

    private String recursiveLsResults() {
        StringBuilder result = new StringBuilder();
        // TODO: This is incorrect, Linux has the '.' character in front of each recursive ls result
        result.append(STRING_CURR_DIR + CHAR_FILE_SEP + ':').append(STRING_NEWLINE);
        for (String file : FILES) {
            result.append(file).append(STRING_NEWLINE);
        }
        for (String emptyFolder : EMPTY_FOLDERS) {
            result.append(emptyFolder).append(STRING_NEWLINE);
        }
        for (String folder : FOLDERS) {
            result.append(folder).append(STRING_NEWLINE);
        }

        result.append(STRING_NEWLINE);

        for (String folder : EMPTY_FOLDERS) {
            result.append(folder).append(':').append(STRING_NEWLINE);
        }

        result.append(STRING_NEWLINE);

        for (String folder : FOLDERS) {
            result.append(folder).append(':').append(STRING_NEWLINE);
            for (String file : FILES) {
                result.append(file).append(STRING_NEWLINE);
            }
            result.append(STRING_NEWLINE);
        }

        return result.toString().trim();
    }
}
