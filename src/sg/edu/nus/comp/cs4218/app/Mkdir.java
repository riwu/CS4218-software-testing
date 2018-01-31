package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface Mkdir extends Application {

	/**
	 * Create folder from the given folder names. Do nothing
	 * if folder already exists. If folder name is a path 
	 * format, create the folder that satisfies the path
	 * specification.
	 * @param folderName Array of string of folder names to
	 * be created
	 * @throws Exception
	 */
	public void createFolder(String... folderName) 
			throws Exception;
	
}
