package sg.edu.nus.comp.cs4218.test.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.GrepArgsParser;

import static org.junit.jupiter.api.Assertions.*;

public class GrepArgsParserTest {
    private GrepArgsParser grepParser;

    @BeforeEach
    void setUp() {
        grepParser = new GrepArgsParser();
    }

    @Test
    void testNoFlags() throws InvalidArgsException {
        String[] args = {"[cd]", "A.txt", "B.txt"};
        String expectedPattern = "[cd]";
        String[] expectedFileNames = {"A.txt", "B.txt"};
        grepParser.parse(args);

        assertFalse(grepParser.isInvert());
        assertEquals(expectedPattern, grepParser.getPattern());
        assertArrayEquals(expectedFileNames, grepParser.getFileNames());
    }

    @Test
    void testNoNonFlag() throws InvalidArgsException {
        String[] args = {"-v"};
        String expectedPattern = null;
        String[] expectedFileNames = null;
        grepParser.parse(args);

        assertTrue(grepParser.isInvert());
        assertEquals(expectedPattern, grepParser.getPattern());
        assertArrayEquals(expectedFileNames, grepParser.getFileNames());
    }

    @Test
    void testNoFileNames() throws InvalidArgsException {
        String[] args = {"-v", "a"};
        String expectedPattern = "a";
        String[] expectedFileNames = null;
        grepParser.parse(args);

        assertTrue(grepParser.isInvert());
        assertEquals(expectedPattern, grepParser.getPattern());
        assertEquals(expectedFileNames, grepParser.getFileNames());
    }

    @Test
    void testSomeInvalidFlags() {
        String[] args = {"-a", "[ab]", "C.txt"};

        assertThrows(InvalidArgsException.class, () -> {
            grepParser.parse(args);
        });
    }
}
