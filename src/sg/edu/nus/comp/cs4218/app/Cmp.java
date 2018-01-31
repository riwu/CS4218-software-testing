package sg.edu.nus.comp.cs4218.app;

import java.io.InputStream;

import sg.edu.nus.comp.cs4218.Application;

public interface Cmp extends Application {
	
	/**
	 * Returns a string reporting differing files. The report contains whether
	 * the files differ, the first byte in which they differ, and the corresponding
	 * line number. Returns an empty string if there are no difference.
	 * @param fileNameA String of file name of the first file to be compared
	 * @param fileNameB String of file name of the second file to be compared
	 * @param isPrintCharDiff Boolean option to print differing characters and 
	 * the differing octal values
	 * @param isPrintSimplify Boolean option to only print "Files differ" if
	 * the files are different 
	 * @param isPrintOctalDiff Boolean option to print the byte offset to the
	 * differing byte and the differing octal values. 
	 * @throws Exception
	 */
	public String cmpTwoFiles(String fileNameA, String fileNameB, Boolean isPrintCharDiff, Boolean isPrintSimplify, Boolean isPrintOctalDiff)
			throws Exception;
	
	/**
	 * Returns a string reporting differing inputs. The report contains whether
	 * the file differ form the Stdin arg, the first byte in which they differ, 
	 * and the corresponding line number. Returns an empty string if there are 
	 * no difference.
	 * @param fileName String of file name of a file to be compared
	 * @param stdin InputStream of Stdin arg to be compared
	 * @param isPrintCharDiff Boolean option to print differing characters and 
	 * the differing octal values
	 * @param isPrintSimplify Boolean option to only print "File differ" if
	 * the inputs are different 
	 * @param isPrintOctalDiff Boolean option to print the byte offset to the
	 * differing byte and the differing octal values. 
	 * @throws Exception
	 */
	public String cmpFileAndStdin(String fileName, InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify, Boolean isPrintOctalDiff)
			throws Exception;
	
	/**
	 * Returns a string reporting differing inputs. The report contains whether
	 * the Stdin args differ, the first byte in which they differ, and the 
	 * corresponding line number. Returns an empty string if there are no 
	 * difference.
	 * @param stdin InputStream of Stdin args to be compared
	 * @param isPrintCharDiff Boolean option to print differing characters and 
	 * the differing octal values
	 * @param isPrintSimplify Boolean option to only print "Input differ" if
	 * the inputs are different 
	 * @param isPrintOctalDiff Boolean option to print the byte offset to the
	 * differing byte and the differing octal values. 
	 * @throws Exception
	 */
	public String cmpStdin(InputStream stdin, Boolean isPrintCharDiff, Boolean isPrintSimplify, Boolean isPrintOctalDiff)
			throws Exception;

}
