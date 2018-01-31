package sg.edu.nus.comp.cs4218.app;

import java.io.InputStream;

import sg.edu.nus.comp.cs4218.Application;

public interface Grep extends Application {

	/**
	 * Returns string containing lines which match the specified pattern in
	 * Stdin
	 * @param pattern String specifying a regular expression in JAVA format
	 * @param isInvert Boolean option to filter the output that doesn't match 
	 * the pattern
	 * @param stdin InputStream containing arguments from Stdin
	 * @throws Exception
	 */
	public String grepFromStdin(String pattern, Boolean isInvert, InputStream stdin) 
			throws Exception;

	/**
	 * Returns string containing lines which match the specified pattern in the
	 * given files
	 * @param pattern String specifying a regular expression in JAVA format
	 * @param isInvert Boolean option to filter the output that doesn't match 
	 * the pattern
	 * @param fileNames Array of file names
	 * @throws Exception
	 */
	public String grepFromMultipleFiles(String pattern, Boolean isInvert, String... fileNames) 
			throws Exception;
	
}
