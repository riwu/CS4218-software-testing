package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.Ls;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.LsException;

/**
 * The ls command lists the contents of the given folder(s) and prints on the
 * standard output.
 * 
 * <p>
 * <b>Command format:</b> <code>ls [-d] [FOLDER...] [-R]</code>
 * <dl>
 * <dt>FOLDER</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * <dt>-d</dt>
 * <dd>Option to list directories only.</dd>
 * <dt>-R</dt>
 * <dd>Option to list files and sub-folders recursively.</dd>
 * </dl>
 * </p>
 */
public class LsApplication implements Ls {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (stdin == null || stdout == null) {
			throw new LsException("Null Pointer Exception");
		}
		boolean isFoldersOnly = false;
		boolean isRecursive = false;
		int optionCount = 0;
		ArrayList<String> folders = new ArrayList<String>();
		
		for(String arg: args) {
			if(arg.equals("-d")) {
				isFoldersOnly = true;
				optionCount++;
			}
			else if(arg.equals("-R")) {
				isRecursive = true;
				optionCount++;
			}
			else {
				File folder = new File(arg);
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
				folders.add(Environment.currentDirectory);
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
			for(String name: folderName) {
				File folder = new File(name);
				folderContents = folder.listFiles(directoryFilter);
				strBuilder.append(folderContentString(name, folderContents));
			}
		}
		return strBuilder.toString();
	}
	
	private String buildString(File... folderContents) {
		StringBuilder strBuilder = new StringBuilder();
		for(File file: folderContents) {
			if(!file.isHidden()) {
				strBuilder.append(file.getName());
				if(file.isDirectory()) {
					strBuilder.append(File.separator);
				}
				strBuilder.append('\n');
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
		if(folder.isDirectory()) {
			folderContents = folder.listFiles(directoryFilter);
			strBuilder.append(folderContentString(folderName, folderContents));
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
	 * @param folderName
	 * 		Name of the folder
	 * @param folderContents
	 * 		List of folder contents to display in String
	 * @return Formatted String showing the folder and its contents
	 */
	private String folderContentString(String folderName, File... folderContents) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(folderName).append(":\n")
				.append(buildString(folderContents)).append('\n');
		
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
