package sg.edu.nus.comp.cs4218.test.util;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {

    /**
     * Tests for isBlank()
     */

    @Test
    public void testIsBlankNull() {
        assertTrue(StringUtils.isBlank(null));
    }

    @Test
    public void testIsBlankEmpty() {
        assertTrue(StringUtils.isBlank(""));
    }

    @Test
    public void testIsBlankWhiteSpaces() {
        assertTrue(StringUtils.isBlank("  \t\n"));
    }

    @Test
    public void testIsBlankOneChar() {
        assertFalse(StringUtils.isBlank("a"));
    }

    @Test
    public void testIsBlankMultipleChar() {
        assertFalse(StringUtils.isBlank("123"));
    }

    @Test
    public void testIsBlankMultipleCharWithSpaces() {
        assertFalse(StringUtils.isBlank("   1 1 2"));
    }

    /**
     * Tests for multiplyChar
     */

    @Test
    public void testMultiplyNegativeTimes() {
        String expected = "";
        String result = StringUtils.multiplyChar('y', -8);
        assertEquals(expected, result);
    }

    @Test
    public void testMultiplyZeroTimes() {
        String expected = "";
        String result = StringUtils.multiplyChar('x', 0);
        assertEquals(expected, result);
    }

    @Test
    public void testMultiplyOneTime() {
        String expected = "d";
        String result = StringUtils.multiplyChar('d', 1);
        assertEquals(expected, result);
    }

    @Test
    public void testMultiplyMultipleTimes() {
        String expected = "aaaaa";
        String result = StringUtils.multiplyChar('a', 5);
        assertEquals(expected, result);
    }
}
