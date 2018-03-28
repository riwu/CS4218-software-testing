package sg.edu.nus.comp.cs4218.test.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.DiffArgsParser;

import static org.junit.jupiter.api.Assertions.*;

public class DiffArgsParserTest {
    private final static String FLAG_IS_SAME = "-s";
    private final static String FLAG_IS_NO_BLANK = "-B";
    private final static String FLAG_IS_SIMPLE = "-q";

    private final static String FILE_A = "fileA";
    private final static String FILE_B = "fileB";
    private final static String STDIN = "-";

    private DiffArgsParser sut;

    @BeforeEach
    void setUp() {
        sut = new DiffArgsParser();
    }

    @Test
    void validArgsTest0() throws InvalidArgsException {
        String[] args = { FILE_A, FILE_B };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertFalse(sut.isNoBlank());
        assertFalse(sut.isSame());
        assertFalse(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(FILE_B, sut.getSecond());
    }

    @Test
    void validArgsTest1() throws InvalidArgsException {
        String[] args = { FILE_A, FILE_B, FLAG_IS_SAME };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertFalse(sut.isNoBlank());
        assertTrue(sut.isSame());
        assertFalse(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(FILE_B, sut.getSecond());
    }

    @Test
    void validArgsTest2() throws InvalidArgsException {
        String[] args = { FILE_A, FILE_B, FLAG_IS_SAME, FLAG_IS_NO_BLANK };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertTrue(sut.isNoBlank());
        assertTrue(sut.isSame());
        assertFalse(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(FILE_B, sut.getSecond());
    }

    @Test
    void validArgsTest3() throws InvalidArgsException {
        String[] args = { FILE_A, FILE_B, FLAG_IS_SAME, FLAG_IS_NO_BLANK, FLAG_IS_SIMPLE };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertTrue(sut.isNoBlank());
        assertTrue(sut.isSame());
        assertTrue(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(FILE_B, sut.getSecond());
    }

    @Test
    void validArgsTest4() throws InvalidArgsException {
        String[] args = { FILE_A, FILE_B, "-sBq" };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertTrue(sut.isNoBlank());
        assertTrue(sut.isSame());
        assertTrue(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(FILE_B, sut.getSecond());
    }

    @Test
    void validArgsTest5() throws InvalidArgsException {
        String[] args = { FILE_A, STDIN };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertFalse(sut.isNoBlank());
        assertFalse(sut.isSame());
        assertFalse(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(STDIN, sut.getSecond());
    }

    @Test
    void validArgsTest6() throws InvalidArgsException {
        String[] args = { FILE_A, "-", FLAG_IS_SAME, FLAG_IS_NO_BLANK, FLAG_IS_SIMPLE };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertTrue(sut.isNoBlank());
        assertTrue(sut.isSame());
        assertTrue(sut.isSimple());

        assertEquals(FILE_A, sut.getFirst());
        assertEquals(STDIN, sut.getSecond());
    }

    @Test
    void validArgsTest7() throws InvalidArgsException {
        String[] args = { STDIN, STDIN };
        sut.parse(args);

        assertNoExceptionsOnEvaluate();
        assertFalse(sut.isNoBlank());
        assertFalse(sut.isSame());
        assertFalse(sut.isSimple());

        assertEquals(STDIN, sut.getFirst());
        assertEquals(STDIN, sut.getSecond());
    }

    @Test
    void singleNonFlagArgTest0() {
        String[] args = { FILE_A };

        assertThrows(InvalidArgsException.class, () -> {
            sut.parse(args);
        });
    }

    @Test
    void singleNonFlagArgTest1() {
        String[] args = { FILE_A, "-sBq" };

        assertThrows(InvalidArgsException.class, () -> {
            sut.parse(args);
        });
    }

    @Test
    void singleNonFlagArgTest2() {
        String[] args = { STDIN };

        assertThrows(InvalidArgsException.class, () -> {
            sut.parse(args);
        });
    }

    @Test
    void emptyNonFlagArgTest0() {
        String[] args = {};

        assertThrows(InvalidArgsException.class, () -> {
            sut.parse(args);
        });
    }

    /**
     * Used when the parser is not expected to produce any exceptions upon evaluation.
     */
    private void assertNoExceptionsOnEvaluate() {
        try {
            sut.parse();
        } catch (InvalidArgsException e) {
            fail("Arguments are valid.");
        }
    }
}
