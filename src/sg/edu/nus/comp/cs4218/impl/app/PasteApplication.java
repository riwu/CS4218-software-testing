package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PasteException;

/**
 * The paste command merges corresponding lines of files sequentially,
 * separated by a TAB character and writes it to standard output
 * 
 * <p>
 * <b>Command format:</b> <code>paste [FILE]...</code>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin</dd>
 * </dl>
 * </p>
 */
public class PasteApplication implements PasteInterface {

	@Override
	@SuppressWarnings("PMD.PreserveStackTrace")
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
			Path currentDir = Paths.get(Environment.currentDirectory);
			for(String arg: args) {
				File file = currentDir.resolve(arg).toFile();
				if(!file.isFile()) {
					throw new PasteException(file.getName() + " is not a file");
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
	@SuppressWarnings("PMD.PreserveStackTrace")
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
	@SuppressWarnings("PMD.PreserveStackTrace")
	public String mergeFile(String... fileName) throws Exception {
		StringBuilder strBuilder = new StringBuilder();
		Scanner[] scList = new Scanner[fileName.length];
		Path currentDir = Paths.get(Environment.currentDirectory);
		try {
			for(int i = 0; i < fileName.length; i++) {
				scList[i] = new Scanner(currentDir.resolve(fileName[i]).toFile());
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
	 * Unimplemented due to project description stating use stdin only
	 * if no file is specified so this is never called
	 */
	@Override
	public String mergeFileAndStdin(InputStream stdin, String... fileName) throws Exception {
		return null;
	}
	
}
