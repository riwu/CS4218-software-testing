package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.CmpException;
import sg.edu.nus.comp.cs4218.impl.CmpApplicationUtil;

public class CmpApplicationTest {
	private static CmpApplication cmpApp;
	private static final Path BASE_PATH = Paths.get(System.getProperty("user.dir"));
	private static final String TEST_FOLDER = "undertest";
	private static final String TEXT_A = "This is\tthe texta";
	private static final String TEXT_B = "That is\nthe textb";
	static Path testFolder;
	static Path fileA;
	static Path fileB;
	static Path fileC;
	static byte[] bytesA;
	static byte[] bytesB;
	static byte[] bytesC;
	private FileInputStream stdin;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cmpApp = new CmpApplication();
		testFolder = Files.createTempDirectory(BASE_PATH, TEST_FOLDER);
	}
	
	@Before
	public void setUp() throws Exception {
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
		bytesA = Files.readAllBytes(fileA);
		bytesB = Files.readAllBytes(fileB);
		bytesC = Files.readAllBytes(fileC);
	}
	
	@After
	public void tearDown() throws Exception {
		if(stdin != null) {
			stdin.close();
			stdin = null;
		}
		fileA.toFile().delete();
		fileB.toFile().delete();
		fileC.toFile().delete();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testFolder.toFile().deleteOnExit();
	}

	//Files Only
	@Test
	public void shouldShowFirstDiffWhenNoOptionsFilesOnly() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = CmpApplicationUtil.getNormalFormatString(fileNameA, fileNameB, 
							bytesA, bytesB, false);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, false, false, false));
	}
	
	@Test
	public void shouldShowSimplifiedWhenSimplifyOnFilesOnly() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = CmpApplicationUtil.getSimpleString(bytesA, bytesB);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, true, true, true));
	}
	
	@Test
	public void shouldShowDiffCharWhenDiffCharOnFilesOnly() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = CmpApplicationUtil.getNormalFormatString(fileNameA, fileNameB, 
							bytesA, bytesB, true);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, true, false, false));
	}

	@Test
	public void shouldShowLongFormatWhenLongFormatOnFilesOnly() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = CmpApplicationUtil.getLongFormatString(bytesA, bytesB, false);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, false, false, true));
	}
	
	@Test
	public void shouldShowLongFormatCharDiffWhenLongFormatCharDiffOnFilesOnly() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = CmpApplicationUtil.getLongFormatString(bytesA, bytesB, true);
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, true, false, true));
	}
	
	@Test
	public void shouldReturnEmptyStringWhenFileContentsAreSame() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameC = fileC.toString();
		assertEquals("", cmpApp.cmpTwoFiles(fileNameA, fileNameC, false, false, false));
	}
	
	//File and Stdin
	@Test
	public void shouldShowFirstDiffWhenNoOptionsFileStdin() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileB.toFile());
		String expected = CmpApplicationUtil.getNormalFormatString(fileNameA, 
							CmpApplicationUtil.STDIN, bytesA, bytesB, false);
		assertEquals(expected, cmpApp.cmpFileAndStdin(fileNameA, stdin, false, false, false));
	}
	
	@Test
	public void shouldShowSimplifiedWhenSimplifyOnFileStdin() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileB.toFile());
		String expected = CmpApplicationUtil.getSimpleString(bytesA, bytesB);
		assertEquals(expected, cmpApp.cmpFileAndStdin(fileNameA, stdin, true, true, true));
	}
	
	@Test
	public void shouldShowDiffCharWhenDiffCharOnFileStdin() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileB.toFile());
		String expected = CmpApplicationUtil.getNormalFormatString(fileNameA, 
							CmpApplicationUtil.STDIN, bytesA, bytesB, true);
		assertEquals(expected, cmpApp.cmpFileAndStdin(fileNameA, stdin, true, false, false));
	}

	@Test
	public void shouldShowLongFormatWhenLongFormatOnFileStdin() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileB.toFile());
		String expected = CmpApplicationUtil.getLongFormatString(bytesA, bytesB, false);
		assertEquals(expected, cmpApp.cmpFileAndStdin(fileNameA, stdin, false, false, true));
	}
	
	@Test
	public void shouldShowLongFormatCharDiffWhenLongFormatCharDiffOnFileStdin() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileB.toFile());
		String expected = CmpApplicationUtil.getLongFormatString(bytesA, bytesB, true);
		assertEquals(expected, cmpApp.cmpFileAndStdin(fileNameA, stdin, true, false, true));
	}
	
	@Test
	public void shouldReturnEmptyStringWhenFileStdinContentAreSame() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileC.toFile());
		assertEquals("", cmpApp.cmpFileAndStdin(fileNameA, stdin, false, false, false));
	}
		
	//General application violations
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenMoreThanTwoFiles() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String fileNameC = fileC.toString();
		cmpApp.run(new String[] {fileNameB, fileNameC, fileNameA}, null, System.out);
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenOneArgOnly() throws Exception {
		String fileNameB = fileB.toString();
		stdin = new FileInputStream(fileC.toFile());
		cmpApp.run(new String[] {fileNameB}, stdin, System.out);
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenFileNotExist() throws Exception {
		String fileNameB = fileB.toString();
		cmpApp.run(new String[] {fileNameB, "missing.txt"}, null, System.out);
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenStdinNotExist() throws Exception {
		String fileNameB = fileB.toString();
		cmpApp.run(new String[] {fileNameB, CmpApplicationUtil.STDIN}, null, System.out);
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenStdoutNotExist() throws Exception {
		String fileNameB = fileB.toString();
		stdin = new FileInputStream(fileC.toFile());
		cmpApp.run(new String[] {fileNameB, CmpApplicationUtil.STDIN}, stdin, null);
	}
	
}
