package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;


/**
 * The cat command concatenates the content of given files and prints on the
 * standard output.
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

	/**
	 * Runs the cat application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application. Each array element is
	 *            the path to a file. If no files are specified stdin is used.
	 * @param stdin
	 *            An InputStream. The input for the command is read from this
	 *            InputStream if no files are specified.
	 * @param stdout
	 *            An OutputStream. The output of the command is written to this
	 *            OutputStream.
	 * 
	 * @throws CatException
	 *             If the file(s) specified do not exist or are unreadable.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws CatException {

		if (args == null || args.length == 0) {
			if (stdin == null || stdout == null) {
				throw new CatException("Null Pointer Exception");
			}
			try {
				int intCount;
				while ((intCount = stdin.read()) != -1) {
					stdout.write(intCount);
				}
			} catch (Exception exIO) {
				throw new CatException("Exception Caught");
			}
		} else {

			//Arrays.stream(String.join(" ", args).split("\\s+")).forEach(System.out::println);

			// transform each filenames into Stream of Path
			final Stream<Path> paths = Arrays.stream(args)
					  						 .map(fileName -> Paths.get(fileName));

			final StringBuilder result = new StringBuilder();

			// by default Stream runs in parallel. We want serial in this case.
			paths.forEachOrdered(path -> {
				// skip if the path is a directory
				// maybe throw exception?
				if (Files.isDirectory(path)) {
					return;
				}

				try {

					result.append(new String(getContent(path)));

				}catch (Exception ex){
					ex.printStackTrace();
				}
			});

			try {
				stdout.write(result.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
  
  	@Override
	public byte[] getContent(Path file) throws CatException {
        try {
        	return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new CatException(e.getMessage());
        }
	}

	/**
	 * Checks if a file is readable.
	 * 
	 * @param filePath
	 *            The path to the file
	 * @return True if the file is readable.
	 * @throws CatException
	 *             If the file is not readable
	 */
	boolean checkIfFileIsReadable(Path filePath) throws CatException {
		
		if (Files.isDirectory(filePath)) {
			throw new CatException("This is a directory");
		}
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return true;
		} else {
			throw new CatException("Could not read file");
		}
	}
}
