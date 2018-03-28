package sg.edu.nus.comp.cs4218.impl.util;

import java.util.ArrayList;
import java.util.List;

public class SplitFileNameGenerator {
    private static final int ALPHABET_LENGTH = 26;
    private static final int INITIAL_SIZE = 2;
    private static final int ASCII_START_VAL = (int) 'a';
    private static final String PADDING = "z";

    private final String prefix;
    private final List<Integer> counter = new ArrayList<>();
    private int paddings = 0;

    public SplitFileNameGenerator(String prefix) {
        this.prefix = prefix;
        for (int i = 0; i < INITIAL_SIZE; ++i) {
            counter.add(0);
        }
    }

    public SplitFileNameGenerator() {
        this.prefix = "x";
        for (int i = 0; i < INITIAL_SIZE; ++i) {
            counter.add(0);
        }
    }

    /**
     * Generates the next value of an alphabet based counter, in format of [prefix][padding][counter]
     * prefix defaults to "x"
     * padding defaults to "z"
     * counter defaults to "aa" as start value
     * prefix and counter are always present in the returned value. padding is inserted if the counter overruns the
     * 2 initial alphabets. An example sequence xaa..xzz..xzaaa..xzzzz...
     *
     * @return the next filename for Split application
     */
    public String next() {
        StringBuilder result = new StringBuilder();
        result.append(prefix);

        for (int i = 0; i < paddings; i++) {
            result.append(PADDING);
        }

        for (int i = counter.size() - 1; i >= 0; i--) {
            int digit = counter.get(i);
            result.append(getChar(digit));
        }

        // Increment counter
        increment();

        return result.toString();
    }

    private void increment() {
        checkInvariant();

        // Increment the least significant number
        counter.set(0, counter.get(0) + 1);

        // Propagate carry from least significant to most significant
        for (int i = 0; i < counter.size(); i++) {
            int curr = counter.get(i);
            if (curr >= ALPHABET_LENGTH) {
                counter.set(i, 0);
                if (i == counter.size() - 1) {
                    counter.add(0);
                    paddings++;
                } else {
                    counter.set(i+1, counter.get(i+1)+1);
                }
            }
        }

        checkInvariant();
    }

    /**
     * Get the alphabet represented by a number in the ASCII table
     * Only supports a-z
     *
     * @param alphabetPosition
     * @return alphabet from ASCII table
     */
    private String getChar(int alphabetPosition) {
        assert alphabetPosition >= 0 && alphabetPosition <= 26;
        System.out.println();
        return String.valueOf((char) (alphabetPosition + ASCII_START_VAL)); // convert number to ascii character
    }

    private void checkInvariant() {
        for (int i = 0; i < counter.size(); i++) {
            assert counter.get(i) < 26;
        }
    }
}
