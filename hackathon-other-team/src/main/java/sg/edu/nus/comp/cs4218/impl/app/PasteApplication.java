package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplication implements PasteInterface{
    public static final String ERR_NO_INPUT = "No inputStream and no filenames";
    public static final String ERR_IO_EXCEPTION = "IOException";
    public static final String ERR_NO_OSTREAM = "OutputStream not provided";
    public static final String ERR_NO_ISTREAM = "InputStream not provided";
    public static final String ERR_NO_SUCH_FILE = "%s: No such file or directory";
    public static final String ERR_NULL_ARGS = "Null arguments";

    /**
     * Note: - just echo back all contents, as in bash
     */
    @Override
    public String mergeStdin(InputStream stdin) throws PasteException {
        if (stdin == null) {
            throw new PasteException(ERR_NO_ISTREAM);
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin))) {
            String results = bufferedReader.lines()
                                           .collect(Collectors.joining(String.valueOf(STRING_NEWLINE)));

            return results.isEmpty() ? "" : results + STRING_NEWLINE;
        } catch (IOException e) {
            throw new PasteException(ERR_IO_EXCEPTION, e);
        }
    }

    /**
     * Note: - mergeFile treats directories as empty files as in bash
     *       - results end with newline
     */
    @Override
    public String mergeFile(String... fileName) throws PasteException {
        Path[] paths = getPaths(fileName);

        // echo back file contents when only one file specified
        if (paths.length == 1) {
            try {
                return Files.isDirectory(paths[0]) ? "" : new String(Files.readAllBytes(paths[0]));
            } catch (IOException e) {
                throw new PasteException(ERR_IO_EXCEPTION, e);
            }
        }

        ArrayList<StringBuilder> resultLines = new ArrayList<>();

        for (int pathIdx = 0; pathIdx < paths.length; pathIdx++) {
            // treat directory as empty file
            if (Files.isDirectory(paths[pathIdx])) {
                resultLines.forEach(line -> line.append(CHAR_TAB));
                continue;
            }

            try {
                List<String> fileLines = Files.readAllLines(paths[pathIdx]);
                int numResultLines = resultLines.size();
                int numFileLines = fileLines.size();

                for (int lineIdx = 0; lineIdx < numResultLines; lineIdx++) {
                    String fileLine = (lineIdx < numFileLines) ? fileLines.get(lineIdx) : "";
                    resultLines.get(lineIdx).append(CHAR_TAB).append(fileLine);
                }

                if (numFileLines > numResultLines) {
                    for (int fileLineIdx = numResultLines; fileLineIdx < numFileLines; fileLineIdx++) {
                        StringBuilder line = new StringBuilder();
                        line.append(StringUtils.multiplyChar(CHAR_TAB, pathIdx)).append(fileLines.get(fileLineIdx));
                        resultLines.add(line);
                    }
                }

            } catch (IOException e) {
                throw new PasteException(ERR_IO_EXCEPTION, e);
            }
        }

        return resultLines.isEmpty() ? "" : String.join(STRING_NEWLINE, resultLines) + STRING_NEWLINE;
    }

    /**
     * Note: - works by combine results from mergefile and mergestdin
     */
    @Override
    public String mergeFileAndStdin(InputStream stdin, String... fileName) throws PasteException {
        // get results
        String stdinResults = mergeStdin(stdin);
        String fileResults = mergeFile(fileName);

        // if both is "", just return "" immediately
        if (stdinResults.isEmpty() && fileResults.isEmpty()) {
            return "";
        }

        String[] stdinLines = stdinResults.substring(0, stdinResults.length()-1)
                                          .split(STRING_NEWLINE);
        String[] fileLines = fileResults.substring(0, fileResults.length()-1)
                                         .split(STRING_NEWLINE);

        StringBuilder resultBuilder = new StringBuilder();
        int numResultLines = Math.max(fileLines.length, stdinLines.length);
        for(int i = 0; i < numResultLines; i++) {
            String stdinLine = i < stdinLines.length ? stdinLines[i] : "";
            String fileLine = i < fileLines.length ? fileLines[i] : "";
            resultBuilder.append(stdinLine)
                         .append(CHAR_TAB)
                         .append(fileLine)
                         .append(STRING_NEWLINE);
        }

        return resultBuilder.toString();
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws PasteException {
        if (args == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new PasteException(ERR_NO_OSTREAM);
        }

        if (args.length == 0) {
            throw new PasteException(ERR_NO_INPUT);
        }

        String results;
        String[] files = extractFiles(args);
        boolean hasFileNames = files.length > 0;
        boolean shldPasteStdin = args.length > files.length;

        if (hasFileNames && shldPasteStdin) {
            results = mergeFileAndStdin(stdin, files);
        } else if (hasFileNames) {
            results = mergeFile(files);
        } else {
            results = mergeStdin(stdin);
        }

        try {
            stdout.write(results.getBytes());
        } catch (IOException e) {
            throw new PasteException(ERR_IO_EXCEPTION, e);
        }
    }

    /**
     * Get all the full paths of pathnames
     * If the path was relative, wrt to curr directory
     * No matter if the pathname refer to a file or directory
     * @param pathNames array of absolute or relative paths
     * @return Path[]
     * @throws PasteException if at least one file does not exist
     */
    private Path[] getPaths(String... pathNames) throws PasteException {
        Path[] paths = new Path[pathNames.length];
        for (int i = 0; i < pathNames.length; i++) {

            Path filePath = new File(pathNames[i]).toPath();
            if (!filePath.isAbsolute()) {
                filePath = Paths.get(Environment.currentDirectory, pathNames[i]);
            }

            if (Files.notExists(filePath)) {
                throw new PasteException(String.format(ERR_NO_SUCH_FILE, pathNames[i]));
            }
            paths[i] = filePath; 
        }
        return paths;
    }

    /**
     * Extract filenames from args (i.e. remove '-' from args)
     * @param args
     * @return array of filenames
     */
    private String[] extractFiles(String... args) {
        return Arrays.stream(args)
                     .filter(arg -> arg != null && !arg.equals("-"))
                     .toArray(String[]::new);
    }
}
