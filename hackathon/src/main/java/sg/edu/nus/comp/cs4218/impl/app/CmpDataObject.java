package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.impl.util.NumUtils;

public class CmpDataObject {
    private final int offset;
    private final int lineNumber;
    private final int firstChar;
    private final int secondChar;
    private final CmpStreamStatus streamStatus;

    /**
     * Encapsulation of comparison result
     * @param offset starts from 1
     * @param lineNumber starts from 1
     * @param firstChar differing value of first input
     * @param secondChar differing value of second input
     */
    CmpDataObject(int offset, int lineNumber, int firstChar, int secondChar) {
        this.offset = offset;
        this.lineNumber = lineNumber;
        this.firstChar = firstChar;
        this.secondChar = secondChar;

        this.streamStatus = CmpStreamStatus.NONE;
    }

    /**
     * Encapsulate stream end information. If a value other than NONE is set,
     * other instance variables should be ignored
     *
     * @param streamStatus indicates which stream has ended
     */
    CmpDataObject(CmpStreamStatus streamStatus, int offset) {
        this.streamStatus = streamStatus;
        this.offset = offset;

        this.lineNumber = -1;
        this.firstChar = -1;
        this.secondChar = -1;
    }

    /**
     * Offset starts from 1. Values less than 1 should be ignored
     *
     * @return offset of difference
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Line number starts from 1. Values less than 1 should be ignored
     * When the data object is along with other CmpDataObjects in a collection,
     * only the line number from the first CmpDataObject should be considered.
     *
     * @return line number of difference
     */
    public int getLineNumber() {
        return lineNumber;
    }


    /**
     * @return char in octal value from the first stream that is different from the other stream
     */
    public int getFirstCharOctVal() { return NumUtils.decToOctal(firstChar); }

    /**
     * @return char in octal value from the second stream that is different from the other stream
     */
    public int getSecondCharOctVal() {
        return NumUtils.decToOctal(secondChar);
    }

    /**
     * @return char in decimal value from the first stream that is different from the other stream
     */
    public int getFirstCharDecVal() { return firstChar; }

    /**
     * @return char in decimal value from the second stream that is different from the other stream
     */
    public int getSecondCharDecVal() { return secondChar; }

    /**
     * @return the stream status of the difference
     */
    public CmpStreamStatus getStreamStatus() { return streamStatus; }

    @Override
    public String toString() {
        return "offset: " + offset + System.lineSeparator() +
                "lineNum: " + lineNumber + System.lineSeparator() +
                "firstChar: " + firstChar + System.lineSeparator() +
                "secondChar: " + secondChar + System.lineSeparator() +
                "streamStatus: " + streamStatus;
    }
}
