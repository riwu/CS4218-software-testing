package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.parser.SedArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SedApplication implements SedInterface {
    private final static String ERR_INVALID_FILE = "can't read %s: No such file or directory";
    private final static String ERR_INVALID_STDIN = "error reading from STDIN";

    private final SedArgsParser parser = new SedArgsParser();

    @Override
    public String replaceSubstringInFile(String pattern, String replacement, int replacementIndex,
                                         String fileName) throws SedException {
        if (fileName == null) {
            throw new SedException("null file not allowed.");
        }

        if (pattern == null || pattern.isEmpty()) {
            throw new SedException("null or empty pattern not allowed.");
        }

        if (replacement == null) {
            throw new SedException("null replacement not allowed.");
        }

        if (replacementIndex == 0) {
            throw new SedException("zero replacement index not allowed.");
        }

        String result;
        String filePath = resolveFilePath(fileName);
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            result = replaceFromReader(reader, pattern, replacement, replacementIndex);
        } catch (IOException e) {
            throw new SedException(String.format(ERR_INVALID_FILE, fileName), e);
        }

        return result;
    }

    @Override
    public String replaceSubstringInStdin(String pattern, String replacement, int replacementIndex,
                                          InputStream stdin) throws SedException {
        if (stdin == null) {
            throw new SedException("null stdin not allowed.");
        }

        if (pattern == null || pattern.isEmpty()) {
            throw new SedException("null or empty pattern not allowed.");
        }

        if (replacement == null) {
            throw new SedException("null replacement not allowed.");
        }

        if (replacementIndex == 0) {
            throw new SedException("zero replacement index not allowed.");
        }

        String result;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        try {
            result = replaceFromReader(reader, pattern, replacement, replacementIndex);
        } catch (IOException e) {
            throw new SedException(ERR_INVALID_STDIN, e);
        }

        return result;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws SedException {
        if (args == null || args.length == 0 || args.length > 2) {
            throw new SedException("invalid number of arguments");
        }

        String result;
        try {
            parser.parse(args[0]);
        } catch (InvalidArgsException e) {
            throw new SedException(e.getMessage(), e);
        }

        if (args.length == 1) {
            result = replaceSubstringInStdin(parser.getRegex(), parser.getReplacement(),
                                             parser.getMatchIndex(), stdin);
        } else {
            result = replaceSubstringInFile(parser.getRegex(), parser.getReplacement(),
                                            parser.getMatchIndex(), args[1]);
        }

        try {
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new SedException("cannot write to stdout.", e);
        }
    }

    /**
     * Resolves the path of the input filename, returns the absolute path.
     *
     * @param fileName - name of the file (could be relative path)
     * @return a string that represents the absolute path of the input file
     */
    private String resolveFilePath(String fileName) {
        if (Paths.get(fileName).isAbsolute()) {
            return fileName;
        }

        Path cwd = Paths.get(Environment.currentDirectory);
        return cwd.resolve(fileName).toAbsolutePath().toString();
    }

    /**
     * Performs a replacement like `sed` from a BufferedReader.
     *
     * @param reader
     * @param pattern
     * @param replacement
     * @param replacementIndex
     * @return
     * @throws IOException
     */
    private String replaceFromReader(BufferedReader reader, String pattern,
                                     String replacement, int replacementIndex) throws IOException {
        StringBuilder result = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(replaceNthOccurrence(line, pattern, replacement, replacementIndex));
            result.append(StringUtils.STRING_NEWLINE);
        }

        return result.toString();
    }

    /**
     * Replaces the n-th occurence of the provided regex pattern in a single line with a replacement
     * string.
     *
     * @param line
     * @param pattern
     * @param replacement
     * @param replacementIndex - this integer represents the n value
     * @return string that is the line after performing the replacements
     */
    private String replaceNthOccurrence(String line, String pattern, String replacement,
                                        int replacementIndex) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = Pattern.compile(pattern).matcher(line);
        for (int i = 0; i < replacementIndex-1; i++) {
            matcher.find();
        }

        if (!matcher.hitEnd() && matcher.find()) {
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
