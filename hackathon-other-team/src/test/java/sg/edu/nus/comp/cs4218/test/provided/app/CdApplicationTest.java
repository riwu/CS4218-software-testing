package sg.edu.nus.comp.cs4218.test.provided.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;
import sg.edu.nus.comp.cs4218.test.TestEnvironment;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.CdException.CD_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.CdApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_PARENT_DIR;

public class CdApplicationTest extends TestEnvironment {
    private static final String TEST_DIR = "provided" + File.separator + "cd-test";

    private CdApplication cdApp;
    private String absTestDirPath;

    // Filename and folders
    public static final String DIR_FOLDER1 = "folder1";
    public static final String DIR_FOLDER2 = "folder2";

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        cdApp = new CdApplication();

        // Set current directory to TEST_DIR
        absTestDirPath = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                   ENV_ROOT,
                                   TEST_DIR).toString();
        Environment.currentDirectory = absTestDirPath;
    }

    @Test
    public void testRelativeDirectoryChange() throws CdException {
        String expected = absTestDirPath + File.separator + DIR_FOLDER1;
        cdApp.changeToDirectory(DIR_FOLDER1);
        String result = Environment.currentDirectory;
        assertEquals(expected, result);
    }

    @Test
    public void testAbsoluteDirectoryChange() throws CdException {
        String expected = absTestDirPath + File.separator + DIR_FOLDER2;
        cdApp.changeToDirectory(absTestDirPath + File.separator + DIR_FOLDER2);
        String result = Environment.currentDirectory;
        assertEquals(expected, result);
    }

    /**
     * Provided was named: testUserDirectoryChange()
     * The expected output for our test is different because
     * we assumed that cd without arguments is not supported
     * So we expect an exception to be thrown
     */
    @Test
    public void testNullDirectoryChange() {
        String expectedErrMsg = CD_ERR_PREFIX + ERR_NO_ARGS;
        Throwable exception = assertThrows(CdException.class, () ->{
            cdApp.changeToDirectory(null);
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    @Test
    public void testCurrentDirectoryChange() throws CdException {
        String expected = absTestDirPath;
        cdApp.changeToDirectory(".");
        String result = Environment.currentDirectory;
        assertEquals(expected, result);
    }

    @Test
    public void testParentDirectoryChange() throws CdException {
        String expected = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                    ENV_ROOT,
                                    "provided").toString();
        cdApp.changeToDirectory("..");
        String result = Environment.currentDirectory;
        assertEquals(expected, result);
    }

    @Test
    public void testDirChangeMixWithSpecialDir() throws CdException {
        String expected = absTestDirPath + File.separator + DIR_FOLDER2;
        cdApp.changeToDirectory("folder1" + CHAR_FILE_SEP + STRING_PARENT_DIR + CHAR_FILE_SEP + "folder2");
        String result = Environment.currentDirectory;
        assertEquals(expected, result);
    }

    /**
     * The expected output for our test is different because
     * we assumed that cd without arguments is not supported
     * So we expect an exception to be thrown
     */
    @Test
    public void testEmptyDirectoryChange() {
        String expectedErrMsg = CD_ERR_PREFIX + ERR_NO_ARGS;
        Throwable exception = assertThrows(CdException.class, () ->{
            cdApp.changeToDirectory("");
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    @Test
    public void testInvalidNonExistentChange() {
        String expectedErrMsg = CD_ERR_PREFIX +  String.format(ERR_NO_SUCH_DIR, "unknownDir");
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.changeToDirectory("unknownDir");
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    @Test
    public void testInvalidFileChange() {
        String expectedErrMsg = CD_ERR_PREFIX +  String.format(ERR_IS_NOT_DIR, "file1.txt");
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.changeToDirectory("file1.txt");
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    /**
     * The expected output for our test is different because
     *  we assumed that spaces are not considered args
     *  so an ERR_NO_ARGS exception is thrown
     */
    @Test
    public void testInvalidSpacesDirectoryChange() {
        String expectedErrMsg = CD_ERR_PREFIX + ERR_NO_ARGS;
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.changeToDirectory("   ");
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    /**
     * The expected output for our test is different because
     * we assumed that cd without arguments is not supported
     * so we expect an exception to be thrown
     */
    @Test
    public void testNullArgs() {
        String expectedErrMsg = CD_ERR_PREFIX + ERR_NULL_ARGS;
        Throwable exception = assertThrows(CdException.class, () -> {
            cdApp.run(null, null, null);
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    /**
     * The expected output for our test is different because
     * we assumed that cd without arguments is not supported
     * so we expect an exception to be thrown
     */
    @Test
    public void testEmptyStringInArgs() {
        String expectedErrMsg = CD_ERR_PREFIX + ERR_NO_ARGS;
        Throwable exception = assertThrows(CdException.class, () -> {
            String[] strArr = {""};
            cdApp.run(strArr, null, null);
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }

    @Test
    public void testInvalidMultipleDirectoryChange() {
        String expectedErrMsg = CD_ERR_PREFIX + ERR_TOO_MANY_ARGS;
        Throwable exception = assertThrows(CdException.class, () -> {
            String[] strArr = {DIR_FOLDER1, DIR_FOLDER2};
            cdApp.run(strArr, null, null);
        });
        assertEquals(expectedErrMsg, exception.getMessage());
    }
}
