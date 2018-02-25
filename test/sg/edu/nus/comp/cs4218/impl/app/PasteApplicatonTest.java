package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.PasteException;

public class PasteApplicatonTest {
	private static PasteApplication pasteApp;
	private static Path filePathOne;
	private static Path filePathTwo;
	private static final String TEXT_ONE = "Line1" + System.lineSeparator() + "Line2";
	private static final String TEXT_TWO = "SingleLine";
	private static String filePathOneS;
	private static String filePathTwoS;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pasteApp = new PasteApplication();
		filePathOne = Files.createTempFile("file1", ".txt");
		filePathOneS = filePathOne.toString();
		filePathTwo = Files.createTempFile("file2", null);
		filePathTwoS = filePathTwo.toString();
		OutputStream os1 = new FileOutputStream(filePathOne.toFile());
		OutputStream os2 = new FileOutputStream(filePathTwo.toFile());
		os1.write(TEXT_ONE.getBytes());
		os2.write(TEXT_TWO.getBytes());
		os1.close();
		os2.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		filePathOne.toFile().deleteOnExit();
		filePathTwo.toFile().deleteOnExit();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void Should_StartLineWithTab_When_FirstFileLessLinesThanSecond() throws Exception {
		String expected = "SingleLine\tLine1" + System.lineSeparator() + "\tLine2";
		assertEquals(expected, pasteApp.mergeFile(new String[] {filePathTwoS, filePathOneS}));
	}
	
	@Test
	public void Should_AddTab_When_FirstFileMoreLinesThanSecond() throws Exception {
		String expected = "Line1\tSingleLine" + System.lineSeparator() + "Line2\t";
		assertEquals(expected, pasteApp.mergeFile(new String[] {filePathOneS, filePathTwoS}));
	}
	
	@Test
	public void Should_PrintFileTwice_When_BothFilesAreSame() throws Exception {
		String expected = "Line1\tLine1" + System.lineSeparator() + "Line2\tLine2";
		assertEquals(expected, pasteApp.mergeFile(new String[] {filePathOneS, filePathOneS}));
	}
	
	@Test(expected=PasteException.class)
	public void Should_ThrowException_When_FileNotExist() throws Exception {	
		pasteApp.run(new String[] {"missing.txt"}, System.in, System.out);
	}
	
	@Test(expected=PasteException.class)
	public void Should_ThrowException_When_FileIsDirectory() throws Exception {	
		Path temp = Files.createTempDirectory("temporary");
		temp.toFile().deleteOnExit();
		String tempFolder = temp.toString();
		pasteApp.run(new String[] {tempFolder}, System.in, System.out);
	}
	
	@Test(expected=PasteException.class)
	public void Should_ThrowException_When_StdoutIsNull() throws Exception {
		pasteApp.run(new String[] {filePathOneS}, System.in, null);
	}

}
