package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.Mkdir;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MkdirException;

/**
 * The mkdir command creates folders 
 * 
 * 
 * <p>
 * <b>Command format:</b> <code>mkdir FOLDERS...</code>
 * <dl>
 * <dt>FOLDERS</dt>
 * <dd>the name of the folder(s) to be created.</dd>
 * </dl>
 * </p>
 */
public class MkdirApplication implements Mkdir {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (args == null || args.length == 0) {
			throw new MkdirException("No folders specified");
		}
		else if (stdin == System.in && stdout == System.out) {
			Path folderPath;
			String[] folderPathS = new String[args.length];
			Path currentDir = Paths.get(Environment.currentDirectory);
			for(int i = 0; i < args.length; i++) {
				try {
					folderPath = currentDir.resolve(args[i]);
					folderPathS[i] = folderPath.toString();
				} catch (Exception e) {
					throw new MkdirException("Invalid characters found in folder name");
				}
			}
			createFolder(folderPathS);
		}
		else {
			throw new MkdirException("I/O Redirection not allowed");
		}
		
	}

	@Override
	public void createFolder(String... folderName) throws MkdirException {
		for (String folder: folderName) {
			File directory = new File(folder);
			directory.mkdirs();
		}
	}

}
