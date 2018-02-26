package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.app.ExitInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class ExitApplication implements ExitInterface {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		try {
			terminateExecution();
		} catch (Exception e) {
			System.exit(-1);
		}
	}

	@Override
	public void terminateExecution() throws Exception {
		System.exit(0);
	}

}
