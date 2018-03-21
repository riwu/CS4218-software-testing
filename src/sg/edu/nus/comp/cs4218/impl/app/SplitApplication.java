package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The cd command changes current directory to the specified directory.
 * <p>
 * <p>
 * <b>Command format:</b> <code>cd PATH</code>
 * </p>
 */
public class SplitApplication implements SplitInterface {

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
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            splitFileByBytes("/tmp/dropbox-antifreeze-0N9a1Y", "x", "1b");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {
        Path filePath = Paths.get(fileName);
        byte[] fileBytes = Files.readAllBytes(filePath);
        int splitByte = bytes(bytesPerFile);


        int i = 1;
        int bytePosition = 0;
        while (bytePosition < fileBytes.length) {

            int toBytePosition = bytePosition + splitByte > fileBytes.length ? fileBytes.length : bytePosition + splitByte;

            writeBytesToFile(Arrays.copyOfRange(fileBytes, bytePosition, toBytePosition), prefix + toBijectiveBase26(i));

            i++;
            bytePosition += toBytePosition;
        }
    }

    public void writeBytesToFile(byte[] byteArray, String filename) {
        try (FileOutputStream fos = new FileOutputStream("/tmp/" + filename)) {
            fos.write(byteArray);
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
