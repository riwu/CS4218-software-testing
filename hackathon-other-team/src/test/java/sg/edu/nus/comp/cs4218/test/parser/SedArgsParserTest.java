package sg.edu.nus.comp.cs4218.test.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.SedArgsParser;

import static org.junit.jupiter.api.Assertions.*;

class SedArgsParserTest {
    private SedArgsParser sut;

    @BeforeEach
    void setUp() {
        sut = new SedArgsParser();
    }

    @Test
    void invalidArgCountTest() {
        String[] args = { "s/abc/def/5", "a", "b" };
        String expectedMessage = "currently only supports one file";
        assertInvalid(args, expectedMessage);
    }

    /**
     * When the sed command string has more segments than it should.
     */
    @ParameterizedTest
    @ValueSource(strings = { "s/abc/def/ghi/jkl", "s/abc/def/5/5/5" })
    void invalidCommandSegmentsTest(String arg) {
        String[] args = { arg };
        String expectedMessage = "match index not an integer";
        assertInvalid(args, expectedMessage);
    }

    /**
     * Start of the command string is an invalid flag.
     *
     * e.g. a/abc/def/ and /abc/def
     */
    @ParameterizedTest
    @ValueSource(strings = { "a/abc/def/", "/abc/def/" })
    void invalidStartCharacterTest(String arg) {
        String[] args = { arg };
        String expectedMessage = "invalid expression - start character";
        assertInvalid(args, expectedMessage);
    }

    /**
     * End of the command string does not end with slash if user did not input replacement index.
     *
     * e.g. s/abc/def and s/123/
     */
    @ParameterizedTest
    @ValueSource(strings = { "s/abc/def", "s/123" })
    void invalidEndCharacterTest(String arg) {
        String[] args = { arg };
        String expectedMessage = "invalid expression - end character";
        assertInvalid(args, expectedMessage);
    }



    /**
     * Empty regex segment of the sed command.
     *
     * e.g. s//def/
     */
    @ParameterizedTest
    @ValueSource(strings = { "s//def/", "s//def/5" })
    void emptyRegexTest(String arg) {
        String[] args = { arg };
        String expectedMessage = "char 0: no previous regular expression";
        assertInvalid(args, expectedMessage);
    }

    /**
     * Invalid regex expression in the regex segment of the sed command.
     *
     * e.g. s/[/abc/
     */
    @ParameterizedTest
    @ValueSource(strings = { "s/[/abc/" })
    void invalidRegexTest(String arg) {
        String[] args = { arg };
        String expectedMessage = "invalid regex expression";
        assertInvalid(args, expectedMessage);
    }

    /**
     * Empty replacement string segment of the sed command.
     *
     * e.g. s/abc//
     */
    @ParameterizedTest
    @ValueSource(strings = { "s/abc//", "s/abc//5" })
    void emptyReplacementStringTest(String arg) {
        String[] args = { arg };
        assertValid(args);
        assertTrue(sut.getReplacement().isEmpty());
    }

    /**
     * Presence of a special character in the replacement string.
     *
     * @param arg
     */
    @ParameterizedTest
    @ValueSource(strings = { "s/abc/*/", "s/a/*/", "s/abc/*/5" })
    void specialCharacterReplacementStringTest(String arg) {
        String[] args = { arg };
        assertValid(args);
        assertEquals(sut.getReplacement(), "*");
    }

    @ParameterizedTest
    @ValueSource(strings = { "s/abc/def/*", "s/abc/def/a" })
    void invalidMatchIndexTest(String arg) {
        String[] args = { arg };
        String expectedMessage = "match index not an integer";
        assertInvalid(args, expectedMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = { "s/abc/def/5", "s/a//5", "s/a/abc/5" })
    void validMatchIndexTest(String arg) {
        String[] args = { arg };
        assertValid(args);
        assertEquals(sut.getMatchIndex(), 5);
    }

    /**
     * Runs parse and asserts that the valid arguments do not throw exception.
     *
     * @param args
     */
    private void assertValid(String ...args) {
        try {
            sut.parse(args);
        } catch (InvalidArgsException e) {
            fail("Valid sed arguments should not throw an exception.");
        }
    }

    /**
     * Runs parse and asserts that the invalid arguments throw an exception with message.
     *
     * @param args
     * @param message - error message to be shown as part of the exception
     */
    private void assertInvalid(String[] args, String message) {
        Throwable exception = assertThrows(InvalidArgsException.class, () -> {
            sut.parse(args);
        });

        assertTrue(exception.getMessage().contains(message));
    }
}
