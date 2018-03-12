package sg.edu.nus.comp.cs4218.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.nus.comp.cs4218.app.CmpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CmpException;

public class CmpApplication implements CmpInterface {

	private static final String SIMPLE = "Files differ";
	private static final String NORMAL_FORMAT = "%1$s %2$s differ: %3$s";
	private static final String NORMAL_DIFF = "char %1$s, line %2$s%3$s";
	private static final String LONG_FORMAT = "%1$s %2$s %3$s"; //byte# octal1# octal2#
	private static final String IS_FORMAT = " is %1$s %2$s";
	private static final String CHAR_FORMAT = "%1$s %2$s"; //octal# value#
	private static final String STDIN = "-";
	private boolean isPrintCharDiff, isPrintSimplify, isPrintOctalDiff;
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		int fileCount = 0;
		boolean hasStdin;
		hasStdin = isPrintCharDiff = isPrintSimplify = isPrintOctalDiff = false;
		List<String> fileNames = new ArrayList<String> ();
		if(stdout == null) {
			throw new CmpException("Null pointer exception");
		}
		for(int i = 0; i < args.length; i++) {
			if(STDIN.equals(args[i])) {
				if(stdin == null) {
					throw new CmpException("No file to read from stdin");
				}
				else {
					hasStdin = true;
					fileCount++;
				}
			}
			else if (args[i].startsWith("-")) {
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
		if(fileNames.isEmpty()) {
			throw new CmpException("Cannot compare stdin with stdin");
		}
		try {
			stdout.write(compare(stdin, hasStdin, fileNames));
			if(stdout == System.out) {
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
			message = getSimpleString(bytesA, bytesB);
		}
		else if(isPrintOctalDiff) {
			message = getLongFormatString(bytesA, bytesB, isPrintCharDiff);
		}
		else {
			message = getNormalFormatString(bytesA, bytesB, isPrintCharDiff, false);
			if(message.length() > 0) {
				message = String.format(NORMAL_FORMAT, fileNameA, fileNameB, message);
			}
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
			message = getSimpleString(bytesA, bytesB);
		}
		else if(isPrintOctalDiff) {
			message = getLongFormatString(bytesA, bytesB, isPrintCharDiff);
		}
		else {
			message = getNormalFormatString(bytesA, bytesB, isPrintCharDiff, false);
			if(message.length() > 0) {
				message = String.format(NORMAL_FORMAT, fileName, STDIN, message);
			}
		}
		return message;
	}
	
	private String getNormalFormatString(byte[] fileA, byte[] fileB, Boolean isCharDiff, boolean hasStdin) throws Exception {
		int line = 1;
		StringBuilder strBuilder = new StringBuilder();
		String appendString = "";
		for(int i=0; i < fileA.length && i < fileB.length; i++) {
			if(fileA[i] != fileB[i]) {
				if(isCharDiff) {
					appendString = getIsFormatString(fileA[i], fileB[i]);
				}
				strBuilder.append(String.format(NORMAL_DIFF, i+1, line, appendString));
				break;
			}
			if(fileA[i] == fileB[i] && fileA[i] == '\n') {
				line++;
			}
		}
		return strBuilder.toString();
	}

	private String getSimpleString(byte[] fileA, byte[] fileB) throws Exception {
		String message;
		boolean areEqual = Arrays.equals(fileA, fileB);
		if(areEqual) {
			message = "";
		}
		else {
			message = SIMPLE;
		}
		return message;
	}
	
	private String getIsFormatString(byte first, byte second) {
		return String.format(IS_FORMAT, getOctalCharString(first), getOctalCharString(second));
	}
	
	private String getOctalCharString(byte single) {
		return String.format(CHAR_FORMAT, toOctal(single), Byte.toString(single));
	}
	
	private String getLongFormatString(byte[] fileA, byte[] fileB, boolean isCharDiff) throws Exception {
		StringBuilder strBuilder = new StringBuilder();
		String diffA, diffB;
		for(int i=0; i < fileA.length && i < fileB.length; i++) {
			if(fileA[i] != fileB[i]) {
				if(strBuilder.length() > 0) {
					strBuilder.append(System.lineSeparator());
				}
				if(isCharDiff) {
					diffA = getOctalCharString(fileA[i]);
					diffB = getOctalCharString(fileB[i]);
				}
				else {
					diffA = toOctal(fileA[i]);
					diffB = toOctal(fileB[i]);
				}
				strBuilder.append(String.format(LONG_FORMAT, i+1, diffA, diffB));
			}
		}
		return strBuilder.toString();
	}

	private String toOctal(byte single) {
		BigInteger bigInt = new BigInteger(new byte[] {single});
		return bigInt.toString(8);
	}

}
