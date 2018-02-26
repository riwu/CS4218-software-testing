package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.MkdirInterface;
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
public class MkdirApplication implements MkdirInterface {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (args == null || args.length == 0) {
			throw new MkdirException("No folders specified");
		}
		else {
			Path folderPath;
			String[] folderPathS = new String[args.length];
			Path currentDir = Paths.get(Environment.currentDirectory);
			for(int i = 0; i < args.length; i++) {
				try {
					folderPath = Paths.get(args[i]);
					if(folderPath.isAbsolute()) {
						folderPathS[i] = folderPath.toString();
					}
					else {
						//ensure it follows relative path based on the changes done by cd
						folderPathS[i] = currentDir.resolve(args[i]).toString();
					}
				} catch (Exception e) {
					throw new MkdirException(e.getMessage());
				}
			}
			try {
				createFolder(folderPathS);
			} catch (Exception e) {
				throw new MkdirException(e.getMessage());
			}
		}
		
	}

	@Override
	public void createFolder(String... folderName) throws Exception {
		boolean success;
		for (String folder: folderName) {
			File directory = new File(folder);
			success = directory.mkdirs();
			if (!directory.isDirectory() && !success) {
				String path = folder.replace(Environment.currentDirectory + File.separatorChar, "");
				throw new Exception(path + " is not a directory path");
			}
		}
	}

}
