package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CdInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The cd command changes current directory to the specified directory.
 * <p>
 * <p>
 * <b>Command format:</b> <code>cd PATH</code>
 * </p>
 */
public class CdApplication implements CdInterface {
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
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
    	if(args != null && args.length > 0) {
    		try {
				changeToDirectory(args[0]);
			} catch (Exception e) {
				throw new CdException(e.getMessage());
			}
    	}
    }

    @Override
    public void changeToDirectory(String path) throws Exception {
    	Path currentDir = Paths.get(Environment.currentDirectory);
    	Path newPath = Paths.get(path);
    	if(!newPath.isAbsolute()) {
    		newPath = currentDir.resolve(path);
    	}
    	File file = newPath.toFile();
    	if(!file.exists()) {
    		throw new Exception(path + ": No such file or directory");
    	}
    	if(!file.isDirectory()) {
    		throw new Exception(path + ": Not a directory");
    	}
    	Environment.currentDirectory = file.getCanonicalPath().toString();
    }


}
