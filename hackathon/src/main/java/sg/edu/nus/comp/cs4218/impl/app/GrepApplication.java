package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.GrepArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface {
    public static final String ERR_BAD_REGEX = "Invalid pattern";
    public static final String ERR_IO_EXCEPTION = "IOException";
    public static final String ERR_IS_DIR = "%s: Is a directory";
    public static final String ERR_NO_INPUT = "No InputStream and no filenames";
    public static final String ERR_NO_FILE_ARGS = "No files provided";
    public static final String ERR_NO_ISTREAM = "InputStream not provided";
    public static final String ERR_NO_OSTREAM = "OutputStream not provided";
    public static final String ERR_NO_SUCH_FILE = "%s: No such file or directory";
    public static final String ERR_NULL_ARGS = "Null arguments";
    public static final String FMT_W_FILENAME = "%s:%s";

    @Override
    public String grepFromStdin(String pattern, Boolean isInvert, InputStream stdin)
            throws GrepException {
        if (stdin == null) {
            throw new GrepException(ERR_NO_ISTREAM);
        }

        Pattern compiledPattern = compilePattern(pattern);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin))) {
            return bufferedReader.lines()
                                 .filter(line -> compiledPattern.matcher(line).find() == !isInvert)
                                 .map(match -> match + STRING_NEWLINE)
                                 .collect(Collectors.joining());
        } catch (IOException e) {
            throw new GrepException(ERR_IO_EXCEPTION, e);
        }
    }

    @Override
    public String grepFromMultipleFiles(String pattern, Boolean isInvert, String... fileNames)
            throws GrepException {
        if (fileNames == null || fileNames.length <= 0) {
            throw new GrepException(ERR_NO_FILE_ARGS);
        }

        Pattern compiledPattern = compilePattern(pattern);
        boolean shldAddFileName = fileNames.length > 1;
        String[] fileResults = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            try {
                fileResults[i] = grepFromOneFile(compiledPattern, isInvert, shldAddFileName,
                                                fileNames[i]);
            } catch (GrepException e) {
                fileResults[i] = e.getMessage();
            }
        }

        return Arrays.stream(fileResults)
                               .filter(fileResult -> !StringUtils.isBlank(fileResult))
                               .map(match -> match + STRING_NEWLINE)
                               .collect(Collectors.joining());
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws GrepException {
        if (args == null) {
            throw new GrepException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new GrepException(ERR_NO_OSTREAM);
        }

        GrepArgsParser parser = new GrepArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new GrepException(e.getMessage(), e);
        }

        String[] fileNames = parser.getFileNames();
        String pattern = parser.getPattern();
        Boolean isInvert = parser.isInvert();

        if (stdin == null && fileNames == null) {
            throw new GrepException(ERR_NO_INPUT);
        }

        String result;
        if (fileNames == null) {
            result = grepFromStdin(pattern, isInvert, stdin);
        } else {
            result = grepFromMultipleFiles(pattern, isInvert, fileNames);
        }

        try {
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new GrepException(ERR_IO_EXCEPTION, e);
        }
    }

    /**
     * Returns the grep results for one file. If there is no results, returns "".
     * Else, returns <matching line 1> + NEWLINE + ... + NEWLINE +  <matching line n>
     * If shldAddFileName, each line in the results will include the name of the file:
     * <file name>:<matching line>
     * @param pattern
     * @param isInvert
     * @param shldAddFileName
     * @param fileName can refer to relative or absolute paths to file
     * @return
     * @throws GrepException
     */
    private String grepFromOneFile(Pattern pattern, Boolean isInvert, Boolean shldAddFileName,
                                   String fileName) throws GrepException {
        Path filePath = new File(fileName).toPath();
        if(!filePath.isAbsolute()) {
            filePath = Paths.get(Environment.currentDirectory, fileName);
        }

        if (!Files.exists(filePath)) {
            throw new GrepException(String.format(ERR_NO_SUCH_FILE, fileName));
        }

        if (Files.isDirectory(filePath)) {
            throw new GrepException(String.format(ERR_IS_DIR, fileName));
        }

        try {
            Stream<String> lines = Files.lines(filePath);

            return lines.filter(line -> pattern.matcher(line).find() == !isInvert)
                        .map(line -> shldAddFileName ? String.format(FMT_W_FILENAME, fileName, line) : line)
                        .collect(Collectors.joining(STRING_NEWLINE));
        } catch (IOException e) {
            throw new GrepException(ERR_IO_EXCEPTION, e);
        }
    }

    /**
     * Compile the pattern into a Pattern object
     * @param pattern
     * @return
     * @throws GrepException when pattern is invalid
     */
    private Pattern compilePattern(String pattern) throws GrepException {
        try {
            return Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            throw new GrepException(ERR_BAD_REGEX, e);
        }
    }
}
