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
		String display = "";
		FileFilter directoryFilter = generateFileFilter(isFoldersOnly);
		
		if (isRecursive) {
			for(String name: folderName) {
				display += listFolderContentRecursive(directoryFilter, name);
			}
			return display;
		}
		
		for(String name: folderName) {
			File folder = new File(name);
			display += folder.getName() + ":\n";
			folderContents = folder.listFiles(directoryFilter);
			display += buildString(folderContents);
			display += "\n";
		}
		return display;
	}

	private String buildString(File[] folderContents) {
		String display = "";
		for(File file: folderContents) {
			if(!file.isHidden()) {
				display += file.getName();
				if(file.isDirectory()) {
					display += File.separator;
				}
				display += "\n";
			}
		}
		return display;
	}

	private String listFolderContentRecursive(FileFilter directoryFilter, String folderName) {
		File[] folderContents;
		String display = "";
		
		File folder = new File(folderName);
		if(folder.isDirectory()) {
			display += folderName + ":\n";
			folderContents = folder.listFiles(directoryFilter);
			display += buildString(folderContents);
			display += "\n";
			for(File file: folderContents) {
				String fileName = folderName + File.separator + file.getName();
				display += listFolderContentRecursive(directoryFilter, fileName);
			}
		}	
		
		return display;
	}

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
