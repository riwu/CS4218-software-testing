package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class SedApplication implements SedInterface {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String replaceSubstringInFile(String pattern, String replacement, int replacementIndex, String fileName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replaceSubstringInStdin(String pattern, String replacement, int replacementIndex, InputStream stdin)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
