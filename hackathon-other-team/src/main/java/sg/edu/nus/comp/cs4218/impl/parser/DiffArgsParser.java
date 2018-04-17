package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

public class DiffArgsParser extends ArgsParser {
    private final static String ERROR_MISSING_OPS = "missing operand after 'diff'";
    private final static String ERROR_MISSING_OP = "missing operand after '%s'";

    private final static char FLAG_SAME = 's';
    private final static char FLAG_NO_BLANK = 'B';
    private final static char FLAG_SIMPLE = 'q';
    private final static String TOKEN_STDIN = "-";

    public DiffArgsParser() {
        super();
        legalFlags.add(FLAG_SAME);
        legalFlags.add(FLAG_NO_BLANK);
        legalFlags.add(FLAG_SIMPLE);
    }

    @Override
    public void validateArgs() throws InvalidArgsException {
        super.validateArgs();

        if (nonFlagArgs.size() == 1) {
            throw new InvalidArgsException(String.format(ERROR_MISSING_OP, nonFlagArgs.get(0)));
        }

        if (nonFlagArgs.isEmpty()) {
            throw new InvalidArgsException(ERROR_MISSING_OPS);
        }
    }

    public Boolean isSame() {
        return flags.contains(FLAG_SAME);
    }

    public Boolean isNoBlank() {
        return flags.contains(FLAG_NO_BLANK);
    }

    public Boolean isSimple() {
        return flags.contains(FLAG_SIMPLE);
    }

    public String getFirst() {
        return nonFlagArgs.get(0);
    }

    public String getSecond() {
        return nonFlagArgs.get(1);
    }
}
