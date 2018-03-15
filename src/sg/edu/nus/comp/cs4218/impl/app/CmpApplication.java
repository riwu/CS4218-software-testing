package sg.edu.nus.comp.cs4218.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.app.CmpInterface;
import sg.edu.nus.comp.cs4218.impl.CmpApplicationUtil;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CmpException;

public class CmpApplication implements CmpInterface {
	
	private boolean isPrintCharDiff, isPrintSimplify, isPrintOctalDiff;
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		int fileCount = 0;
		boolean hasStdin;
		hasStdin = isPrintCharDiff = isPrintSimplify = isPrintOctalDiff = false;
		List<String> fileNames = new ArrayList<String> ();
		if(stdin == null || stdout == null) {
			throw new CmpException("Null pointer exception");
		}
		for(int i = 0; i < args.length; i++) {
			if(CmpApplicationUtil.STDIN.equals(args[i])) {
				if(!hasStdin) {
					fileCount++;
				}
				hasStdin = true;
			}
			else if (args[i].matches("^-[lcs]+$")) {
				extractOptions(args[i]);
			}
			else {
				File file = new File(args[i]);
				if(!file.exists() || file.isDirectory()) {
					throw new CmpException(file.getName() + " cannot be resolved to a file");
				}
				fileNames.add(args[i]);
				fileCount++;
			}
		}
		if(fileCount != 2) {
			throw new CmpException("There must be two files to compare");
		}
		try {
			stdout.write(compare(stdin, hasStdin, fileNames));
			if(stdout instanceof ByteArrayOutputStream) {
				stdout.write(System.lineSeparator().getBytes());
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private byte[] compare(InputStream stdin, boolean hasStdin, List<String> fileNames) {
		String output = "";
		try {
			if(hasStdin) {
				output = cmpFileAndStdin(fileNames.get(0), stdin, isPrintOctalDiff, isPrintOctalDiff, isPrintOctalDiff);
			}
			else {
				output = cmpTwoFiles(fileNames.get(0), fileNames.get(1), isPrintCharDiff, isPrintSimplify, isPrintOctalDiff);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return output.getBytes();
	}

	private void extractOptions(String arg) {
		String options = arg.substring(1);
		if(options.contains("l")) {
			isPrintOctalDiff = true;
		}
		if(options.contains("s")) {
			isPrintSimplify = true;
		}
		if(options.contains("c")) {
			isPrintCharDiff = true;
		}
	}

	@Override
	public String cmpTwoFiles(String fileNameA, String fileNameB, Boolean isPrintCharDiff, Boolean isPrintSimplify,
			Boolean isPrintOctalDiff) throws Exception {
		String message;
		byte[] bytesA = Files.readAllBytes(Paths.get(fileNameA));
		byte[] bytesB = Files.readAllBytes(Paths.get(fileNameB));
		if(isPrintSimplify) {
			message = CmpApplicationUtil.getSimpleString(bytesA, bytesB);
		}
		else if(isPrintOctalDiff) {
			message = CmpApplicationUtil.getLongFormatString(bytesA, bytesB, isPrintCharDiff);
		}
		else {
			message = CmpApplicationUtil.getNormalFormatString(fileNameA, fileNameB, 
						bytesA, bytesB, isPrintCharDiff);
		}
		return message;
	}

	@Override
	public String cmpFileAndStdin(String fileName, InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify,
			Boolean isPrintOctalDiff) throws Exception {
		String message;
		byte[] bytesA = Files.readAllBytes(Paths.get(fileName));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int read;
		byte[] data = new byte[1024];
		while((read = stdin.read(data, 0, data.length)) != -1) {
			baos.write(data, 0, read);
		}
		baos.flush();
		byte[] bytesB = baos.toByteArray();
		if(isPrintSimplify) {
			message = CmpApplicationUtil.getSimpleString(bytesA, bytesB);
		}
		else if(isPrintOctalDiff) {
			message = CmpApplicationUtil.getLongFormatString(bytesA, bytesB, isPrintCharDiff);
		}
		else {
			message = CmpApplicationUtil.getNormalFormatString(fileName, CmpApplicationUtil.STDIN, 
						bytesA, bytesB, isPrintCharDiff);
		}
		return message;
	}

}
