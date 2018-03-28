package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CmpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CmpException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CmpArgsParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CmpApplication implements CmpInterface {
    public static final String SIMPLE_OUTPUT = "Files differ";
    public static final String EOF_FMT = "EOF on %s after char %d"; // FMT stands for format. PMD dont ask why
    public static final String DEFAULT_FMT = "%s %s differ: char %d, line %d";
    public static final String CHAR_DIFF_FMT = "%s %s differ: char %d, line %d is %d %c %d %c";
    public static final String OCT_DIFF_FMT = "%d %d %d%n";
    public static final String CHAR_OCT_DIFF_FMT = "%d %d %c %d %c%n";
    public static final String STD_IN = "-";

    private static final int LINE_FEED = 10; //line feed
    private static final int CARRIAGE_RETURN = 13; // carriage return

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        CmpArgsParser parser = new CmpArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new CmpException(e.getMessage(), e);
        }

        List<String> operands = parser.getOperands();
        boolean isPrintSimplify = parser.isPrintSimplify();
        boolean isPrintCharDiff = parser.isPrintCharDiff();
        boolean isPrintOctalDiff = parser.isPrintOctalDiff();

        try {
            if (operands.get(1).equals(STD_IN)) {
                cmpFileAndStdin(operands.get(0), stdin, isPrintCharDiff, isPrintSimplify, isPrintOctalDiff);
            } else {
                cmpTwoFiles(operands.get(0), operands.get(1), isPrintCharDiff, isPrintSimplify, isPrintOctalDiff);
            }
        } catch (Exception e) {
            throw new CmpException(e.getMessage(), e);
        }
    }

    /**
     * c: isPrintCharDiff
     * s: isPrintSimplify
     * l: isPrintOctalDiff
     * Valid flag combinations: [s|l][c]
     *
     * @param fileNameA        String of file name of the first file to be compared
     * @param fileNameB        String of file name of the second file to be compared
     * @param isPrintCharDiff  Boolean option to print differing characters and the differing octal
     *                         values
     * @param isPrintSimplify  Boolean option to only print "Files differ" if the files are
     *                         different
     * @param isPrintOctalDiff Boolean option to print the byte offset to the differing byte and the
     *                         differing octal values.
     * @return
     * @throws Exception if invalid flag combination is given
     */
    @Override
    public String cmpTwoFiles(String fileNameA, String fileNameB, Boolean isPrintCharDiff, Boolean isPrintSimplify, Boolean isPrintOctalDiff) throws Exception {
        InputStream stream1 = new FileInputStream(Paths.get(Environment.currentDirectory, fileNameA).toFile());
        InputStream stream2 = new FileInputStream(Paths.get(Environment.currentDirectory, fileNameB).toFile());
        List<CmpDataObject> comparisonResults = compare(stream1, stream2);
        return outputBuilder(comparisonResults,
                fileNameA, fileNameB,
                isPrintCharDiff, isPrintSimplify, isPrintOctalDiff);
    }

    @Override
    public String cmpFileAndStdin(String fileName, InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify, Boolean isPrintOctalDiff) throws Exception {
        InputStream stream1 = new FileInputStream(Paths.get(Environment.currentDirectory, fileName).toFile());
        List<CmpDataObject> comparisonResults = compare(stream1, stdin);
        return outputBuilder(comparisonResults,
                fileName, "-",
                isPrintCharDiff, isPrintSimplify, isPrintOctalDiff);
    }

    @Override
    public String cmpStdin(InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify, Boolean isPrintOctalDiff) throws Exception {
        throw new CmpException("Ignored due to incorrect signature");
    }

    /**
     * Compares differences between 2 streams. Assumes streams are correctly terminated
     *
     * @param stream1
     * @param stream2
     * @return Data object containing information about the comparison. Guaranteed to return
     * at least one object in the list, with stream status ended values
     */
    public List<CmpDataObject> compare(InputStream stream1, InputStream stream2) {
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(stream1));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(stream2));
        List<CmpDataObject> results = new ArrayList<>();
        int offset = 1;
        int lineNumber = 1;

        try {
            int char1 = reader1.read();
            int char2 = reader2.read();

            while (char1 != -1 && char2 != -1) {
                if (char1 != char2) {
                    CmpDataObject result = new CmpDataObject(offset, lineNumber, char1, char2);
                    results.add(result);
                }

                if (char1 == LINE_FEED || char2 == LINE_FEED || char1 == CARRIAGE_RETURN || char2 == CARRIAGE_RETURN) {
                    // line number is only useful and accurate for first diff
                    // subsequent line number is inaccurate due to line separator misalignment
                    // and should be ignored
                    lineNumber += 1;
                }
                offset++;

                char1 = reader1.read();
                char2 = reader2.read();
            }

            if (char1 == -1 && char2 == -1) {
                CmpDataObject result = new CmpDataObject(CmpStreamStatus.BOTH, offset - 1);
                results.add(result);
            } else if (char1 == -1) {
                CmpDataObject result = new CmpDataObject(CmpStreamStatus.FIRST_STREAM_ENDED, offset - 1);
                results.add(result);
            } else {
                CmpDataObject result = new CmpDataObject(CmpStreamStatus.SECOND_STREAM_ENDED, offset - 1);
                results.add(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert !results.isEmpty();
        return results;
    }

    private String outputBuilder(List<CmpDataObject> results,
                                 String inputA,
                                 String inputB,
                                 boolean isPrintCharDiff,
                                 boolean isPrintSimplify,
                                 boolean isPrintOctalDiff) {

        if (isPrintSimplify && hasDiff(results)) {
            return SIMPLE_OUTPUT;
        }

        StringBuilder builder = new StringBuilder();

        for (CmpDataObject result : results) {
            CmpStreamStatus status = result.getStreamStatus();

            if (status == CmpStreamStatus.FIRST_STREAM_ENDED) {
                builder.append(String.format(EOF_FMT, inputA, result.getOffset()));
            } else if (status == CmpStreamStatus.SECOND_STREAM_ENDED) {
                builder.append(String.format(EOF_FMT, inputB, result.getOffset()));
            } else if (status == CmpStreamStatus.NONE) {

                if (isPrintOctalDiff) {
                    if (isPrintCharDiff) {
                        builder.append(buildCharOctDiffOutput(result));
                    } else {
                        builder.append(buildOctDiffOutput(result));
                    }
                } else {
                    String output = String.format(DEFAULT_FMT,
                            inputA, inputB,
                            result.getOffset(), result.getLineNumber());

                    if (isPrintCharDiff) {
                        output = String.format(CHAR_DIFF_FMT,
                                inputA, inputB,
                                result.getOffset(), result.getLineNumber(),
                                result.getFirstCharOctVal(), (char) result.getFirstCharDecVal(),
                                result.getSecondCharOctVal(), (char) result.getSecondCharDecVal());
                    }
                    return output;
                }
            }
            // do nothing when both streams end
        }

        return builder.toString();
    }

    private boolean hasDiff(List<CmpDataObject> results) {
        // defensively check for size > 0 even though the list generated from compare has at least 1 item
        return !results.isEmpty() && results.get(0).getStreamStatus() != CmpStreamStatus.BOTH;
    }

    private String buildCharOctDiffOutput(CmpDataObject result) {
        return String.format(CHAR_OCT_DIFF_FMT,
                result.getOffset(),
                result.getFirstCharOctVal(), (char) result.getFirstCharDecVal(),
                result.getSecondCharOctVal(), (char) result.getSecondCharDecVal());
    }

    private String buildOctDiffOutput(CmpDataObject result) {
        return String.format(OCT_DIFF_FMT,
                        result.getOffset(),
                        result.getFirstCharOctVal(),
                        result.getSecondCharOctVal());
    }
}