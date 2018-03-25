package sg.edu.nus.comp.cs4218.impl.app;

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

    private static final String PREFIX = "x";

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
        if (args.length < 2) {
            throw new SplitException("Split option not specified");
        }
        try {
            InputStream source = args.length > 2 ? new FileInputStream(args[2]) : stdin;
            String prefix = args.length > 3 ? args[3] : PREFIX;
            switch (args[0]) {
                case "-l":
                    splitFileByLines(source, prefix, Integer.parseInt(args[1]));
                    break;
                case "-b":
                    splitFileByBytes(source, prefix, args[1]);
                    break;
                default:
                    throw new SplitException("Invalid split option specified");
            }
        } catch (Exception e) {
            throw new SplitException(e.getMessage());
        }
    }

    @Override
    public void splitFileByLines(InputStream stdin, String prefix, int linesPerFile) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        List<String> lines = new ArrayList<>();
        reader.lines().forEach(lines::add);

        PrintWriter writer = null;
        for (int i = 0; i < lines.size(); i++) {
            if (i % linesPerFile == 0) {
                if (writer != null) writer.close();
                writer = new PrintWriter(prefix + toBijectiveBase26(i / linesPerFile + 1), "UTF-8");
            }
            writer.println(lines.get(i));
        }
        if (writer != null) writer.close();
    }

    @Override
    public void splitFileByBytes(InputStream stdin, String prefix, String bytesPerFile) throws Exception {
        int bytes = bytes(bytesPerFile);
        byte[] data = new byte[bytes];
        int i = 1;
        int nRead;
        while ((nRead = stdin.read(data)) != -1) {
            writeBytesToFile(Arrays.copyOfRange(data, 0, nRead), prefix + toBijectiveBase26(i));
            i++;
        }
    }

    public void writeBytesToFile(byte[] byteArray, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(byteArray);
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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

    private int bytes(String bytesPerFile) {
        // this regex matches any number of digits follow by exactly one of [b,k,m]
        Pattern pattern = Pattern.compile("^(\\d+)([b,k,m])$");
        Matcher matcher = pattern.matcher(bytesPerFile);

        // doesn't match, return negative value
        if (!matcher.matches()) return -1;

        // extract matched components
        int base = Integer.parseInt(matcher.group(1)); // guaranteed digits by regex
        char multiplier = matcher.group(2).charAt(0); // guaranteed single char by regex

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
