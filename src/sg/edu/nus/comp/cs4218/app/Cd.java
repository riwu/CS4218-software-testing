package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;

public interface Cd extends Application {

	/**
	 * Change the environment context to a different directory. 
	 * @param path String of the path to a directory
	 * @param env Environment context 
	 * @throws Exception
	 */
	public void changeToDirectory(String path, Environment env) 
			throws Exception;
	
}
