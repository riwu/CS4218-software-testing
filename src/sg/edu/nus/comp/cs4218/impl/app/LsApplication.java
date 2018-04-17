package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.LsException;

/**
 * The ls command lists the contents of the given folder(s) and prints to the
 * output stream.
 * 
 * <p>
 * <b>Command format:</b> <code>ls [-d] [FOLDER...] [-R]</code>
 * <dl>
 * <dt>FOLDER</dt>
 * <dd>the name of the folder(s). If no folders are specified, use current directory.</dd>
 * <dt>-d</dt>
 * <dd>Option to list directories only.</dd>
 * <dt>-R</dt>
 * <dd>Option to list files and sub-folders recursively.</dd>
 * </dl>
 * </p>
 */
public class LsApplication implements LsInterface {

	@Override
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (stdout == null) {
			throw new LsException("Null Pointer Exception");
		}
		boolean isFoldersOnly = false;
		boolean isRecursive = false;
		int optionCount = 0;
		ArrayList<String> folders = new ArrayList<String>();
		
		for(String arg: args) {
			if("-d".equals(arg)) {
				isFoldersOnly = true;
				optionCount++;
			}
			else if("-R".equals(arg)) {
				isRecursive = true;
				optionCount++;
			}
			else {
				Path currentDir = Paths.get(Environment.currentDirectory);
				File folder = currentDir.resolve(arg).toFile();
				if(folder.isDirectory()) {
					folders.add(arg);
				}
				else {
					throw new LsException(arg + " is not a directory");
				}
			}
		}
		
		try {
			if(args == null || optionCount == args.length) {
				folders.add(".");
			}
			String[] folderNames = folders.toArray(new String[folders.size()]);
			String display = listFolderContent(isFoldersOnly, isRecursive, folderNames);
			stdout.write(display.getBytes());
		} catch (Exception e) {
			throw new LsException(e.getMessage());
		}
	}

	@Override
	public String listFolderContent(Boolean isFoldersOnly, Boolean isRecursive, String... folderName) throws Exception {
		File[] folderContents;
		FileFilter directoryFilter = generateFileFilter(isFoldersOnly);
		StringBuilder strBuilder = new StringBuilder();
		if (isRecursive) {
			for(String name: folderName) {
				strBuilder.append(listFolderContentRecursive(directoryFilter, name));
			}
		}
		else {
			Path currentDir = Paths.get(Environment.currentDirectory);
			boolean displayFolder = false;
			boolean extraNewLine = false;
			if(folderName.length > 1) {
				displayFolder = true;
				extraNewLine = true;
			}
			for(String name: folderName) {
				File folder = currentDir.resolve(name).toFile();
				folderContents = folder.listFiles(directoryFilter);
				Arrays.sort(folderContents);
				if(displayFolder) {
					strBuilder.append(name).append(':').append(System.lineSeparator());
				}
				if(folderContents.length > 0) {
					strBuilder.append(listContents(folderContents));
					if(extraNewLine) {
						strBuilder.append(System.lineSeparator());
					}
				}
			}
		}
		return strBuilder.toString();
	}

	/**
	 * Method to recursively display contents of a directory and all its sub-directories
	 * @param directoryFilter
	 * 		FileFilter to filter only selected files
	 * @param folderName
	 * 		Name of folder to display contents from
	 * @return String of contents found in the directory and all its sub-directories
	 */
	private String listFolderContentRecursive(FileFilter directoryFilter, String folderName) {
		File[] folderContents;
		StringBuilder strBuilder = new StringBuilder();
		
		File folder = new File(folderName);
		Path currentDir = Paths.get(Environment.currentDirectory);
		folder = currentDir.resolve(folderName).toFile();
		if(folder.isDirectory()) {
			strBuilder.append(folderName).append(':').append(System.lineSeparator());
			folderContents = folder.listFiles(directoryFilter);
			Arrays.sort(folderContents);
			if(folderContents.length > 0) {
				strBuilder.append(listContents(folderContents));
			}
			strBuilder.append(System.lineSeparator());
			for(File file: folderContents) {
				String fileName = folderName + File.separator + file.getName();
				strBuilder.append(listFolderContentRecursive(directoryFilter, fileName));
			}
		}
		
		return strBuilder.toString();
	}

	/**
	 * Method to format the folder and its contents in String
	 * 
	 * @param folderContents
	 * 		List of folder contents to display in String
	 * @return Formatted String showing the folder and its contents
	 */
	private String listContents(File... folderContents) {
		StringBuilder strBuilder = new StringBuilder();
		
		for(File file: folderContents) {
			strBuilder.append(file.getName());
			if(file.isDirectory()) {
				strBuilder.append(File.separator);
			}
			strBuilder.append(System.lineSeparator());
		}
		return strBuilder.toString();
	}

	/**
	 * Method to create FileFilter to ensure hidden files are not shown and additionally
	 * show directories only if isFolderOnly is true
	 * 
	 * @param isFoldersOnly
	 * 		true if -d option is provided in args, otherwise false
	 * @return FileFilter based on the option provided
	 */
	private FileFilter generateFileFilter(Boolean isFoldersOnly) {
		return new FileFilter() {
			@Override
			public boolean accept(File file) {
				boolean accepted = true;
				if(isFoldersOnly) {
					accepted = file.isDirectory();
				}
				accepted &= !file.isHidden();
				return accepted;
			}
		};
	}

}
