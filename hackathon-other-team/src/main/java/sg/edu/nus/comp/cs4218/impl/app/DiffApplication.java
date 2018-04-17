package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.DiffArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Easiest implementation of the diff algorithm by using the longest common subsequence DP solution.
 *
 * Of course there are more efficient algorithms to do so, like finding shortest path in an edit
 * graph, but that is not necessary for our basic implementation for testing. :)
 *
 * A paper for the daring: http://www.xmailserver.org/diff2.pdf
 */
public class DiffApplication implements DiffInterface {
    private final static String MESSAGE_SIMPLE = "Files %s and %s differ";
    private final static String MESSAGE_SAME = "Files %s %s are identical";
    private final static String ERROR_MISMATCH = "%s/%s: No such file or directory";
    private final static String TOKEN_STDIN = "-";

    @Override
    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame,
                               Boolean isNoBlank, Boolean isSimple) throws Exception {
        List<String> listA = readFile(fileNameA);
        List<String> listB = readFile(fileNameB);
        List<String> lcs = longestCommonSubsequence(listA, listB);
        List<Difference> left = compareWithLCS(listA, lcs, Direction.LEFT);
        List<Difference> right = compareWithLCS(listB, lcs, Direction.RIGHT);

        left.addAll(right);
        Collections.sort(left);

        if (isNoBlank) {
            left = filterBlankDifferences(left);
        }

        if (isSimple && !left.isEmpty()) {
            return String.format(MESSAGE_SIMPLE, fileNameA, fileNameB);
        } else if (isShowSame && left.isEmpty()) {
            return String.format(MESSAGE_SAME, fileNameA, fileNameB);
        } else {
            return buildResult(left);
        }
    }

    @Override
    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank,
                             Boolean isSimple) throws Exception {
        List<String> listA = readDir(folderA);
        List<String> listB = readDir(folderB);
        List<Difference> differences = getDirDiff(listA, listB, folderA, folderB);
        Collections.sort(differences);

        return buildResult(differences);
    }

    @Override
    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame,
                                   Boolean isNoBlank, Boolean isSimple) throws Exception {
        List<String> listA = readFile(fileName);
        List<String> listB = readStdin(stdin);
        List<String> lcs = longestCommonSubsequence(listA, listB);
        List<Difference> left = compareWithLCS(listA, lcs, Direction.LEFT);
        List<Difference> right = compareWithLCS(listB, lcs, Direction.RIGHT);

        left.addAll(right);
        Collections.sort(left);

        if (isNoBlank) {
            left = filterBlankDifferences(left);
        }

        if (isSimple && !left.isEmpty()) {
            return String.format(MESSAGE_SIMPLE, fileName, TOKEN_STDIN);
        } else if (isShowSame && left.isEmpty()) {
            return String.format(MESSAGE_SAME, fileName, TOKEN_STDIN);
        } else {
            return buildResult(left);
        }
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws DiffException {
        DiffArgsParser parser = new DiffArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new DiffException(e.getMessage(), e);
        }

        String fileNameA = parser.getFirst();
        String fileNameB = parser.getSecond();

        Boolean isSimple = parser.isSimple();
        Boolean isSame = parser.isSame();
        Boolean isNoBlank = parser.isNoBlank();

        String result;
        try {
            if (isDiffDirectory(fileNameA, fileNameB)) {
                result = diffTwoDir(fileNameA, fileNameB, isSame, isNoBlank, isSimple);
            } else if (fileNameA.equals(TOKEN_STDIN) && fileNameB.equals(TOKEN_STDIN)) {
                result = "";
            } else if (fileNameA.equals(TOKEN_STDIN)) {
                result = diffFileAndStdin(fileNameB, stdin, isSame, isNoBlank, isSimple);
            } else if (fileNameB.equals(TOKEN_STDIN)) {
                result = diffFileAndStdin(fileNameA, stdin, isSame, isNoBlank, isSimple);
            } else {
                result = diffTwoFiles(fileNameA, fileNameB, isSame, isNoBlank, isSimple);
            }
        } catch (DiffException e) {
            throw e;
        } catch (Exception e) {
            throw new DiffException("unknown error.", e);
        }

        try {
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    /**
     * Returns true if both filenames are directories.
     *
     * @param fileNameA
     * @param fileNameB
     * @return
     * @throws DiffException
     */
    private Boolean isDiffDirectory(String fileNameA, String fileNameB) throws DiffException {
        if (fileNameA.equals(TOKEN_STDIN) || fileNameB.equals(TOKEN_STDIN)) {
            return false;
        }

        Path pathA = Paths.get(Environment.currentDirectory, fileNameA);
        Path pathB = Paths.get(Environment.currentDirectory, fileNameB);

        if (Files.isDirectory(pathA) ^ Files.isDirectory(pathB)) {
            throw new DiffException(String.format(ERROR_MISMATCH, fileNameA, fileNameB));
        } else {
            return Files.isDirectory(pathA) && Files.isDirectory(pathB);
        }
    }

    /**
     * Reads contents of a file into a list of string.
     *
     * @return list of string that represents the contents of a file
     */
    private List<String> readFile(String fileName) throws Exception {
        // Convert to path
        Path path;
        if (fileName.charAt(0) == '/') {
            path = Paths.get(fileName).normalize();
        } else {
            path = Paths.get(Environment.currentDirectory, fileName).normalize();
        }

        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        ArrayList<String> result = new ArrayList<>();
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            result.add(currentLine);
        }

        return result;
    }

    /**
     * Reads contents of stdin into a list of string.
     *
     * @param inputStream - input stream to read the contents from
     * @return list of string that represents the contents of a file
     * @throws Exception
     */
    private List<String> readStdin(InputStream inputStream) throws Exception {
        List<String> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }

        return result;
    }

    /**
     * Reads the contents of a single directory into a list of strings.
     *
     * @param directory - directory to read the contents from
     * @return a list of string that represents the child nodes of a directory
     */
    private List<String> readDir(String directory) {
        // Convert to path
        Path path;
        if (directory.charAt(0) == '/') {
            path = Paths.get(directory).normalize();
        } else {
            path = Paths.get(Environment.currentDirectory, directory).normalize();
        }

        File file  = new File(path.toString());

        return Arrays.asList(file.list());
    }

    /**
     * Remove differences that are just a blank line for content.
     *
     * @param differences - differences to filter
     * @return filtered difference
     */
    private List<Difference> filterBlankDifferences(List<Difference> differences) {
        List<Difference> result = new ArrayList<>();
        for (Difference difference : differences) {
            if (!StringUtils.isBlank(difference.getContent())) {
                result.add(difference);
            }
        }

        return result;
    }

    /**
     * Builds the result given a list of differences.
     * @param differences - list of differences to build a result string from
     * @return formatted string to output to the user
     */
    private String buildResult(List<Difference> differences) {
        StringBuilder result = new StringBuilder();
        for (Difference difference : differences) {
            result.append(difference.toString()).append(StringUtils.STRING_NEWLINE);
        }

        return result.toString().trim();
    }

    /**
     * Returns a list of differences between two lists of strings.
     *
     * @param listA
     * @param listB
     * @return list of differences (symmetric difference) between the two sets of strings
     */
    private List<Difference> getDirDiff(List<String> listA, List<String> listB,
                                        String parentA, String parentB) {
        Set<String> setA = new HashSet<>(listA);
        Set<String> setB = new HashSet<>(listB);

        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        setA.removeAll(intersection);
        setB.removeAll(intersection);

        List<Difference> result = new ArrayList<>();
        for (String file : setA) {
            Difference diff = new DirectoryDifference(file, parentA);
            result.add(diff);
        }

        for (String file : setB) {
            Difference diff = new DirectoryDifference(file, parentB);
            result.add(diff);
        }

        return result;
    }

    /**
     * Returns the difference between the contents and the longest common subsequence.
     *
     * @return
     */
    private List<Difference> compareWithLCS(List<String> contents, List<String> lcs,
                                                Direction direction) {
        List<Difference> result = new ArrayList<>();

        int lcsIndex = 0;
        for (int i = 0; i < contents.size(); i++) {
            if (lcsIndex < lcs.size() && contents.get(i).equals(lcs.get(lcsIndex))) {
                lcsIndex++;
            } else {
                LineDifference difference = new LineDifference(contents.get(i), i, direction);
                result.add(difference);
            }
        }

        return result;
    }

    /**
     * Get the longest common subsequence between two list of strings.
     *
     * @param listA
     * @param listB
     * @return longest common subsequence (LCS) as a list of strings
     */
    private List<String> longestCommonSubsequence(List<String> listA, List<String> listB) {
        int[][] table = new int[listA.size() + 1][listB.size() + 1];

        for (int i = 1; i < listA.size() + 1; i++) {
            for (int j =1; j < listB.size() + 1; j++) {
                if (listA.get(i-1).equals(listB.get(j-1))) {
                    table[i][j] = table[i-1][j-1] + 1;
                } else {
                    table[i][j] = Math.max(table[i-1][j], table[i][j-1]);
                }
            }
        }

        List<String> lcs = new ArrayList<>();
        getLCS(listA, table, listA.size(), listB.size(), lcs);

        return lcs;
    }

    /**
     * Returns the actual longest common subsequence using the dynamic programming table built.
     *
     * @param list
     * @param table
     * @param row
     * @param col
     * @param lcs
     */
    private void getLCS(List<String> list, int[][] table, int row, int col, List<String> lcs) {
        if (table[row][col] == 0) {
            Collections.reverse(lcs);
            return;
        } else if (table[row][col] == table[row][col-1]) {
            getLCS(list, table, row, col-1, lcs);
        } else if (table[row][col] == table[row-1][col]) {
            getLCS(list, table, row-1, col, lcs);
        } else if (table[row][col] == table[row-1][col-1] + 1) {
            lcs.add(list.get(row-1));
            getLCS(list, table, row-1, col-1, lcs);
        }
    }

    /**
     * Encapsulates the direction of the change.
     */
    private enum Direction {
        LEFT,
        RIGHT;

        @Override
        public String toString() {
            if (this == LEFT) {
                return "<";
            } else {
                return ">";
            }
        }
    }

    private interface Difference extends Comparable<Difference>{
        String getContent();
    }

    /**
     * Encapsulates a single deviation from the longest common subsequence, and the line number that
     * the offence was made.
     */
    private class LineDifference implements Difference {
        private final String content;
        private final int line;
        private final Direction direction;

        public LineDifference(String content, int line, Direction direction) {
            this.content = content;
            this.line = line;
            this.direction = direction;
        }

        @Override
        public int compareTo(Difference other) {
            LineDifference otherLineDiff = (LineDifference) other;
            return line - otherLineDiff.line;
        }

        @Override
        public String toString() {
            return direction.toString() + content;
        }

        @Override
        public String getContent() {
            return content;
        }
    }

    /**
     * Encapsulates a single deviation from the longest common subsequence, and the parent directory
     * of the file.
     */
    private class DirectoryDifference implements Difference {
        private final static String FORMAT_OUTPUT = "Only in %s: %s";
        private final String name;
        private final String parent;

        public DirectoryDifference(String name, String parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public int compareTo(Difference other) {
            DirectoryDifference otherDirDiff = (DirectoryDifference) other;
            return this.name.compareTo(otherDirDiff.name);
        }

        @Override
        public String toString() {
            return String.format(FORMAT_OUTPUT, this.parent, this.name);
        }

        @Override
        public String getContent() {
            return name;
        }
    }
}
