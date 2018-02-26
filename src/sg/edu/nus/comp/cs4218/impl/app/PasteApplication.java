package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PasteException;

public class PasteApplication implements PasteInterface {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if(args == null || args.length == 0) {
			if(stdin == null || stdout == null) {
				throw new PasteException("Null Pointer Exception");
			}
			try {
				String contents = mergeStdin(stdin);
				stdout.write(contents.getBytes());
				stdout.write(System.lineSeparator().getBytes());
			} catch (Exception e) {
				throw new PasteException(e.getMessage());
			}
		}
		else {
			ArrayList<String> list = new ArrayList<String>();
			for(String arg: args) {
				File file = new File(arg);
				if(!file.isFile()) {
					throw new PasteException(file.getName() + "is not a file");
				}
				list.add(arg);
			}
			String[] files = list.toArray(new String[list.size()]);
			try {
				String contents = mergeFile(files);
				stdout.write(contents.getBytes());
				stdout.write(System.lineSeparator().getBytes());
			} catch (Exception e) {
				throw new PasteException("Unable to merge file(s)");
			}
		}
	}

	@Override
	public String mergeStdin(InputStream stdin) throws Exception {
		BufferedReader bReader = null;
		StringBuilder strBuilder = new StringBuilder();
		String line;
		try {
			bReader = new BufferedReader(new InputStreamReader(stdin));
			line = bReader.readLine();
			strBuilder.append(line);
			while ((line = bReader.readLine()) != null) {
				strBuilder.append(System.lineSeparator());
				strBuilder.append(line);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (bReader != null) {
				try {
					bReader.close();
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
			}
		}

		return strBuilder.toString();
	}

	@Override
	public String mergeFile(String... fileName) throws Exception {
		StringBuilder strBuilder = new StringBuilder();
		Scanner[] scList = new Scanner[fileName.length];
		try {
			for(int i = 0; i < fileName.length; i++) {
				scList[i] = new Scanner(new File(fileName[i]));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
		int lastFileIdx = scList.length - 1;
		boolean hasMoreLines = false;
		
		for(int i = 0; i < scList.length || hasMoreLines; i++) {
			String line = "";
			boolean hasLine = scList[i].hasNextLine();
			if(hasLine) {
				line = scList[i].nextLine();
			}
			if(i < lastFileIdx) {
				line.replace("\n", "").replace("\r", "");
				line += "\t";
			}
			hasMoreLines |= scList[i].hasNextLine();
			strBuilder.append(line);
			if(i == lastFileIdx) {
				if(hasMoreLines) {
					strBuilder.append(System.lineSeparator());
					i = -1;
					hasMoreLines = false;
				}
				else {
					break;
				}
			}
		}
		for(int i = 0; i < scList.length; i++) {
			scList[i].close();
		}
		return strBuilder.toString();
	}

	/**
	 * Unimplemented due to project description stating use stdin only if no file specified
	 */
	@Override
	public String mergeFileAndStdin(InputStream stdin, String... fileName) throws Exception {
		return null;
	}
	
}
