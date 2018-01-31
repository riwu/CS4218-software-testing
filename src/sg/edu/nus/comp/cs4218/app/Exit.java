package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface Exit extends Application {

	/**
	 * Terminate shell.
	 * @throws Exception
	 */
	public void terminateExecution() 
			throws Exception;
	
}
