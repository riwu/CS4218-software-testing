package sg.edu.nus.comp.cs4218.test.integration.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.test.integration.IntegrationTestEnvironment;
import sg.edu.nus.comp.cs4218.test.integration.params.PipeTestParams;
import sg.edu.nus.comp.cs4218.test.integration.params.PipeTwoTestParam;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.exception.CatException.CAT_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.exception.GrepException.GREP_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.exception.LsException.LS_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.exception.PasteException.PASTE_ERR_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.app.CatApplication.ERR_READING_FILE;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.ERR_BAD_REGEX;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.ERR_NO_SUCH_FILE;
import static sg.edu.nus.comp.cs4218.impl.app.LsApplication.ERR_INVALID_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PipeIntegrationTest extends IntegrationTestEnvironment {
    private static final String TEST_DIR = "pipe-integration-test";

    private static final String A_TXT = "a.txt";
    private static final String B_TXT = "b.txt";
    private static final String DIFF1_TXT = "diff1.txt";
    private static final String DIFF2_TXT = "diff2.txt";
    private static final String SPACE = "s p a c e";

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Set current directory to TEST_DIR
        Environment.currentDirectory = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                                 ENV_ROOT,
                                                 TEST_DIR).toString();
    }

    @ParameterizedTest
    @MethodSource("validLsGrepProvider")
    void validLsGrepTest(PipeTwoTestParam params) {
        assertValidCommand(params);
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("invalidLsGrepProvider")
    void invalidLsGrepTest(PipeTwoTestParam params) {
        assertInvalidCommand(params);
    }

    @ParameterizedTest
    @MethodSource("validLsCatProvider")
    void validLsCatTest(PipeTwoTestParam params) {
        assertValidCommand(params);
    }

    @ParameterizedTest
    @MethodSource("validLsDiffProvider")
    void validLsDiffTest(PipeTwoTestParam params) {
        assertValidCommand(params);
    }

    @ParameterizedTest
    @MethodSource("validLsPasteProvider")
    void validLsPasteTest(PipeTwoTestParam params) {
        assertValidCommand(params);
    }

    @ParameterizedTest
    @MethodSource("validLsSedProvider")
    void validLsSedTest(PipeTwoTestParam params) {
        assertValidCommand(params);
    }

    @ParameterizedTest
    @MethodSource("validCatGrepProvider")
    void validCatGrepTest(PipeTestParams param) {
        assertValidMultiPipedCommand(param);
    }

    @ParameterizedTest
    @MethodSource("pipeThreeCommandsProvider")
    void pipeThreeCommandsTest(PipeTestParams param) {
        assertValidMultiPipedCommand(param);
    }

    /**
     * Test cases for valid ls | grep integration test cases.
     *
     * @return Stream of test parameters to be used by a ParameterizedTest.
     */
    private static Stream<PipeTwoTestParam> validLsGrepProvider() {
        String app1 = ApplicationRunner.APP_LS;
        String app2 = ApplicationRunner.APP_GREP;

        List<PipeTwoTestParam> testParams = new ArrayList<>();
        PipeTwoTestParam param;

        // Test Case #1: Happy path
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.emptyList());
        param.setArgs2(Collections.singletonList(A_TXT));
        param.setExpected(A_TXT + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #2: ls a relative path
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.singletonList("."));
        param.setArgs2(Collections.singletonList(B_TXT));
        param.setExpected(B_TXT + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Test cases for invalid ls | grep tests.
     *
     * @return Stream of test parameters
     */
    private static Stream<PipeTwoTestParam> invalidLsGrepProvider() {
        String app1 = ApplicationRunner.APP_LS;
        String app2 = ApplicationRunner.APP_GREP;

        List<PipeTwoTestParam> testParams = new ArrayList<>();
        PipeTwoTestParam param;

        // Test Case #1: ls an invalid directory, an exception should still be thrown.
        // TODO: This is failing now.
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.singletonList("invalid"));
        param.setArgs2(Collections.singletonList(A_TXT));
        param.setExpected("ls: ");
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Test cases for valid ls | cat tests.
     *
     * Suppress PMD because this method has to be long!
     *
     * @return Stream of test parameters
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static Stream<PipeTwoTestParam> validLsCatProvider() {
        String app1 = ApplicationRunner.APP_LS;
        String app2 = ApplicationRunner.APP_CAT;

        List<PipeTwoTestParam> testParams = new ArrayList<>();
        PipeTwoTestParam param;

        // Test Case #1: Happy path
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.emptyList());
        param.setArgs2(Collections.emptyList());
        param.setExpected(
                A_TXT + StringUtils.STRING_NEWLINE +
                        B_TXT + StringUtils.STRING_NEWLINE +
                        DIFF1_TXT + StringUtils.STRING_NEWLINE +
                        DIFF2_TXT + StringUtils.STRING_NEWLINE +
                        SPACE + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #2: Happy path with relative path
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.singletonList("."));
        param.setArgs2(Collections.emptyList());
        param.setExpected(
                "./:" + StringUtils.STRING_NEWLINE +
                        A_TXT + StringUtils.STRING_NEWLINE +
                        B_TXT + StringUtils.STRING_NEWLINE +
                        DIFF1_TXT + StringUtils.STRING_NEWLINE +
                        DIFF2_TXT + StringUtils.STRING_NEWLINE +
                        SPACE + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // NOTE: Test cases below are actually invalid, but the way ls handles things now is that it
        // does not throw an Exception, but rather write to STDOUT when a file is invalid.
        // This is such that we can list the contents of the directories of other files.

        // We can remove this functionality such that ls will simply FAIL if one of the arguments is
        // invalid.

        // Test Case #3: One invalid path from ls
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.singletonList("invalid"));
        param.setArgs2(Collections.emptyList());
        param.setExpected("ls: cannot access 'invalid': No such file or directory" +
                                  StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #4: One invalid path from ls, another valid path from ls
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Arrays.asList("invalid", "."));
        param.setArgs2(Collections.emptyList());
        param.setExpected("ls: cannot access 'invalid': No such file or directory" +
                                  StringUtils.STRING_NEWLINE +
                                  "./:" + StringUtils.STRING_NEWLINE +
                                  A_TXT + StringUtils.STRING_NEWLINE +
                                  B_TXT + StringUtils.STRING_NEWLINE +
                                  DIFF1_TXT + StringUtils.STRING_NEWLINE +
                                  DIFF2_TXT + StringUtils.STRING_NEWLINE +
                                  SPACE + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #5: Both invalid path from ls
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Arrays.asList("invalid1", "invalid2"));
        param.setArgs2(Collections.emptyList());
        param.setExpected("ls: cannot access 'invalid1': No such file or directory" +
                                  StringUtils.STRING_NEWLINE +
                                  "ls: cannot access 'invalid2': No such file or directory" +
                                  StringUtils.STRING_NEWLINE);
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Valid test cases for ls | diff.
     *
     * @return Stream of test case parameters
     */
    private static Stream<PipeTwoTestParam> validLsDiffProvider() {
        String app1 = ApplicationRunner.APP_LS;
        String app2 = ApplicationRunner.APP_DIFF;

        List<PipeTwoTestParam> testParams = new ArrayList<>();
        PipeTwoTestParam param;

        // Test Case #1: Happy path - no difference
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.emptyList());
        param.setArgs2(Arrays.asList("-", DIFF1_TXT));
        param.setExpected("");
        testParams.add(param);

        // Test Case #2: Happy path - difference
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.emptyList());
        param.setArgs2(Arrays.asList(DIFF2_TXT, "-"));
        param.setExpected("<extra");
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Valid test cases for ls | paste
     */
    private static Stream<PipeTwoTestParam> validLsPasteProvider() {
        String app1 = ApplicationRunner.APP_LS;
        String app2 = ApplicationRunner.APP_PASTE;

        List<PipeTwoTestParam> testParams = new ArrayList<>();
        PipeTwoTestParam param;

        // Test Case #1: Happy path - paste output of ls with the contents of ls itself in DIFF1
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.emptyList());
        param.setArgs2(Arrays.asList(DIFF1_TXT, "-"));
        StringBuilder expected = new StringBuilder(32);
        expected.append(A_TXT).append(StringUtils.CHAR_TAB)
                .append(A_TXT).append(StringUtils.STRING_NEWLINE)
                .append(B_TXT).append(StringUtils.CHAR_TAB)
                .append(B_TXT).append(StringUtils.STRING_NEWLINE)
                .append(DIFF1_TXT).append(StringUtils.CHAR_TAB)
                .append(DIFF1_TXT).append(StringUtils.STRING_NEWLINE)
                .append(DIFF2_TXT).append(StringUtils.CHAR_TAB)
                .append(DIFF2_TXT).append(StringUtils.STRING_NEWLINE)
                .append(SPACE).append(StringUtils.CHAR_TAB)
                .append(SPACE).append(StringUtils.STRING_NEWLINE);
        param.setExpected(expected.toString());
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Valid ls | sed test cases.
     *
     * @return Stream of valid test cases
     */
    private static Stream<PipeTwoTestParam> validLsSedProvider() {
        String app1 = ApplicationRunner.APP_LS;
        String app2 = ApplicationRunner.APP_SED;

        List<PipeTwoTestParam> testParams = new ArrayList<>();
        PipeTwoTestParam param;

        // Test Case #1: Happy path - replace one of the output of ls with another string
        param = new PipeTwoTestParam(app1, app2);
        param.setArgs1(Collections.emptyList());
        param.setArgs2(Collections.singletonList(String.format("s/%s/REPLACED/", DIFF1_TXT)));
        StringBuilder expected = new StringBuilder();
        expected.append(A_TXT).append(StringUtils.STRING_NEWLINE)
                .append(B_TXT).append(StringUtils.STRING_NEWLINE)
                .append("REPLACED").append(StringUtils.STRING_NEWLINE)
                .append(DIFF2_TXT).append(StringUtils.STRING_NEWLINE)
                .append(SPACE).append(StringUtils.STRING_NEWLINE);
        param.setExpected(expected.toString());
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Valid cat | grep test cases.
     *
     * @return Stream of valid test cases
     */
    private static Stream<PipeTestParams> validCatGrepProvider() {
        String app1 = ApplicationRunner.APP_CAT;
        String app2 = ApplicationRunner.APP_GREP;

        List<String> args1;
        List<String> args2;
        List<PipeTestParams> testParams = new ArrayList<>();
        PipeTestParams param;

        // Test Case #1: cat diff1.txt | grep diff1.txt
        args1 = Collections.singletonList(DIFF1_TXT);
        args2 = Collections.singletonList(DIFF1_TXT);
        param = new PipeTestParams(app1, app2);
        param.setArgs(0, args1);
        param.setArgs(1, args2);
        param.setExpected(DIFF1_TXT + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #2: cat diff1.txt | grep diff2.txt
        args1 = Collections.singletonList(DIFF1_TXT);
        args2 = Collections.singletonList("missing");
        param = new PipeTestParams(app1, app2);
        param.setArgs(0, args1);
        param.setArgs(1, args2);
        param.setExpected("");
        testParams.add(param);

        return testParams.stream();
    }

    /**
     * Test cases that links three pipe commands together.
     *
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static Stream<PipeTestParams> pipeThreeCommandsProvider() {
        String app1;
        String app2;
        String app3;
        List<String> args1;
        List<String> args2;
        List<String> args3;
        List<PipeTestParams> testParams = new ArrayList<>();
        PipeTestParams param;

        // Test Case #1: echo HELLO | sed s/EL/OO/ | grep HOOLO
        app1 = ApplicationRunner.APP_ECHO;
        args1 = Collections.singletonList("HELLO");
        app2 = ApplicationRunner.APP_SED;
        args2 = Collections.singletonList("s/EL/OO/");
        app3 = ApplicationRunner.APP_GREP;
        args3 = Collections.singletonList("HOOLO");

        param = new PipeTestParams(app1, app2, app3);
        param.setArgs(0, args1);
        param.setArgs(1, args2);
        param.setArgs(2, args3);
        param.setExpected("HOOLO" + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #2: ls | grep a.txt | sed s/a/b/
        app1 = ApplicationRunner.APP_LS;
        args1 = Collections.emptyList();
        app2 = ApplicationRunner.APP_GREP;
        args2 = Collections.singletonList(A_TXT);
        app3 = ApplicationRunner.APP_SED;
        args3 = Collections.singletonList("s/a/b/");

        param = new PipeTestParams(app1, app2, app3);
        param.setArgs(0, args1);
        param.setArgs(1, args2);
        param.setArgs(2, args3);
        param.setExpected("b.txt" + StringUtils.STRING_NEWLINE);
        testParams.add(param);

        // Test Case #3: cat diff1.txt | diff - diff2.txt | cat
        app1 = ApplicationRunner.APP_CAT;
        args1 = Collections.singletonList(DIFF1_TXT);
        app2 = ApplicationRunner.APP_DIFF;
        args2 = Arrays.asList("-", DIFF2_TXT);
        app3 = ApplicationRunner.APP_CAT;
        args3 = Collections.emptyList();

        param = new PipeTestParams(app1, app2, app3);
        param.setArgs(0, args1);
        param.setArgs(1, args2);
        param.setArgs(2, args3);
        param.setExpected("<extra");
        testParams.add(param);

        // Test Case #4: ls | cmp diff1.txt - | grep byte
        app1 = ApplicationRunner.APP_LS;
        args1 = Collections.emptyList();
        app2 = ApplicationRunner.APP_CMP;
        args2 = Arrays.asList(DIFF1_TXT, "-");
        app3 = ApplicationRunner.APP_GREP;
        args3 = Collections.singletonList("byte");

        param = new PipeTestParams(app1, app2, app3);
        param.setArgs(0, args1);
        param.setArgs(1, args2);
        param.setArgs(2, args3);
        param.setExpected("");
        testParams.add(param);

        return testParams.stream();
    }
}
