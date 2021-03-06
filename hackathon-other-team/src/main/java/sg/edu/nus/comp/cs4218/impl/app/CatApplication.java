package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The cat command concatenates the content of given files and prints on the standard output.
 *
 * <p>
 * <b>Command format:</b> <code>cat [FILE]...</code>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */
public class CatApplication implements CatInterface {
    public static final String ERR_IS_DIR = "This is a directory";
    public static final String ERR_READING_FILE = "Could not read file";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";

    /**
     * Runs the cat application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     *
     * @throws CatException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {

        if (args == null || args.length == 0) {
            if (stdin == null || stdout == null) {
                throw new CatException(ERR_NULL_STREAMS);
            }
            try {
                int intCount;
                while ((intCount = stdin.read()) != -1) {
                    stdout.write(intCount);
                }
            } catch (IOException e) {
                throw new CatException(ERR_GENERAL, e);
            }
        } else {
            int numOfFiles = args.length;
            if (numOfFiles == 0) {
                return;
            }

            Path filePath;
            Path[] filePathArray = new Path[numOfFiles];
            Path currentDir = Paths.get(Environment.currentDirectory);
            boolean isFileReadable;

            for (int i = 0; i < numOfFiles; i++) {
                filePath = currentDir.resolve(args[i]);
                isFileReadable = checkIfFileIsReadable(filePath);
                if (isFileReadable) {
                    filePathArray[i] = filePath;
                }
            }

            for (int j = 0; j < filePathArray.length; j++) {
                try {
                    byte[] byteFileArray = Files.readAllBytes(filePathArray[j]);
                    stdout.write(byteFileArray);
                } catch (IOException e) {
                    throw new CatException(ERR_WRITE_STREAM, e);
                }
            }
        }
    }

    /**
     * Checks if a file is readable.
     *
     * @param filePath The path to the file
     *
     * @return True if the file is readable.
     *
     * @throws CatException If the file is not readable
     */
    private boolean checkIfFileIsReadable(Path filePath) throws CatException {

        if (Files.isDirectory(filePath)) {
            throw new CatException(ERR_IS_DIR);
        }
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            return true;
        } else {
            throw new CatException(ERR_READING_FILE);
        }
    }
}
