package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.InputStream;
import java.io.OutputStream;

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

    }

    @Override
    public void splitFileByLines(String fileName, String prefix, int linesPerFile) throws Exception {

    }

    @Override
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {

    }





}