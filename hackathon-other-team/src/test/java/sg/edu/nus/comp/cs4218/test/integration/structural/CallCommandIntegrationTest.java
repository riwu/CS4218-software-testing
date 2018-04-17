package sg.edu.nus.comp.cs4218.test.integration.structural;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.test.FileSystemTest;
import sg.edu.nus.comp.cs4218.test.stub.ApplicationRunnerStub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

/**
 * This integration test suite ensures that CallCommand correctly evaluates the following shell
 * functionalities and their interactions:
 *
 * Handled by IORedirectionHandler
 * - IO redirection
 *
 * Handled by ArgumentResolver
 * - quoting
 * - globing
 * - command substitution
 */
class CallCommandIntegrationTest extends FileSystemTest {
    private static final String APP = "app";
    private static final String[] ARGS = {"arg1", "arg2", "arg3"};
    private static final InputStream STDIN = System.in;
    private static final OutputStream STDOUT = System.out;

    private static final String FOLDER_CREATED = "created folder";
    private static final String NESTED_FOLDER_1 = "nested folder 1";
    private static final String NESTED_FOLDER_2 = "nested folder 2";
    private static final String NESTED_FILE_1 = "nested file 1";
    private static final String NESTED_FILE_2 = "nested file 2";
    private static final String FILE_CREATED_1 = "valid_file 1.txt";
    private static final String FILE_CREATED_2 = "valid_file 2.txt";
    private static final String FILE_CONTENTS_1 = "contents of created file 1";
    private static final String FILE_CONTENTS_2 = "contents of created file 2";
    private static final String FILE_NOT_CREATED = "invalid_file.txt";

    private static final String STR_REDIR_INPUT = String.valueOf(CHAR_REDIR_INPUT);
    private static final String STR_REDIR_OUTPUT = String.valueOf(CHAR_REDIR_OUTPUT);
    private static final String STR_ASTERISK = String.valueOf(CHAR_ASTERISK);
    private static final String MSG_SYNTAX = "shell: " + ERR_SYNTAX;

    private final ApplicationRunnerStub appRunnerStub = new ApplicationRunnerStub();
    private List<String> argsList;
    private CallCommand sut;

    @BeforeEach
    void setUp() throws IOException {
        Environment.currentDirectory = tempFolder.getRoot().getPath();

        tempFolder.newFolder(FOLDER_CREATED);
        tempFolder.newFolder(FOLDER_CREATED, NESTED_FOLDER_1);
        tempFolder.newFolder(FOLDER_CREATED, NESTED_FOLDER_2);
        tempFolder.newFile(
                FOLDER_CREATED + CHAR_FILE_SEP + NESTED_FOLDER_1 + CHAR_FILE_SEP + NESTED_FILE_1);
        tempFolder.newFile(
                FOLDER_CREATED + CHAR_FILE_SEP + NESTED_FOLDER_2 + CHAR_FILE_SEP + NESTED_FILE_2);
        createTestFileWithContents(FILE_CREATED_1, FILE_CONTENTS_1);
        createTestFileWithContents(FILE_CREATED_2, FILE_CONTENTS_2);
    }

    /**
     * Quoting
     */
    // Defensive check: CallCommand will not receive arguments with mismatched quotes
    @Test
    void testMismatchedSingleQuotes() {
        argsList = Arrays.asList(APP, "'arg1");

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Quoting
     */
    // Defensive check: CallCommand will not receive arguments with mismatched quotes
    @Test
    void testMismatchedDoubleQuotes() {
        argsList = Arrays.asList(APP, "\"arg1");

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Quoting
     */
    @Test
    void testSimpleSingleAndDoubleQuotes() throws Exception {
        argsList = Arrays.asList("'app'\"\"", "\"arg 1\"", "' 'arg\"2\"", "\"ar\"'g 3 '");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals("app", appRunnerStub.getApp());
        assertArrayEquals(new String[]{"arg 1", " arg2", "arg 3 "}, appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Quoting
     */
    @Test
    void testNestedSingleAndDoubleQuotes() throws Exception {
        argsList = Arrays.asList(APP, "\"'arg 1'\"", "\"arg'2\"", "ar'g \"3 '");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{"'arg 1'", "arg'2", "arg \"3 "},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Command Substitution
     */
    // Defensive check: CallCommand will not receive arguments with mismatched quotes
    @Test
    void testMismatchedBackQuotes() {
        argsList = Arrays.asList(APP, "`arg1");

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Command Substitution + Quoting
     */
    @Test
    void testBackQuotesNestedInSingleQuotes() throws Exception {
        argsList = Arrays.asList(APP, "'`arg1`'", "'arg `2'", "arg3' ` `'");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{"`arg1`", "arg `2", "arg3 ` `"},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Command Substitution + Quoting
     */
    // Defensive check: CallCommand will not receive arguments with mismatched quotes
    @Test
    void testMismatchedBackQuotesNestedInDoubleQuotes() {
        argsList = Arrays.asList(APP, "\"arg`1\"`");

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Command Substitution + Quoting
     */
    @Test
    void testMismatchedBackQuoteNestedInDoubleQuotes() {
        argsList = Arrays.asList(APP, "\"arg`1\"");

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Command Substitution + Quoting
     * TODO don't use echo to resolve subcommand
     */
    @Test
    void testBackQuotesNestedInDoubleQuotes() throws Exception {
        argsList = Arrays.asList(APP, "\"`echo hello`\"", "\"123 `echo \"hello world\"` \"");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{"hello ", "123 hello world  "},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Command Substitution
     * TODO don't use echo to resolve subcommand
     */
    @Test
    void testCommandSubstitutionWithoutApp() throws Exception {
        argsList = Arrays.asList("`echo app hello world`");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{"hello", "world"}, appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectInputOnly() throws Exception {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, FILE_CREATED_1);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertNotEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(FILE_CONTENTS_1, appRunnerStub.getInputStreamString());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectOutputOnly() throws Exception {
        argsList = Arrays.asList(APP, STR_REDIR_OUTPUT, FILE_CREATED_1);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertNotEquals(STDOUT, appRunnerStub.getOutputStream());
        assertEquals(ApplicationRunnerStub.STUBBED_OUTPUT, getContentsFromTestFile(FILE_CREATED_1));
    }

    /**
     * IO Redirection + Quoting
     */
    @Test
    void testRedirectWithQuotes() throws Exception {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, "'" + FILE_CREATED_1 + "'");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertNotEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(FILE_CONTENTS_1, appRunnerStub.getInputStreamString());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectInputAndOutput() throws Exception {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, FILE_CREATED_1, STR_REDIR_OUTPUT,
                                 FILE_CREATED_2);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertNotEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(FILE_CONTENTS_1, appRunnerStub.getInputStreamString());
        assertNotEquals(STDOUT, appRunnerStub.getOutputStream());
        assertEquals(ApplicationRunnerStub.STUBBED_OUTPUT, getContentsFromTestFile(FILE_CREATED_2));
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectInputFileDoesNotExist() {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, FILE_NOT_CREATED);

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals("shell: " + IOUtils.resolveFilePath(FILE_NOT_CREATED).toString()
                             + " (No such file or directory)",
                     exception.getMessage());
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectOutputFileDoesNotExist() throws Exception {
        argsList = Arrays.asList(APP, STR_REDIR_OUTPUT, FILE_NOT_CREATED);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertNotEquals(STDOUT, appRunnerStub.getOutputStream());
        assertTrue(Files.exists(IOUtils.resolveFilePath(FILE_NOT_CREATED)));
        assertEquals(ApplicationRunnerStub.STUBBED_OUTPUT,
                     getContentsFromTestFile(FILE_NOT_CREATED));
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectInputWithoutSpecifyingFile() {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT);

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * IO Redirection
     */
    @Test
    void testRedirectOutputWithoutSpecifyingFile() {
        argsList = Arrays.asList(APP, STR_REDIR_OUTPUT);

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * IO Redirection
     */
    @Test
    void testConsecutiveRedirectOperators() {
        argsList = Arrays.asList(APP, STR_REDIR_OUTPUT, STR_REDIR_INPUT, ARGS[0]);

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * IO Redirection + Globing
     */
    @Test
    void testAmbiguousRedirectWithGlobing() {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, "val" + STR_ASTERISK);

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * IO Redirection + Globing
     */
    @Test
    void testRedirectWithGlobing() throws Exception {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, "valid" + STR_ASTERISK + "2" + STR_ASTERISK);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[0], appRunnerStub.getArgsArray());
        assertNotEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(FILE_CONTENTS_2, appRunnerStub.getInputStreamString());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * IO Redirection + Command Substitution
     */
    @Test
    void testAmbiguousRedirectWithCommandSubstitution() {
        argsList = Arrays.asList(APP, STR_REDIR_INPUT, "`echo ambiguous redirect`");

        sut = new CallCommand(argsList, appRunnerStub);

        Throwable exception = assertThrows(ShellException.class, () -> {
            sut.evaluate(STDIN, STDOUT);
        });
        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Globing
     */
    @Test
    void testNoGlobingWhenAsteriskInQuotes() throws Exception {
        argsList = Arrays.asList(APP, "'" + STR_ASTERISK + "'", ARGS[0], ARGS[1], ARGS[2]);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{STR_ASTERISK, ARGS[0], ARGS[1], ARGS[2]},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingWithoutMatchingFiles() throws Exception {
        argsList = Arrays.asList(APP, ARGS[0] + STR_ASTERISK, ARGS[1], ARGS[2]);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{ARGS[0] + STR_ASTERISK, ARGS[1], ARGS[2]},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingMatchWithoutExpansion() throws Exception {
        argsList = Arrays.asList(APP, FILE_CREATED_1 + STR_ASTERISK, ARGS[0], ARGS[1], ARGS[2]);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FILE_CREATED_1, ARGS[0], ARGS[1], ARGS[2]},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingMatchWithExpansion() throws Exception {
        argsList = Arrays.asList(APP, "valid" + STR_ASTERISK, ARGS[0], "v" + STR_ASTERISK + ".txt");

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FILE_CREATED_1, FILE_CREATED_2, ARGS[0], FILE_CREATED_1,
                                       FILE_CREATED_2},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingMatchMultipleAsterisks() throws Exception {
        argsList = Arrays.asList(APP, "va" + STR_ASTERISK + "lid" + STR_ASTERISK + STR_ASTERISK);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FILE_CREATED_1, FILE_CREATED_2},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingMatchAll() throws Exception {
        argsList = Arrays.asList(APP, STR_ASTERISK, ARGS[0], ARGS[1], ARGS[2]);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FOLDER_CREATED, FILE_CREATED_1, FILE_CREATED_2, ARGS[0],
                                       ARGS[1], ARGS[2]},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingMatchAllWithoutSpecifyingApp() throws Exception {
        argsList = Arrays.asList(STR_ASTERISK, ARGS[0], ARGS[1], ARGS[2]);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(FOLDER_CREATED, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FILE_CREATED_1, FILE_CREATED_2, ARGS[0], ARGS[1], ARGS[2]},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Test
    void testGlobingFoldersWithoutSlash() throws Exception {
        argsList = Arrays.asList(APP, FOLDER_CREATED + CHAR_ASTERISK);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FOLDER_CREATED}, appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Disabled
    @Test
    void testGlobingFoldersWithSlash() throws Exception {
        argsList = Arrays.asList(APP, FOLDER_CREATED + CHAR_FILE_SEP + CHAR_ASTERISK);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FOLDER_CREATED + CHAR_FILE_SEP + NESTED_FOLDER_1,
                                       FOLDER_CREATED + CHAR_FILE_SEP + NESTED_FOLDER_2},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }

    /**
     * Globing
     */
    @Disabled
    @Test
    void testGlobingNestedFolders() throws Exception {
        argsList = Arrays.asList(APP, FOLDER_CREATED + CHAR_FILE_SEP + CHAR_ASTERISK
                + CHAR_FILE_SEP + CHAR_ASTERISK);

        sut = new CallCommand(argsList, appRunnerStub);
        sut.evaluate(STDIN, STDOUT);

        assertEquals(APP, appRunnerStub.getApp());
        assertArrayEquals(new String[]{FOLDER_CREATED + CHAR_FILE_SEP + NESTED_FOLDER_1
                                               + CHAR_FILE_SEP + NESTED_FILE_1,
                                       FOLDER_CREATED + CHAR_FILE_SEP + NESTED_FOLDER_2
                                               + CHAR_FILE_SEP + NESTED_FILE_2},
                          appRunnerStub.getArgsArray());
        assertEquals(STDIN, appRunnerStub.getInputStream());
        assertEquals(STDOUT, appRunnerStub.getOutputStream());
    }
}
