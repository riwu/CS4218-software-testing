package sg.edu.nus.comp.cs4218.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CmpInterface;
import sg.edu.nus.comp.cs4218.impl.CmpApplicationUtil;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CmpException;

public class CmpApplication implements CmpInterface {
	
	private static final int CHAR_IDX = 0;
	private static final int SIMPLIFY_IDX = 1;
	private static final int OCTAL_IDX = 2;
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		int fileCount = 0;
		boolean hasStdin;
		hasStdin = false;
		boolean[] flags = new boolean[3];
		List<String> fileNames = new ArrayList<String> ();
		if(stdout == null) {
			throw new CmpException("Null pointer exception");
		}
		for(int i = 0; i < args.length; i++) {
			if(CmpApplicationUtil.STDIN.equals(args[i])) {
				if(stdin == null) {
					throw new CmpException("Specified stdin but stdin is null");
				}
				if(!hasStdin) {
					fileCount++;
				}
				hasStdin = true;
			}
			else if (args[i].matches("^(-[lcs]+)+$")) {
				extractOptions(args[i], flags);
			}
			else {
				Path currentDir = Paths.get(Environment.currentDirectory);
				File file = currentDir.resolve(args[i]).toFile();
				if(!file.isFile()) {
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
			byte[] output = compare(stdin, hasStdin, fileNames, flags);
			stdout.write(output);
			if(output.length > 0 && stdout instanceof ByteArrayOutputStream) {
				stdout.write(System.lineSeparator().getBytes());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void extractOptions(String options, boolean... flags) {
		if(options.contains("l")) {
			flags[OCTAL_IDX] = true;
		}
		if(options.contains("s")) {
			flags[SIMPLIFY_IDX] = true;
		}
		if(options.contains("c")) {
			flags[CHAR_IDX] = true;
		}
	}

	private byte[] compare(InputStream stdin, boolean hasStdin, List<String> fileNames,
				boolean... flags) {
		String output = "";
		try {
			if(hasStdin) {
				output = cmpFileAndStdin(fileNames.get(0), stdin, flags[CHAR_IDX],
							flags[SIMPLIFY_IDX], flags[OCTAL_IDX]);
			}
			else {
				output = cmpTwoFiles(fileNames.get(0), fileNames.get(1), flags[CHAR_IDX],
							flags[SIMPLIFY_IDX], flags[OCTAL_IDX]);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return output.getBytes();
	}

	@Override
	public String cmpTwoFiles(String fileNameA, String fileNameB, Boolean isPrintCharDiff, Boolean isPrintSimplify,
			Boolean isPrintOctalDiff) throws Exception {
		String message;
		Path currentDir = Paths.get(Environment.currentDirectory);
		byte[] bytesA = Files.readAllBytes(currentDir.resolve(fileNameA));
		byte[] bytesB = Files.readAllBytes(currentDir.resolve(fileNameB));
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
		if(message.isEmpty()) {
			hasNoDiffTillEof(fileNameA, fileNameB, bytesA.length, bytesB.length);
		}
		return message;
	}

	@Override
	public String cmpFileAndStdin(String fileName, InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify,
			Boolean isPrintOctalDiff) throws Exception {
		String message;
		Path currentDir = Paths.get(Environment.currentDirectory);
		byte[] bytesA = Files.readAllBytes(currentDir.resolve(fileName));
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
		if(message.isEmpty()) {
			hasNoDiffTillEof(fileName, CmpApplicationUtil.STDIN, bytesA.length, bytesB.length);
		}
		return message;
	}
	
	private void hasNoDiffTillEof(String fileNameA, String fileNameB, int sizeA, int sizeB)
			throws CmpException {
		if(sizeA < sizeB) {
			throw new CmpException(CmpException.EOF_MSG + fileNameA);
		}
		if(sizeA > sizeB) {
			throw new CmpException(CmpException.EOF_MSG + fileNameB);
		}
	}

}
