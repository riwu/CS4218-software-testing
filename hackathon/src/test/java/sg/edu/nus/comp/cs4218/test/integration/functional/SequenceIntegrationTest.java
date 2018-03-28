package sg.edu.nus.comp.cs4218.test.integration.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import sg.edu.nus.comp.cs4218.test.integration.IntegrationTestEnvironment;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.exception.CatException.CAT_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.exception.CdException.CD_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.exception.GrepException.GREP_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.exception.PasteException.PASTE_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.CatApplication.ERR_READING_FILE;
import static sg.edu.nus.comp.cs4218.impl.app.CdApplication.ERR_NO_SUCH_DIR;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.ERR_BAD_REGEX;
import static sg.edu.nus.comp.cs4218.impl.app.PasteApplication.ERR_NO_SUCH_FILE;
import static sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SequenceIntegrationTest extends IntegrationTestEnvironment {
    private static final String TEST_DIR = "seq-integration-test";

    private static final String[] FILES = {"file0.txt", "file1.txt", "file2.txt"};
    private static final String[] FILES_IN_FOLDERS = {"file3.txt", "file4.txt"};
    private static final String[] FOLDERS = {"folder0", "folder1"};
    private static final String[][] FILE_CONTENTS = {{"01", "234", "5678"}, {"hello", "foo bar"},
                                                     {"a", "b", "c", "d", "e"}};
    private static final String[] FILE_3_4_CONTENTS = {"inside folder0", "inside folder1"};
    private static final String INVALID_FILE = "invalidFile.txt";

    private String tempFolderAbsPath;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Set current directory to TEST_DIR
        tempFolderAbsPath = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                      ENV_ROOT,
                                      TEST_DIR).toString();
        Environment.currentDirectory = tempFolderAbsPath;
    }

    /****************************************************
     * Seq tests with no state change
     * e.g. no change in curr dir or no creation of new files
     ****************************************************/

    /**
     * cat file0.txt;
     * @throws Exception
     */
    @Test
    void testOneValidCmd() throws Exception {
        CallCommand catCmd = createCommand(APP_CAT, FILES[0]);
        SequenceCommand seq = sequenceCommands(catCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = String.join(STRING_NEWLINE, FILE_CONTENTS[0]) + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    /**
     * grep [ file0.txt;
     * @throws Exception
     */
    @Test
    void testOneInvalidCmd() throws Exception {
        CallCommand catCmd = createCommand(APP_GREP, "[", FILES[0]);
        SequenceCommand seq = sequenceCommands(catCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = GREP_ERR_PREFIX + ERR_BAD_REGEX + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    /**
     * ls; echo foo; cat file0.txt | grep
     */
    @Test
    void testMultipleValidCmd() throws Exception {
        CallCommand lsCmd = createCommand(APP_LS);
        CallCommand echoCmd = createCommand(APP_ECHO, "foo");

        CallCommand pipeCatCmd = createCommand(APP_CAT, FILES[0]);
        CallCommand pipeGrepCmd = createCommand(APP_GREP, "0");
        PipeCommand pipedCmds = pipeCommands(pipeCatCmd, pipeGrepCmd);

        SequenceCommand seq = sequenceCommands(lsCmd, echoCmd, pipedCmds);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = String.join(STRING_NEWLINE, FILES) + STRING_NEWLINE
                        + String.join(STRING_NEWLINE, FOLDERS) + STRING_NEWLINE
                        + "foo" + STRING_NEWLINE
                        + FILE_CONTENTS[0][0] + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    /**
     * echo HEY; grep ++ file0.txt; cat file1.txt
     */
    @Test
    void testMultipleMixedValidInvalidCmd() throws Exception {
        CallCommand echoCmd = createCommand(APP_ECHO, "HEY");
        CallCommand invalidGrepCmd = createCommand(APP_GREP, "++", FILES[0]);
        CallCommand catCmd = createCommand(APP_CAT, FILES[1]);
        SequenceCommand seq = sequenceCommands(echoCmd, invalidGrepCmd, catCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = "HEY" + STRING_NEWLINE
                + GREP_ERR_PREFIX + ERR_BAD_REGEX + STRING_NEWLINE
                + String.join(STRING_NEWLINE, FILE_CONTENTS[1]) + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    /**
     * paste invalidFile.txt; diff invalidFile.txt file0.txt; sed A/S file0.txt
     * @throws Exception
     */
    @Test
    void testMultipleInvalidCmd() throws Exception {
        CallCommand pasteCmd = createCommand(APP_PASTE, INVALID_FILE);
        CallCommand diffCmd = createCommand(APP_DIFF, INVALID_FILE, FILES[0]);
        CallCommand sedCmd = createCommand(APP_SED, "A/S", FILES[0]);
        SequenceCommand seq = sequenceCommands(pasteCmd, diffCmd, sedCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = PASTE_ERR_PREFIX + String.format(ERR_NO_SUCH_FILE, INVALID_FILE) + STRING_NEWLINE
                        + "diff: unknown error." + STRING_NEWLINE
                        + "sed: invalid expression - start character A" + STRING_NEWLINE;
        assertEquals(expected, results);
    }

    /****************************************************
     * Seq tests with exit
     * These test cases should always throw ExitException
     ****************************************************/

    /**
     * ls folder0; exit; diff file1.txt file2.txt
     */
    @Test
    void testMultipleValidCmdWithExit() {
        CallCommand lsCmd = createCommand(APP_LS, FOLDERS[0]);
        CallCommand exitCmd = createCommand(APP_EXIT);
        CallCommand diffCmd = createCommand(APP_DIFF, FILES[1], FILES[2]);
        SequenceCommand seq = sequenceCommands(lsCmd, exitCmd, diffCmd);

        writeToStdin("");
        Throwable exception = assertThrows(ExitException.class, () -> {
            seq.evaluate(stdin, stdout);
        });
    }

    /**
     * grep ++ file0.txt; exit; diff invalidFile.txt file2.txt
     */
    @Test
    void testMultipleInvalidCmdWithExit() {
        CallCommand grepCmd = createCommand(APP_GREP, "++", FILES[0]);
        CallCommand exitCmd = createCommand(APP_EXIT);
        CallCommand diffCmd = createCommand(APP_DIFF, INVALID_FILE, FILES[2]);
        SequenceCommand seq = sequenceCommands(grepCmd, exitCmd, diffCmd);

        writeToStdin("");
        Throwable exception = assertThrows(ExitException.class, () -> {
            seq.evaluate(stdin, stdout);
        });
    }

    /****************************************************
     * Seq tests with state change
     * e.g. change in curr dir or creation of new files
     ****************************************************/

    /**
     * ls; mkdir /tempFolder/newfolder; ls
     * @throws Exception
     */
    @Test
    void testMultipleValidCmdWithMkdir() throws Exception {
        CallCommand mkdirCmd = createCommand(APP_MKDIR, tempFolderAbsPath + CHAR_FILE_SEP + "newfolder");
        CallCommand lsCmd = createCommand(APP_LS);
        SequenceCommand seq = sequenceCommands(lsCmd, mkdirCmd, lsCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = String.join(STRING_NEWLINE, FILES) + STRING_NEWLINE
                        + String.join(STRING_NEWLINE, FOLDERS) + STRING_NEWLINE
                        + String.join(STRING_NEWLINE, FILES) + STRING_NEWLINE
                        + String.join(STRING_NEWLINE, FOLDERS) + STRING_NEWLINE
                        + "newfolder" + STRING_NEWLINE;
        assertEquals(expected, results);
        assertTrue(Files.exists(Paths.get(tempFolderAbsPath, "newfolder")));
    }

    /**
     * mkdir new; mkdir new
     * @throws Exception
     */
    @Test
    void testMultipleInvalidCmdWithMkdir() throws Exception {
        CallCommand mkdirCmd = createCommand(APP_MKDIR, tempFolderAbsPath + CHAR_FILE_SEP + "new");
        SequenceCommand seq = sequenceCommands(mkdirCmd, mkdirCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = "mkdir: mkdir: " + tempFolderAbsPath + CHAR_FILE_SEP + "new failed" + STRING_NEWLINE + STRING_NEWLINE;
        assertEquals(expected, results);
        assertTrue(Files.exists(Paths.get(tempFolderAbsPath, "new")));
    }

    /**
     * ls folder1; cd folder0; cat file3.txt
     */
    @Test
    void testMultipleValidCmdWithCd() throws Exception {
        CallCommand lsCmd = createCommand(APP_LS, FOLDERS[1]);
        CallCommand cdCmd = createCommand(APP_CD, FOLDERS[0]);
        CallCommand catCmd = createCommand(APP_CAT, FILES_IN_FOLDERS[0]);
        SequenceCommand seq = sequenceCommands(lsCmd, cdCmd, catCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = FOLDERS[1] + ":" + STRING_NEWLINE
                        + FILES_IN_FOLDERS[1] + STRING_NEWLINE
                        + FILE_3_4_CONTENTS[0] + STRING_NEWLINE;
        assertEquals(expected, results);
        assertEquals(tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS[0], Environment.currentDirectory);
    }

    /**
     * cat invalidFile.txt; cd folder0; grep +/+ file3.txt
     */
    @Test
    void testMultipleInvalidCmdWithCd() throws Exception {
        CallCommand catCmd = createCommand(APP_CAT, INVALID_FILE);
        CallCommand cdCmd = createCommand(APP_CD, FOLDERS[0]);
        CallCommand grepCmd = createCommand(APP_GREP, "+/+", FILES_IN_FOLDERS[0]);
        SequenceCommand seq = sequenceCommands(catCmd, cdCmd, grepCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = CAT_ERR_PREFIX + ERR_READING_FILE + STRING_NEWLINE
                        + GREP_ERR_PREFIX + ERR_BAD_REGEX + STRING_NEWLINE;
        assertEquals(expected, results);
        assertEquals(tempFolderAbsPath + CHAR_FILE_SEP + FOLDERS[0], Environment.currentDirectory);
    }

    /**
     * grep [23] file0.txt; cd invalid; ls
     */
    @Test
    void testMultipleCmdWithInvalidCd() throws Exception {
        CallCommand grepCmd = createCommand(APP_GREP, "2", FILES[0]);
        CallCommand cdCmd = createCommand(APP_CD, "invalid");
        CallCommand lsCmd = createCommand(APP_LS);
        SequenceCommand seq = sequenceCommands(grepCmd, cdCmd, lsCmd);

        writeToStdin("");
        seq.evaluate(stdin, stdout);

        String results = readFromStdout();
        String expected = FILE_CONTENTS[0][1] + STRING_NEWLINE
                        + CD_ERR_PREFIX + String.format(ERR_NO_SUCH_DIR, "invalid") + STRING_NEWLINE
                        + String.join(STRING_NEWLINE, FILES) + STRING_NEWLINE
                        + String.join(STRING_NEWLINE, FOLDERS) + STRING_NEWLINE;
        assertEquals(expected, results);
        assertEquals(tempFolderAbsPath, Environment.currentDirectory);
    }
}
