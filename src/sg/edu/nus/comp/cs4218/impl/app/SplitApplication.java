package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.SplitException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The cd command changes current directory to the specified directory.
 * <p>
 * <p>
 * <b>Command format:</b> <code>cd PATH</code>
 * </p>
 */
public class SplitApplication implements SplitInterface {

    private static final String DEFAULT_PREFIX = "x";
    private static final int DEFAULT_LINES = 1000;

    /**
     * Runs the cd application with the specified path.
     *
     * @param args   Array of arguments for the application. Each array element is
     *               the path to a file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this
     *               InputStream if no files are specified.
     * @param stdout An OutputStream. The output of the command is written to this
     *               OutputStream.
     * @throws
     */

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws SplitException {
        boolean isSplitByLine = getIsSplitByLine(args);
        String prefix = getPrefix(args);

        try {
            InputStream source = getSource(args, stdin);
            if (isSplitByLine) {
                splitFileByLines(source, prefix, getLinesArg(args));
            } else {
                splitFileByBytes(source, prefix, getBytesArg(args));
            }
        } catch (Exception e) {
            throw new SplitException(e.getMessage());
        }
    }

    private boolean getIsSplitByLine(String[] args) {
        return args.length == 0 || !args[0].equals("-b");
    }

    private String getBytesArg(String[] args) {
        return args[1];
    }

    private int getLinesArg(String[] args) {
        return (args.length > 1 && args[0].equals("-l")) ? Integer.parseInt(args[1]) : DEFAULT_LINES;
    }

    private boolean hasOptions(String[] args) {
        return args.length > 0 && (args[0].equals("-b") || args[0].equals("-l"));
    }

    private InputStream getSource(String[] args, InputStream stdin) throws FileNotFoundException {
        if (args.length == 0) {
            return stdin;
        }
        if (hasOptions(args)) {
            return args.length > 2 ? new FileInputStream(args[2]) : stdin;
        }
        return new FileInputStream(args[0]);
    }

    private String getPrefix(String[] args) {
        int prefixIndex = hasOptions(args) ? 3 : 1;
        return args.length > prefixIndex ? args[prefixIndex] : DEFAULT_PREFIX;
    }

    @Override
    public void splitFileByLines(InputStream stdin, String prefix, int linesPerFile) throws Exception {
        if (linesPerFile <= 0) {
            throw new SplitException("Lines per file should be positive");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        List<String> lines = new ArrayList<>();
        reader.lines().forEach(lines::add);

        PrintWriter writer = null;
        for (int i = 0; i < lines.size(); i++) {
            if (i % linesPerFile == 0) {
                if (writer != null) writer.close();
                writer = new PrintWriter(Environment.currentDirectory + File.separator +
                        prefix + getSuffix(i / linesPerFile + 1), "UTF-8");
            }
            writer.println(lines.get(i));
        }
        if (writer != null) writer.close();
    }

    @Override
    public void splitFileByLines(String fileName, String prefix, int linesPerFile) throws Exception {
        splitFileByLines(new FileInputStream(new File(fileName)), prefix, linesPerFile);
    }

    @Override
    public void splitFileByBytes(InputStream stdin, String prefix, String bytesPerFile) throws Exception {
        int bytes = bytes(bytesPerFile);
        byte[] data = new byte[bytes];
        int i = 1;
        int nRead;
        while ((nRead = stdin.read(data)) != -1) {
            writeBytesToFile(Arrays.copyOfRange(data, 0, nRead), prefix + getSuffix(i));
            i++;
        }
    }

    @Override
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {
        splitFileByBytes(new FileInputStream(new File(fileName)), prefix, bytesPerFile);
    }

    public void writeBytesToFile(byte[] byteArray, String filename) {
        try (FileOutputStream fos = new FileOutputStream(Environment.currentDirectory + File.separator + filename)) {
            fos.write(byteArray);
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String getSuffix(int num) {
        return topologicalPrefix(num) + toBijectiveBase26(num);
    }

    public String topologicalPrefix(int num) {
        if (num <= 676) return "";

        String prefix = "~";
        int count = (int) Math.floor(Math.log(num) / Math.log(26)) - 1;

        return new String(new char[count]).replace("\0", prefix);

    }

    public String toBijectiveBase26(int num) {
        final int skip = 26;
        num += skip;

        StringBuilder result = new StringBuilder();

        while (num > 0) {
            --num;
            result.append((char) ('a' + num % 26));
            num /= 26;
        }

        return result.reverse().toString();

    }

    private int bytes(String bytesPerFile) throws SplitException {
        // this regex matches any number of digits follow by exactly one of [b,k,m]
        Pattern pattern = Pattern.compile("^(\\d+)([b,k,m]?)$");
        Matcher matcher = pattern.matcher(bytesPerFile);

        // doesn't match, return negative value
        if (!matcher.matches()) return -1;

        // extract matched components
        int base = Integer.parseInt(matcher.group(1)); // guaranteed digits by regex
        if (base <= 0) {
            throw new SplitException("Bytes per file should be positive");
        }

        String modifier = matcher.group(2);
        if (modifier.isEmpty()) {
            return base;
        }
        char multiplier = modifier.charAt(0); // guaranteed single char by regex

        // calculate & return the exact bytes in numerical form
        switch (multiplier) {
            case 'b':
                return base * 512;
            case 'k':
                return base * 1024;
            case 'm':
                return base * 1048576;
            default:
                return -1;
        }

    }

}
