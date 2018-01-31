package sg.edu.nus.comp.cs4218.app;

import java.io.IOException;
import java.io.InputStream;

import sg.edu.nus.comp.cs4218.Application;

public interface Split extends Application {
	
	/**
	 * Split a file into fixed size pieces with specified number of 
	 * lines. Output splits naming convention: prefix + counter.
	 * Default prefix is "x". Default counter is aa, ab, ..., zz, 
	 * zaa, zab, ..., zzz, zzaa, etc. For example: xaa, xab, etc.
	 * This is the default option for 'split'.
	 * @param fileName String of source file name
	 * @param prefix String of output file prefix (default is 'x')
	 * @param linesPerFile Int of lines to have in the output file
	 * (default is 1,000 lines)
	 * @throws Exception
	 */
	public void splitFileByLines(String fileName, String prefix, int linesPerFile) 
			throws Exception;
	
	/**
	 * Split a file into fixed size pieces with specified number of 
	 * lines. Output splits naming convention: prefix + counter.
	 * Default prefix is "x". Default counter is aa, ab, ..., zz, 
	 * zaa, zab, ..., zzz, zzaa, etc. For example: xaa, xab, etc.
	 * @param fileName String of source file name
	 * @param prefix String of output file prefix (default is 'x')
	 * @param bytesPerFile String of number of bytes of content to 
	 * fit into a file. Can have a suffix of either 'b', 'k', or 'm'.
	 * Impact of suffix:
	 * 'b' - multiply the bytes by 512
	 * 'k' - multiply the bytes by 1024
	 * 'm' - multiply the bytes by 1048576
	 * @throws Exception
	 */
	public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) 
			throws Exception;

}
