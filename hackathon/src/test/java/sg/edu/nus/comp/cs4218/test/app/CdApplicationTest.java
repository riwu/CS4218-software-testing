package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.CdException.CD_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.CdApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

@EnableRuleMigrationSupport
public class CdApplicationTest {
    private final static String FILE = "file.txt";
    private final static String[] FOLDERS = {"fA", "fB", "fC"};
    private final static String[] SUBFOLDERS = {"s1", "s2"};

    private CdApplication cdApp;
    private String tempFolderPath;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {

        // create a temp file for error testing
        tempFolder.newFile(FILE);

        // create level 1 folders and their subfolders
        for (String folder : FOLDERS) {
            tempFolder.newFolder(folder);

            // create subfolders
            for (String subfolder : SUBFOLDERS) {
                tempFolder.newFolder(folder, subfolder);
            }
        }

        // Set current directory to be that of tempFolder
        tempFolderPath = tempFolder.getRoot().getAbsolutePath();
        Environment.currentDirectory = tempFolderPath;

        cdApp = new CdApplication();
    }

    /**
     * Tests for changeToDirectory
     */

    @Test
    void testCDOneLevelRelativePath() throws CdException {
        // cd fA
        String path = FOLDERS[0];
        cdApp.changeToDirectory(path);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + path, Environment.currentDirectory);
    }

    @Test
    void testCDMultipleRelativePath() throws CdException {
        // cd fA/s1
        String path = FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0];
        cdApp.changeToDirectory(path);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + path, Environment.currentDirectory);
    }

    @Test
    void testCDAbsolutePath() throws CdException {
        // cd /<tempFolder>/fA/s1
        String path = tempFolderPath + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0];
        cdApp.changeToDirectory(path);
        assertEquals(path, Environment.currentDirectory);
    }

    @Test
    void testCDAbsolutePathInSubfolder() throws CdException {
        // when in fA, cd /<tempFolder/fB/s1>

        // set current directory to fA
        Environment.currentDirectory = tempFolderPath + CHAR_FILE_SEP + FOLDERS[0];

        // cd /<tempFolder/fB/s1>
        String path = tempFolderPath + CHAR_FILE_SEP + FOLDERS[1] + CHAR_FILE_SEP + SUBFOLDERS[0];
        cdApp.changeToDirectory(path);
        assertEquals(path, Environment.currentDirectory);
    }

    @Test
    void testCDCurrDir() throws CdException {
        // cd .
        String path = ".";
        cdApp.changeToDirectory(path);
        assertEquals(tempFolderPath, Environment.currentDirectory);
    }

    @Test
    void testCDPathWithOneDot() throws CdException {
        // cd ./fA/./s1
        String prevDirectory = Environment.currentDirectory;
        String path = STRING_CURR_DIR + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP +
                STRING_CURR_DIR + CHAR_FILE_SEP + SUBFOLDERS[0];
        cdApp.changeToDirectory(path);
        assertEquals(prevDirectory + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0],
                     Environment.currentDirectory);
    }

    @Test
    void testCDOneDirUp() throws CdException {
        // set current directory to fA
        Environment.currentDirectory = tempFolderPath + CHAR_FILE_SEP + FOLDERS[0];

        // cd ..
        String path = STRING_PARENT_DIR;
        cdApp.changeToDirectory(path);
        assertEquals(tempFolderPath, Environment.currentDirectory);
    }

    @Test
    void testCDPathWithTwoDots() throws CdException {
        // cd fA/s1/../../fA
        String path = FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0] + CHAR_FILE_SEP +
                STRING_PARENT_DIR + CHAR_FILE_SEP + STRING_PARENT_DIR + CHAR_FILE_SEP + FOLDERS[0];
        cdApp.changeToDirectory(path);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + FOLDERS[0], Environment.currentDirectory);
    }

    @Test
    void testCDOneDirUpRoot() throws CdException {
        // if curr dir is / then cd .. => shld remain in /

        // set current directory to /
        Environment.currentDirectory = String.valueOf(CHAR_FILE_SEP);

        // cd ..
        String path = "..";
        cdApp.changeToDirectory(path);
        assertEquals(String.valueOf(CHAR_FILE_SEP), Environment.currentDirectory);
    }

    @Test
    void testCDNonExistent() {
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.changeToDirectory("xxxxx");
        });
        assertEquals(CD_ERR_PREFIX + String.format(ERR_NO_SUCH_DIR, "xxxxx"),
                     exception.getMessage());
    }

    @Test
    void testCDFile() {
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.changeToDirectory(FILE);
        });
        assertEquals(CD_ERR_PREFIX + String.format(ERR_IS_NOT_DIR, FILE), exception.getMessage());
    }

    /**
     * Tests for run
     */

    @Test
    void testRunCDOneLevelRelativePath() throws CdException {
        // cd fA
        String[] args = {FOLDERS[0]};
        cdApp.run(args, null, null);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + args[0], Environment.currentDirectory);
    }

    @Test
    void testRunCDMultipleRelativePath() throws CdException {
        // cd fA/s1
        String[] args = {FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0]};
        cdApp.run(args, null, null);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + args[0], Environment.currentDirectory);
    }

    @Test
    void testRunCDAbsolutePath() throws CdException {
        // cd /<tempFolder>/fA/s1
        String[] args = {
                tempFolderPath + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0]};
        cdApp.run(args, null, null);
        assertEquals(args[0], Environment.currentDirectory);
    }

    @Test
    void testRunCDAbsolutePathInSubfolder() throws CdException {
        // when in fA, cd /<tempFolder/fB/s1>

        // set current directory to fA
        Environment.currentDirectory = tempFolderPath + CHAR_FILE_SEP + FOLDERS[0];

        // cd /<tempFolder/fB/s1>
        String[] args = {
                tempFolderPath + CHAR_FILE_SEP + FOLDERS[1] + CHAR_FILE_SEP + SUBFOLDERS[0]};
        cdApp.run(args, null, null);
        assertEquals(args[0], Environment.currentDirectory);
    }

    @Test
    void testRunCDCurrDir() throws CdException {
        // cd .
        String[] args = {STRING_CURR_DIR};
        cdApp.run(args, null, null);
        assertEquals(tempFolderPath, Environment.currentDirectory);
    }

    @Test
    void testRunCDPathWithOneDot() throws CdException {
        // cd ./fA/./s1
        String[] args = {
                STRING_CURR_DIR + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP + STRING_CURR_DIR +
                        CHAR_FILE_SEP + SUBFOLDERS[0]};
        cdApp.run(args, null, null);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0],
                     Environment.currentDirectory);
    }

    @Test
    void testRunCDOneDirUp() throws CdException {
        // set current directory to fA
        Environment.currentDirectory = tempFolderPath + CHAR_FILE_SEP + FOLDERS[0];

        // cd ..
        String[] args = {STRING_PARENT_DIR};
        cdApp.run(args, null, null);
        assertEquals(tempFolderPath, Environment.currentDirectory);
    }

    @Test
    void testRunCDPathWithTwoDots() throws CdException {
        // cd fA/s1/../../fA
        String[] args = {
                FOLDERS[0] + CHAR_FILE_SEP + SUBFOLDERS[0] + CHAR_FILE_SEP + STRING_PARENT_DIR +
                        CHAR_FILE_SEP + STRING_PARENT_DIR + CHAR_FILE_SEP + FOLDERS[0]};
        cdApp.run(args, null, null);
        assertEquals(tempFolderPath + CHAR_FILE_SEP + FOLDERS[0], Environment.currentDirectory);
    }

    @Test
    void testRunCdOneDirUpRoot() throws CdException {
        // if curr dir is / then cd .. => shld remain in /

        // set current directory to /
        Environment.currentDirectory = String.valueOf(CHAR_FILE_SEP);

        // cd ..
        String[] args = {STRING_PARENT_DIR};
        cdApp.run(args, null, null);
        assertEquals(String.valueOf(CHAR_FILE_SEP), Environment.currentDirectory);
    }

    @Test
    void testRunCDNonExistent() {
        String[] args = {"yyyyyy"};
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.run(args, null, null);
        });
        assertEquals(CD_ERR_PREFIX + String.format(ERR_NO_SUCH_DIR, "yyyyyy"),
                     exception.getMessage());
    }

    @Test
    void testRunCDFile() {
        String[] args = {FILE};
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.run(args, null, null);
        });
        assertEquals(CD_ERR_PREFIX + String.format(ERR_IS_NOT_DIR, FILE), exception.getMessage());
    }

    @Test
    void testRunNoArgs() {
        String[] args = {};
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.run(args, null, null);
        });
        assertEquals(CD_ERR_PREFIX + ERR_NO_ARGS, exception.getMessage());
    }

    @Test
    void testRunTooManyArgs() {
        String[] args = {FOLDERS[0], FOLDERS[1]};
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.run(args, null, null);
        });
        assertEquals(CD_ERR_PREFIX + ERR_TOO_MANY_ARGS, exception.getMessage());
    }

    @Test
    void testRunNullArgs() {
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.run(null, null, null);
        });
        assertEquals(CD_ERR_PREFIX + ERR_NULL_ARGS, exception.getMessage());
    }
}
