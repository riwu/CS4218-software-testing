package sg.edu.nus.comp.cs4218.test.util;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.SplitFileNameGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SplitFileNameGeneratorTest {
    private static final int ALPHABET_LENGTH = 26;

    @Test
    void firstCounterDefaultPrefix() {
        SplitFileNameGenerator generator = new SplitFileNameGenerator();
        assertEquals("xaa", generator.next());
    }

    @Test
    void lastCounterForFirstAlphabetDefaultPrefix() {
        SplitFileNameGenerator generator = new SplitFileNameGenerator();
        int count = ALPHABET_LENGTH;
        String name = "";
        for (int i = 0; i < count; ++i) {
            name = generator.next();
        }
        assertEquals("xaz", name);
    }

    @Test
    void afterLastCounterForFirstAlphabetDefaultPrefix() {
        SplitFileNameGenerator generator = new SplitFileNameGenerator();
        for (int i = 0; i < ALPHABET_LENGTH; ++i) {
            generator.next();
        }
        assertEquals("xba", generator.next());
    }

    /**
     * The 677th split file name.
     */
    @Test
    void largeCounterDefaultPrefix() {
        SplitFileNameGenerator generator = new SplitFileNameGenerator();
        int count = (int) Math.pow(ALPHABET_LENGTH, 2);
        String actual = "";
        for (int i = 0; i <= count; i++) {
            actual = generator.next();
        }

        assertEquals("xzaaa", actual);
    }

    /**
     * The 676th split file name.
     */
    @Test
    void largeCounterBoundaryDefaultPrefix() {
        SplitFileNameGenerator generator = new SplitFileNameGenerator();
        int count = (int) Math.pow(ALPHABET_LENGTH, 2);
        String actual = "";
        for (int i = 0; i < count; i++) {
            actual = generator.next();
        }

        assertEquals("xzz", actual);
    }

    @Test
    void firstCounterEmptyPrefix() {
        SplitFileNameGenerator generator = new SplitFileNameGenerator("");
        assertEquals("aa", generator.next());
    }

    @Test
    void firstCounterCustomPrefix() {
        String prefix = "hello";
        SplitFileNameGenerator generator = new SplitFileNameGenerator(prefix);
        assertEquals(String.format("%s%s", prefix, "aa"), generator.next());
    }

}