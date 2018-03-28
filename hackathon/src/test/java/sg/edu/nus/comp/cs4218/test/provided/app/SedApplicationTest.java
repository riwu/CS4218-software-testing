package sg.edu.nus.comp.cs4218.test.provided.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.SedApplication;
import sg.edu.nus.comp.cs4218.test.TestEnvironment;

import java.io.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SedApplicationTest extends TestEnvironment {
    private static final String TEST_DIR = "provided" + File.separator + "sed-test";

    private static final String STRING_TEST = "test";
    private static final String STRING_REPLACE = "replaced";
    private static final String STRING_STREAM = "stream";
    private static final String STRING_COMMAND = "s/test/replaced/";

    private static final String TESTFILE1 = "sedTestFile1.txt";
    private static final String TESTFILE2 = "sedTestFile2.txt";
    private static final String TESTFILE1_HEADER = "This is Sed Test File 1.\n";
    private static final String TESTFILE2_HEADER = "This is Sed Test File 2.\n";

    private static final String EXP_NULL_POINTER = "sed: Null Pointer Exception";
    private static final String NULL_PATTERN = "sed: null or empty pattern not allowed.";
    private static final String NULL_FILE = "sed: null file not allowed.";
    private static final String NULL_REPLACEMENT = "sed: null replacement not allowed.";

    private SedApplication sut;
    private InputStream stdin;
    private OutputStream stdout;
    private String expected;
    private String result;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        sut = new SedApplication();
        stdin = new ByteArrayInputStream("Text from Inputstream\nstream of texts in stream".getBytes());
        stdout = new ByteArrayOutputStream();

        // Set current directory to TEST_DIR
        Environment.currentDirectory = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                                 ENV_ROOT,
                                                 TEST_DIR).toString();
    }

    @Test
    public void testReplaceThirdOccuranceInFile() {
        expected = TESTFILE2_HEADER +
                "1. testestestesreplacedest\n" +
                "2. testestestest\n" +
                "3. test test replaced test\n" +
                "4. test test replaced\n" +
                "5. test test\n" +
                "6. test\n";
        try {
            result = sut.replaceSubstringInFile(STRING_TEST, STRING_REPLACE, 3, TESTFILE2);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testRegexMatchAddLeadBracketInFile() {
        expected = "> This is Sed Test File 1.\n" +
                "> 1. test\n" +
                "> 2. test test\n" +
                "> 3. test test test\n" +
                "> 4. test test test test\n" +
                "> 5. testestestest\n" +
                "> 6. testestestestestest\n";
        try {
            result = sut.replaceSubstringInFile("^", "> ", -1, TESTFILE1);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testRegexMatchRemoveLeadNumInFile() {
        expected = TESTFILE2_HEADER +
                "testestestestestest\n" +
                "testestestest\n" +
                "test test test test\n" +
                "test test test\n" +
                "test test\n" +
                "test\n";
        try {
            result = sut.replaceSubstringInFile("[0-9].\\s+", "", -1, TESTFILE2);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testNoRegexMatchInFile() {
        expected = TESTFILE1_HEADER +
                "1. test\n" +
                "2. test test\n" +
                "3. test test test\n" +
                "4. test test test test\n" +
                "5. testestestest\n" +
                "6. testestestestestest\n";
        try {
            result = sut.replaceSubstringInFile("NoMatched", STRING_REPLACE, -1, TESTFILE1);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullPatternInFile() {
        expected = NULL_PATTERN;
        try {
            result = sut.replaceSubstringInFile(null, STRING_REPLACE, -1, TESTFILE1);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidEmptyPatternInFile() {
        expected = NULL_PATTERN;
        try {
            result = sut.replaceSubstringInFile("", STRING_REPLACE, -1, TESTFILE1);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullReplacementInFile() {
        expected = NULL_REPLACEMENT;
        try {
            result = sut.replaceSubstringInFile(STRING_TEST, null, -1, TESTFILE1);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullFile() {
        expected = NULL_FILE;
        try {
            result = sut.replaceSubstringInFile(STRING_TEST, STRING_REPLACE, -1, null);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNonExistentFile() {
        expected = "sed: can't read nonExistentFile: No such file or directory";
        try {
            result = sut.replaceSubstringInFile(STRING_TEST, STRING_REPLACE, -1, "nonExistentFile");
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidEmptyFileName() {
        expected = "sed: can't read : No such file or directory";
        try {
            result = sut.replaceSubstringInFile(STRING_TEST, STRING_REPLACE, -1, "");
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidSpacesFileName() {
        expected = "sed: can't read    : No such file or directory";
        try {
            result = sut.replaceSubstringInFile(STRING_TEST, STRING_REPLACE, -1, "   ");
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidFolderReplace() {
        expected = "sed: can't read folder1: No such file or directory";

        try {
            result = sut.replaceSubstringInFile(STRING_TEST, STRING_REPLACE, -1, "folder1");
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testReplaceFromInputStream() {
        expected = "Text from Inputreplaced\nreplaced of texts in stream\n";
        try {
            result = sut.replaceSubstringInStdin(STRING_STREAM, STRING_REPLACE, 1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testReplaceSecondOccuranceInStream() {
        expected = "Text from Inputstream\nstream of texts in replaced\n";
        try {
            result = sut.replaceSubstringInStdin(STRING_STREAM, STRING_REPLACE, 2, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testRegexMatchAddLeadBracketInStream() {
        expected = "> Text from Inputstream\n> stream of texts in stream\n";
        try {
            result = sut.replaceSubstringInStdin("^", "> ", -1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testRegexMatchRemoveTextInStream() {
        expected = "Text from Input\nof texts in stream\n";
        try {
            result = sut.replaceSubstringInStdin("\\s*stream\\s*", "", 1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testNoRegexMatchInStream() {
        expected = "Text from Inputstream\nstream of texts in stream\n";
        try {
            result = sut.replaceSubstringInStdin("noMatched", STRING_REPLACE, 1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullPatternInStream() {
        expected = NULL_PATTERN;
        try {
            result = sut.replaceSubstringInStdin(null, STRING_REPLACE, 1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidEmptyPatternInStream() {
        expected = NULL_PATTERN;
        try {
            result = sut.replaceSubstringInStdin("", STRING_REPLACE, -1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullReplacementInStream() {
        expected = NULL_REPLACEMENT;
        try {
            result = sut.replaceSubstringInStdin(STRING_STREAM, null, -1, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidReplaceIndexZeroInStream() {
        expected = "sed: zero replacement index not allowed.";
        try {
            result = sut.replaceSubstringInStdin(STRING_STREAM, STRING_REPLACE, 0, stdin);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullStream() {
        expected = "sed: null stdin not allowed.";
        try {
            result = sut.replaceSubstringInStdin(null, STRING_REPLACE, -1, null);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testOtherSeparatingCharNumeric() {
        expected = TESTFILE1_HEADER +
                "1. replaced\n" +
                "2. replaced test\n" +
                "3. replaced test test\n" +
                "4. replaced test test test\n" +
                "5. replacedestestest\n" +
                "6. replacedestestestestest\n";
        try {
            String[] strArr = {"s1test1replaced1", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testOtherSeparatingCharAlpha() {
        expected = "Text from Inputreplaced\nreplaced of texts in stream\n";
        try {
            String[] strArr = {"szstreamzreplacedz"};
            sut.run(strArr, stdin, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidEmptyArguments() {
        expected = "sed: invalid number of arguments";
        try {
            String[] strArr = {};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidCommand() {
        expected = "sed: invalid length of command string";
        try {
            String[] strArr = {"i", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidMissingSeparatingChar() {
        expected = "sed: invalid length of command string";
        try {
            String[] strArr = {"s", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidUnterminatedCommand() {
        expected = "sed: invalid expression - end character";
        try {
            String[] strArr = {"s/s", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidEmptyRegExp() {
        expected = "sed: char 0: no previous regular expression";
        try {
            String[] strArr = {"s//replaced/", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidReplaceIndexChar() {
        expected = "sed: match index not an integer ???";
        try {
            String[] strArr = {"s/test/replaced/???", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidReplaceIndex() {
        expected = "sed: match index may not be zero";
        try {
            String[] strArr = {"s/test/replaced/0", TESTFILE1};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidCommandPosition() {
        expected = "sed: match index not an integer 1.txt";
        try {
            String[] strArr = {TESTFILE1, STRING_COMMAND};
            sut.run(strArr, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    public void testInvalidNullArgs() {
        expected = "sed: invalid number of arguments";
        try {
            sut.run(null, null, stdout);
            result = stdout.toString();
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }

    @Test
    void testInvalidNullOutputStream() {
        expected = "sed: cannot write to stdout.";
        try {
            String[] strArr = {STRING_COMMAND, TESTFILE1};
            sut.run(strArr, null, null);
        } catch (SedException e) {
            result = e.getMessage();
        }
        assertEquals(expected, result);
    }
}
