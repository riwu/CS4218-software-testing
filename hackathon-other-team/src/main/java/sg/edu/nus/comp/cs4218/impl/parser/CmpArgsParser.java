package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.List;

public class CmpArgsParser extends ArgsParser {
    private static final char SIMPLIFY_FLAG = 's';
    private static final char CHAR_DIFF_FLAG = 'c';
    private static final char OCT_DIFF_FLAG = 'l';

    public CmpArgsParser() {
        super();

        legalFlags.add(SIMPLIFY_FLAG);
        legalFlags.add(OCT_DIFF_FLAG);
        legalFlags.add(CHAR_DIFF_FLAG);
    }

    /**
     * c: isPrintCharDiff
     * s: isPrintSimplify
     * l: isPrintOctalDiff
     * Flag combinations: [s|l][c]
     */
    @Override
    protected void validateArgs() throws InvalidArgsException {
        super.validateArgs();

        if (nonFlagArgs.size() != 2) {
            throw new InvalidArgsException("Expected 2 operands only found " + nonFlagArgs.size());
        }

        if (flags.contains(SIMPLIFY_FLAG) && flags.contains(OCT_DIFF_FLAG)) {
            throw new InvalidArgsException("Illegal flag combination");
        }
    }

    public boolean isPrintSimplify() {
        return flags.contains(SIMPLIFY_FLAG);
    }

    public boolean isPrintCharDiff() {
        return flags.contains(CHAR_DIFF_FLAG);
    }

    public boolean isPrintOctalDiff() {
        return flags.contains(OCT_DIFF_FLAG);
    }

    public List<String> getOperands() {
        return nonFlagArgs;
    }
}
