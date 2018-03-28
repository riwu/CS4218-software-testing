package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

public class SplitArgsParser extends ArgsParser {
    private static final char FLAG_LINES = 'l';
    private static final char FLAG_BYTES = 'b';
    private static final char UNIT_BYTE = 'b';
    private static final char UNIT_KILOBYTE = 'k';
    private static final char UNIT_MEGABYTE = 'm';
    private static final int BYTE_SIZE = 512;
    private static final int KILOBYTE_SIZE = 1024;
    private static final int MEGABYTE_SIZE = 1048576;
    private static final int IDX_COUNT = 0;
    private static final int IDX_FILE = 0;
    private static final int IDX_FILE_FLAG = 1; // when there is flag
    private static final int IDX_PREFIX = 1;
    private static final int IDX_PREFIX_FLAG = 2; // when there is flag
    private static final int DEFAULT_SIZE = 1000;

    /**
     * Convert to bytes if given any units, b, k, m
     * @param size
     * @return
     */
    public static int getByteSize(String size) {
        if (size.endsWith(String.valueOf(UNIT_BYTE))) {
            return Integer.parseInt(size.substring(0, size.length() - 1)) * BYTE_SIZE;
        } else if (size.endsWith(String.valueOf(UNIT_KILOBYTE))) {
            return Integer.parseInt(size.substring(0, size.length() - 1)) * KILOBYTE_SIZE;
        } else if (size.endsWith(String.valueOf(UNIT_MEGABYTE))) {
            return Integer.parseInt(size.substring(0, size.length() - 1)) * MEGABYTE_SIZE;
        } else {
            return Integer.parseInt(size);
        }
    }

    public SplitArgsParser() {
        super();

        legalFlags.add(FLAG_BYTES);
        legalFlags.add(FLAG_LINES);
    }

    @Override
    public void parse(String... args) throws InvalidArgsException {
        super.parse(args);

        if (flags.contains(FLAG_BYTES) && flags.contains(FLAG_LINES)) {
            throw new InvalidArgsException("Illegal flag combination");
        }

        if (flags.isEmpty()) {
            if (nonFlagArgs.isEmpty()) {
                throw new InvalidArgsException("Missing operands");
            } else if (nonFlagArgs.size() > 2) {
                throw new InvalidArgsException("Too many operands");
            }
        } else {
            if (nonFlagArgs.size() < 2) {
                throw new InvalidArgsException("Missing operands");
            } else if (nonFlagArgs.size() > 3) {
                throw new InvalidArgsException("Too many operands");
            }
        }
    }

    public boolean isLineCount() {
        return flags.isEmpty() || flags.contains(FLAG_LINES); // defaults to LINES
    }

    public boolean isByteCount() {
        return flags.contains(FLAG_BYTES);
    }

    public int getChunkSize() {
        String size = nonFlagArgs.get(IDX_COUNT);

        if (isLineCount()) {
            return Integer.parseInt(size);
        } else if (isByteCount()) {
            // Convert to bytes if given any units
            return getByteSize(size);
        } else {
            return DEFAULT_SIZE;
        }
    }

    public String getPrefix() {
        return flags.isEmpty() ? nonFlagArgs.get(IDX_PREFIX) : nonFlagArgs.get(IDX_PREFIX_FLAG);
    }

    public String getFileName() {
        return flags.isEmpty() ? nonFlagArgs.get(IDX_FILE) : nonFlagArgs.get(IDX_FILE_FLAG);
    }
}
