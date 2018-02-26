package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.CmpException;

public class CmpApplicationTest {
	boolean isImplemented = false;
	private static CmpApplication cmpApp;
	private static final Path BASE_PATH = Paths.get(System.getProperty("user.dir"));
	private static final String TEST_FOLDER = "undertest";
	private static final String TEXT_A = "This is\tthe text";
	private static final String TEXT_B = "That is\nthe text";
	private static final String SIMPLE = "Files differ";
	private static final String NORMAL_FORMAT = "1$s 2$s differ: char 3$s, line 4$s5$s";
	private static final String LONG_FORMAT = "1$s 2$s 3$s"; //byte# octal1# octal2#
	private static final String IS_FORMAT = " is 1$s 2$s";
	private static final String CHAR_FORMAT = "1$s 2$s"; //octal# value#
	static Path testFolder;
	static Path fileA;
	static Path fileB;
	static Path fileC;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cmpApp = new CmpApplication();
		testFolder = Files.createTempDirectory(BASE_PATH, TEST_FOLDER);
		fileA = Files.createTempFile(testFolder, "fileA", null);
		fileB = Files.createTempFile(testFolder, "fileB", null);
		fileC = Files.createTempFile(testFolder, "fileC", null);
		OutputStream outStream = new FileOutputStream(fileA.toFile());
		outStream.write(TEXT_A.getBytes());
		outStream.close();
		outStream = new FileOutputStream(fileB.toFile());
		outStream.write(TEXT_B.getBytes());
		outStream.close();
		outStream = new FileOutputStream(fileC.toFile());
		outStream.write(TEXT_A.getBytes());
		outStream.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		fileA.toFile().delete();
		fileB.toFile().delete();
		fileC.toFile().delete();
		testFolder.toFile().delete();
	}

	@Test
	public void Should_ShowFirstDiff_When_NoOptionsFilesOnly() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = String.format(NORMAL_FORMAT, fileNameA, fileNameB, 3, 1);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, false, false, false));
	}
	
	@Test
	public void Should_ShowSimplified_When_SimplifyFiles() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		assertEquals(SIMPLE, cmpApp.cmpTwoFiles(fileNameA, fileNameB, true, true, true));
	}
	
	@Test
	public void Should_ShowDiffChar_When_DiffCharFilesOnly() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String appendString = getIsFormatString(TEXT_A.getBytes()[2], TEXT_B.getBytes()[2]);
		String expected = String.format(NORMAL_FORMAT, fileNameA, fileNameB, 3, 1, appendString);
		assertEquals(SIMPLE, cmpApp.cmpTwoFiles(fileNameA, fileNameB, false, true, false));
	}
	
	private String getIsFormatString(byte b, byte c) {
		return String.format(IS_FORMAT, getOctalCharString(b), getOctalCharString(c));
	}

	@Test
	public void Should_ShowLongFormat_When_LongFormatFilesOnly() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = getLongFormatString(TEXT_A, TEXT_B, false);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, false, false, true));
	}
	
	@Test
	public void Should_ShowLongFormatCharDiff_When_LongFormatCharDiffOptionsFilesOnly() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = getLongFormatString(TEXT_A, TEXT_B, true);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, false, false, true));
	}
	
	@Test
	public void Should_ReturnEmptyString_When_FileContentsAreSame() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameA = fileA.toString();
		String fileNameC = fileC.toString();
		assertEquals("", cmpApp.cmpTwoFiles(fileNameA, fileNameC, false, false, false));
	}
	
	@Test(expected=CmpException.class)
	public void Should_ThrowException_When_FileNotExist() throws Exception {
		Assume.assumeTrue(isImplemented);
		String fileNameB = fileB.toString();
		cmpApp.cmpTwoFiles("missing.txt" , fileNameB, false, false, false);
	}
	
	
	//Utility methods
	private String getOctalCharString(byte single) {
		return String.format(CHAR_FORMAT, toOctal(single), Byte.toString(single));
	}
	
	private String getLongFormatString(String textA, String textB, boolean isCharDiff) {
		byte[] bytesA = textA.getBytes();
		byte[] bytesB = textB.getBytes();
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i < bytesA.length && i < bytesB.length; i++) {
			if(bytesA[i] != bytesB[i]) {
				if(strBuilder.length() > 0) {
					strBuilder.append(System.lineSeparator());
				}
				String diffA = "";
				String diffB = "";
				if(isCharDiff) {
					diffA = getOctalCharString(bytesA[i]);
					diffB = getOctalCharString(bytesB[i]);
				}
				else {
					diffA = toOctal(bytesA[i]);
					diffB = toOctal(bytesB[i]);
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
