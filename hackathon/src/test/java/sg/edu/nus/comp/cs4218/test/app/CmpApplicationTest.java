package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.CmpApplication;
import sg.edu.nus.comp.cs4218.impl.util.NumUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableRuleMigrationSupport
class CmpApplicationTest {
    private static final String NO_DIFF_OUTPUT = "";
    private static final String FILE_NAME_A = "a.txt";
    private static final String FILE_NAME_B = "b.txt";
    private static final String STD_IN = "-";

    private static final String SIMPLE_OUTPUT = "Files differ";
    private static final String EOF_FMT = "EOF on %s after char %d";
    private static final String DEFAULT_FMT = "%s %s differ: char %d, line %d";
    private static final String CHAR_DIFF_FMT = "%s %s differ: char %d, line %d is %d %c %d %c";
    private static final String OCT_DIFF_FMT = "%d %d %d%n";
    private static final String CHAR_OCT_DIFF_FMT = "%d %d %c %d %c%n";

    // Content for testing
    private static final String EMPTY = "";
    private static final String SIMPLE = String.format("a%n");
    private static final String SPECIAL = String.format("∆%n");
    private static final String IDENTICAL_LONG =
            String.format(SIMPLE +
                    SIMPLE +
                    SIMPLE);
    private static final String LONG_MULTILINE_A =
            String.format("a b%n" +
                    "c d%n");
    private static final String LONG_MULTILINE_B =
            String.format("e f%n" +
                    "g h%n");
    private static final String LONGER_MULTILINE =
            String.format("e f%n" +
                    "g h%n" +
                    "i j%n");

    private CmpApplication sut;
    private File fileA;
    private File fileB;
    private InputStream stream = null;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        fileA = tempFolder.newFile(FILE_NAME_A);
        fileB = tempFolder.newFile(FILE_NAME_B);

        Environment.currentDirectory = tempFolder.getRoot().getAbsolutePath();
        sut = new CmpApplication();
    }

    @Test
    void noContent() throws Exception {
        writeToFile(fileA, EMPTY);
        writeToFile(fileB, EMPTY);

        String noFlagResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);
        String charDiffResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);
        String simpleResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);
        String octDiffFlagResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);
        String charOctResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);
        String simpleCharResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);

        assertEquals(NO_DIFF_OUTPUT, noFlagResult);
        assertEquals(NO_DIFF_OUTPUT, charDiffResult);
        assertEquals(NO_DIFF_OUTPUT, simpleResult);
        assertEquals(NO_DIFF_OUTPUT, octDiffFlagResult);
        assertEquals(NO_DIFF_OUTPUT, charOctResult);
        assertEquals(NO_DIFF_OUTPUT, simpleCharResult);
    }

    @Test
    void sameContentShortLengthNoFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SIMPLE);

        String noFlagResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);
        assertEquals(NO_DIFF_OUTPUT, noFlagResult);
    }

    @Test
    void sameContentShortLengthCharFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SIMPLE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentShortLengthOctFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SIMPLE);

        String octDiffFlagResult = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);
        assertEquals(NO_DIFF_OUTPUT, octDiffFlagResult);
    }

    @Test
    void sameContentShortLengthCharOctFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SIMPLE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentShortLengthSimpleFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SIMPLE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentShortLengthSimpleCharFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SIMPLE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentLongLengthNoFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_A);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentLongLengthCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_A);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentLongLengthOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_A);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentLongLengthCharOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_A);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentLongLengthSimpleCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_A);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentLongLengthSimpleFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_A);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);
        assertEquals(NO_DIFF_OUTPUT, result);
    }

    @Test
    void sameContentWithShorterInputANoFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, IDENTICAL_LONG);
        int offset = 2;

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);
        assertEquals(
                String.format(EOF_FMT, FILE_NAME_A, offset),
                result);
    }
    @Test
    void sameContentWithShorterInputACharFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, IDENTICAL_LONG);
        int offset = 2;

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);
        assertEquals(
                String.format(EOF_FMT, FILE_NAME_A, offset),
                result);
    }
    @Test
    void sameContentWithShorterInputAOctFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, IDENTICAL_LONG);
        int offset = 2;

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);
        assertEquals(
                String.format(EOF_FMT, FILE_NAME_A, offset),
                result);
    }
    @Test
    void sameContentWithShorterInputACharOctFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, IDENTICAL_LONG);
        int offset = 2;

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);
        assertEquals(
                String.format(EOF_FMT, FILE_NAME_A, offset),
                result);
    }

    @Test
    void sameContentWithShorterInputASimpleFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, IDENTICAL_LONG);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);
        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void sameContentWithShorterInputASimpleCharFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, IDENTICAL_LONG);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);
        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentOneCharLengthWithExtendedAsciiNoFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SPECIAL);

        int offset = 1;
        int lineDiff = 1;

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);
        assertEquals(
                String.format(DEFAULT_FMT, FILE_NAME_A, FILE_NAME_B, offset, lineDiff),
                result);
    }

    @Test
    void diffContentOneCharLengthWithExtendedAsciiCharFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SPECIAL);

        char diffA = stripWhiteSpaces(SIMPLE).charAt(0);
        char diffB = stripWhiteSpaces(SPECIAL).charAt(0);
        int charAOctVal = NumUtils.decToOctal(diffA);
        int charBOctVal = NumUtils.decToOctal(diffB);
        int offset = 1;
        int lineDiff = 1;

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);
        assertEquals(
                String.format(CHAR_DIFF_FMT,
                        FILE_NAME_A, FILE_NAME_B,
                        offset, lineDiff,
                        charAOctVal, diffA,
                        charBOctVal, diffB),
                result);
    }

    @Test
    void diffContentOneCharLengthWithExtendedAsciiOctFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SPECIAL);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);

        char diffA = stripWhiteSpaces(SIMPLE).charAt(0);
        char diffB = stripWhiteSpaces(SPECIAL).charAt(0);
        int charAOctVal = NumUtils.decToOctal(diffA);
        int charBOctVal = NumUtils.decToOctal(diffB);
        int offset = 1;

        assertEquals(
                String.format(OCT_DIFF_FMT, offset, charAOctVal, charBOctVal),
                result);
    }

    @Test
    void diffContentOneCharLengthWithExtendedAsciiCharOctFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SPECIAL);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);

        char diffA = stripWhiteSpaces(SIMPLE).charAt(0);
        char diffB = stripWhiteSpaces(SPECIAL).charAt(0);
        int charAOctVal = NumUtils.decToOctal(diffA);
        int charBOctVal = NumUtils.decToOctal(diffB);
        int offset = 1;

        assertEquals(
                String.format(CHAR_OCT_DIFF_FMT,
                        offset, charAOctVal, diffA, charBOctVal, diffB),
                result);
    }

    @Test
    void diffContentOneCharLengthWithExtendedAsciiSimpleFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SPECIAL);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);

        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentOneCharLengthWithExtendedAsciiSimpleCharFlag() throws Exception {
        writeToFile(fileA, SIMPLE);
        writeToFile(fileB, SPECIAL);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);

        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentLongLengthNoFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_B);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);

        int offset = 1;
        int lineDiff = 1;

        assertEquals(
                String.format(DEFAULT_FMT, FILE_NAME_A, FILE_NAME_B, offset, lineDiff),
                result);
    }

    @Test
    void diffContentLongLengthCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_B);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);

        int offset = 1;
        int lineDiff = 1;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONG_MULTILINE_B);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                String.format(CHAR_DIFF_FMT,
                        FILE_NAME_A, FILE_NAME_B,
                        offset, lineDiff,
                        diffAOctValues[0], diffA.charAt(0),
                        diffBOctValues[0], diffB.charAt(0)),
                result);
    }

    @Test
    void diffContentLongLengthOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_B);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);

        int[] offsets = { 1, 3, 5, 7 };
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONG_MULTILINE_B);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                buildOctalDiffString(offsets, diffAOctValues, diffBOctValues),
                result);
    }

    @Test
    void diffContentLongLengthCharOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_B);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);

        int[] offsets = { 1, 3, 5, 7 };
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONG_MULTILINE_B);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                buildCharOctalDiffString(offsets,
                        diffAOctValues, diffBOctValues,
                        diffA.toCharArray(), diffB.toCharArray()),
                result);
    }

    @Test
    void diffContentLongLengthSimpleFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_B);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);
        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentLongLengthSimpleCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONG_MULTILINE_B);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);
        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentWithShorterFileANoFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONGER_MULTILINE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, false);

        int offset = 1;
        int lineDiff = 1;

        assertEquals(
                String.format(DEFAULT_FMT, FILE_NAME_A, FILE_NAME_B, offset, lineDiff),
                result);
    }

    @Test
    void diffContentWithShorterFileACharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONGER_MULTILINE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, false);

        int offset = 1;
        int lineDiff = 1;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONGER_MULTILINE);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                String.format(CHAR_DIFF_FMT,
                        FILE_NAME_A, FILE_NAME_B,
                        offset, lineDiff,
                        diffAOctValues[0], diffA.charAt(0),
                        diffBOctValues[0], diffB.charAt(0)),
                result);
    }

    @Test
    void diffContentWithShorterFileAOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONGER_MULTILINE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, false, true);

        int[] offsets = { 1, 3, 5, 7};
        int eofOffset = 8;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONGER_MULTILINE);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                buildOctalDiffString(offsets, diffAOctValues, diffBOctValues) +
                        String.format(EOF_FMT, FILE_NAME_A, eofOffset),
                result);
    }

    @Test
    void diffContentWithShorterFileACharOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONGER_MULTILINE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, false, true);

        int[] offsets = { 1, 3, 5, 7};
        int eofOffset = 8;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONGER_MULTILINE);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                buildCharOctalDiffString(offsets,
                        diffAOctValues, diffBOctValues,
                        diffA.toCharArray(), diffB.toCharArray()) +
                        String.format(EOF_FMT, FILE_NAME_A, eofOffset),
                result);
    }

    @Test
    void diffContentWithShorterFileASimpleFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONGER_MULTILINE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                false, true, false);

        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentWithShorterFileASimpleCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToFile(fileB, LONGER_MULTILINE);

        String result = sut.cmpTwoFiles(FILE_NAME_A, FILE_NAME_B,
                true, true, false);

        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentWithDiffLenWithStreamNoFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToStream(LONGER_MULTILINE);

        String result = sut.cmpFileAndStdin(FILE_NAME_A, stream,
                false, false, false);

        int offset = 1;
        int lineDiff = 1;

        assertEquals(
                String.format(DEFAULT_FMT, FILE_NAME_A, STD_IN, offset, lineDiff),
                result);
    }

    @Test
    void diffContentWithDiffLenWithStreamCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToStream(LONGER_MULTILINE);

        String result = sut.cmpFileAndStdin(FILE_NAME_A, stream,
                true, false, false);

        int offset = 1;
        int lineDiff = 1;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONGER_MULTILINE);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                String.format(CHAR_DIFF_FMT,
                        FILE_NAME_A, STD_IN,
                        offset, lineDiff,
                        diffAOctValues[0], diffA.charAt(0),
                        diffBOctValues[0], diffB.charAt(0)),
                result);
    }

    @Test
    void diffContentWithDiffLenWithStreamOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToStream(LONGER_MULTILINE);

        String result = sut.cmpFileAndStdin(FILE_NAME_A, stream,
                false, false, true);

        int[] offsets = { 1, 3, 5, 7};
        int eofOffset = 8;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONGER_MULTILINE);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                buildOctalDiffString(offsets, diffAOctValues, diffBOctValues) +
                        String.format(EOF_FMT, FILE_NAME_A, eofOffset),
                result);
    }

    @Test
    void diffContentWithDiffLenWithStreamCharOctFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToStream(LONGER_MULTILINE);

        String result = sut.cmpFileAndStdin(FILE_NAME_A, stream,
                true, false, true);

        int[] offsets = { 1, 3, 5, 7};
        int eofOffset = 8;
        String diffA = stripWhiteSpaces(LONG_MULTILINE_A);
        String diffB = stripWhiteSpaces(LONGER_MULTILINE);
        int[] diffAOctValues = diffA.chars().map(NumUtils::decToOctal).toArray();
        int[] diffBOctValues = diffB.chars().map(NumUtils::decToOctal).toArray();

        assertEquals(
                buildCharOctalDiffString(offsets,
                        diffAOctValues, diffBOctValues,
                        diffA.toCharArray(), diffB.toCharArray()) +
                        String.format(EOF_FMT, FILE_NAME_A, eofOffset),
                result);
    }

    @Test
    void diffContentWithDiffLenWithStreamSimpleFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToStream(LONGER_MULTILINE);

        String result = sut.cmpFileAndStdin(FILE_NAME_A, stream,
                false, true, false);

        assertEquals(SIMPLE_OUTPUT, result);
    }

    @Test
    void diffContentWithDiffLenWithStreamSimpleCharFlag() throws Exception {
        writeToFile(fileA, LONG_MULTILINE_A);
        writeToStream(LONGER_MULTILINE);

        String result = sut.cmpFileAndStdin(FILE_NAME_A, stream,
                true, true, false);

        assertEquals(SIMPLE_OUTPUT, result);
    }

    private String buildOctalDiffString(int[] offsets, int[] octValues1, int... octValues2) {
        assert offsets.length == octValues1.length || offsets.length == octValues2.length;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < offsets.length; ++i) {
            builder.append(String.format(OCT_DIFF_FMT, offsets[i], octValues1[i], octValues2[i]));
        }

        return builder.toString();
    }

    private String buildCharOctalDiffString(int[] offsets,
                                            int[] octValues1, int[] octValues2,
                                            char[] char1, char... char2) {
        assert offsets.length == octValues1.length || offsets.length == octValues2.length;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < offsets.length; ++i) {
            builder.append(String.format(CHAR_OCT_DIFF_FMT, offsets[i],
                    octValues1[i], char1[i],
                    octValues2[i], char2[i]));
        }

        return builder.toString();
    }

    private void writeToFile(File file, String str) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(str);
        writer.close();
    }

    private void writeToStream(String str) throws IOException {
        stream = new ByteArrayInputStream(str.getBytes());
        stream.close();
    }

    private String stripWhiteSpaces(String str) {
        return str.replaceAll("\\s+", "");
    }
}