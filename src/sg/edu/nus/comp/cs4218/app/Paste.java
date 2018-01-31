package sg.edu.nus.comp.cs4218.app;

import java.io.InputStream;

import sg.edu.nus.comp.cs4218.Application;

public interface Paste extends Application {
	
	/**
	 * Returns string of line-wise concatenated 
	 * (tab-separated) Stdin arguments. If only 
	 * one Stdin arg is specified, echo back the
	 * Stdin. 
	 * @param stdin InputStream containing 
	 * arguments from Stdin
	 * @throws Exception
	 */
	public String mergeStdin(InputStream stdin) 
			throws Exception;
	
	/**
	 * Returns string of line-wise concatenated 
	 * (tab-separated) files. If only one file
	 * is specified, echo back the file content. 
	 * @param fileName Array of file names to 
	 * be read and merged
	 * @throws Exception
	 */
	public String mergeFile(String... fileName) 
			throws Exception;
	
	/**
	 * Returns string of line-wise concatenated 
	 * (tab-separated) files and Stdin arguments.
	 * @param stdin InputStream containing 
	 * arguments from Stdin
	 * @param fileName Array of file names to be 
	 * read and merged
	 * @throws Exception
	 */
	public String mergeFileAndStdin(InputStream stdin, String... fileName) 
			throws Exception;

}
