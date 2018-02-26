package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.app.CmpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class CmpApplication implements CmpInterface {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String cmpTwoFiles(String fileNameA, String fileNameB, Boolean isPrintCharDiff, Boolean isPrintSimplify,
			Boolean isPrintOctalDiff) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cmpFileAndStdin(String fileName, InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify,
			Boolean isPrintOctalDiff) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
