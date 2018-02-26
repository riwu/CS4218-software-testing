package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
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
	
	@Test
	public void shouldPrintSingleLineWhenFileIsSingleLine() throws Exception {
		assertEquals(TEXT_TWO, pasteApp.mergeFile(new String[] {filePathTwoS}));
	}
	
	@Test
	public void shouldPrintMultiLineWhenFileIsMultiLine() throws Exception {
		assertEquals(TEXT_ONE, pasteApp.mergeFile(new String[] {filePathOneS}));
	}
	
	@Test
	public void shouldPrintMultiLineWhenStdinMultiLine() throws Exception {
		InputStream inputStream = new FileInputStream(filePathOne.toFile());
		assertEquals(TEXT_ONE, pasteApp.mergeStdin(inputStream));
	}
	
	@Test
	public void shouldPrintSingleLineWhenStdinSingleLine() throws Exception {
		InputStream inputStream = new FileInputStream(filePathTwo.toFile());
		assertEquals(TEXT_TWO, pasteApp.mergeStdin(inputStream));
	}

	@Test
	public void shouldStartLineWithTabWhenFirstFileLessLinesThanSecond() throws Exception {
		String expected = "SingleLine\tLine1" + System.lineSeparator() + "\tLine2";
		assertEquals(expected, pasteApp.mergeFile(new String[] {filePathTwoS, filePathOneS}));
	}
	
	@Test
	public void shouldAddTabWhenFirstFileMoreLinesThanSecond() throws Exception {
		String expected = "Line1\tSingleLine" + System.lineSeparator() + "Line2\t";
		assertEquals(expected, pasteApp.mergeFile(new String[] {filePathOneS, filePathTwoS}));
	}
	
	@Test
	public void shouldPrintFileTwiceWhenBothFilesAreSame() throws Exception {
		String expected = "Line1\tLine1" + System.lineSeparator() + "Line2\tLine2";
		assertEquals(expected, pasteApp.mergeFile(new String[] {filePathOneS, filePathOneS}));
	}
	
	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenFileNotExist() throws Exception {	
		pasteApp.run(new String[] {"missing.txt"}, System.in, System.out);
	}
	
	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenFileIsDirectory() throws Exception {	
		Path temp = Files.createTempDirectory("temporary");
		temp.toFile().deleteOnExit();
		String tempFolder = temp.toString();
		pasteApp.run(new String[] {tempFolder}, System.in, System.out);
	}
	
	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenStdoutIsNull() throws Exception {
		pasteApp.run(new String[] {filePathOneS}, System.in, null);
	}

}
