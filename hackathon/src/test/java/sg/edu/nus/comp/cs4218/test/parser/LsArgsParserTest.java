package sg.edu.nus.comp.cs4218.test.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;

import static org.junit.jupiter.api.Assertions.*;

class LsArgsParserTest {
    private LsArgsParser sut;

    @BeforeEach
    void setUp() {
        sut = new LsArgsParser();
    }

    @Test
    void noFlagsMultipleDirectoriesTest() throws InvalidArgsException {
        String[] args = { "A", "B", "C" };
        String[] expectedNonFlags = { "A", "B", "C" };
        sut.parse(args);

        assertFalse(sut.isFoldersOnly());
        assertFalse(sut.isRecursive());
        assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
    }

    @Test
    void isFoldersOnlyTest() throws InvalidArgsException {
        String[] args = { "A", "B", "C", "-d" };
        String[] expectedNonFlags = { "A", "B", "C" };
        sut.parse(args);

        assertTrue(sut.isFoldersOnly());
        assertFalse(sut.isRecursive());
        assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
    }


    @Test
    void isRecursiveTest() throws InvalidArgsException {
        String[] args = { "A", "B", "C", "-R" };
        String[] expectedNonFlags = { "A", "B", "C" };
        sut.parse(args);

        assertTrue(sut.isRecursive());
        assertFalse(sut.isFoldersOnly());
        assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
    }

    @Test
    void isRecursiveAndFoldersOnlyTest() throws InvalidArgsException {
        String[] args = { "A", "B", "C", "-d", "-R" };
        String[] expectedNonFlags = { "A", "B", "C" };
        sut.parse(args);

        assertTrue(sut.isFoldersOnly());
        assertTrue(sut.isRecursive());
        assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
    }

    @Test
    void isFoldersOnlyInBetweenTest() throws InvalidArgsException {
        String[] args = { "A", "-d", "B", "C" };
        String[] expectedNonFlags = { "A", "B", "C" };
        sut.parse(args);

        assertTrue(sut.isFoldersOnly());
        assertFalse(sut.isRecursive());
        assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
    }

    @Test
    void multipleFlagsTest() throws InvalidArgsException {
        String[] args = { "A", "B", "C", "-Rd" };
        String[] expectedNonFlags = { "A", "B", "C" };
        sut.parse(args);

        assertTrue(sut.isFoldersOnly());
        assertTrue(sut.isRecursive());
        assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
    }

    @Test
    void evaluateValidFlagsTest() {
        String[] args = { "A", "B", "C", "-Rd" };
        String[] expectedNonFlags = { "A", "B", "C" };

        try {
            sut.parse(args);
        } catch (InvalidArgsException e) {
            fail("Evaluate not supposed to fail on valid flags");
        } finally {
            assertTrue(sut.isFoldersOnly());
            assertTrue(sut.isRecursive());
            assertArrayEquals(expectedNonFlags, sut.getDirectories().toArray());
        }
    }

    @Test
    void evaluateInvalidFlagsTest() {
        String[] args = { "A", "B", "C", "-c" };

        assertThrows(InvalidArgsException.class, () -> {
            sut.parse(args);
        });
    }
}
