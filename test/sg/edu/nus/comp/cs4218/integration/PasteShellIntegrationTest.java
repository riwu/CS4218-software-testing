package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PasteShellIntegrationTest {

	private final ShellImpl shell = new ShellImpl();
	private static final Path BASE_PATH = Paths.get(Environment.currentDirectory);
	private static Path filePathOne;
	private static Path filePathTwo;
	private static final String TEXT_ONE = "Line1" + System.lineSeparator() + "Line2" + System.lineSeparator();
	private static final String TEXT_TWO = "SingleLine" + System.lineSeparator();
	private static String filePathOneS;
	private static String filePathTwoS;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	
	@Before
	public void setUp() throws Exception {
		Environment.currentDirectory = BASE_PATH.toString();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		filePathOne.toFile().deleteOnExit();
		filePathTwo.toFile().deleteOnExit();
	}
	
	@Test
	public void shouldPrintSingleLineWhenFileIsSingleLine() throws Exception {
		String cmdline = "paste " + filePathTwoS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(TEXT_TWO, baos.toString());
	}

	@Test
	public void shouldPrintSingleLineWhenFileIsSingleLineRelativePath() throws Exception {
		Environment.currentDirectory = filePathTwo.getParent().toString();
		String cmdline = "paste " + filePathTwo.getFileName().toString();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(TEXT_TWO, baos.toString());
	}
	
	@Test
	public void shouldPrintMultiLineWhenFileIsMultiLine() throws Exception {
		String cmdline = "paste " + filePathOneS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(TEXT_ONE, baos.toString());
	}
	
	@Test
	public void shouldPrintSingleLineWhenStdinSingleLine() throws Exception {
		String cmdline = "paste < " + filePathTwoS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(TEXT_TWO, baos.toString());
	}

	
	@Test
	public void shouldPrintMultiLineWhenStdinMultiLine() throws Exception {
		String cmdline = "paste < " + filePathOneS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(TEXT_ONE, baos.toString());
	}
	
	@Test
	public void shouldStartLineWithTabWhenFirstFileLessLinesThanSecond() throws Exception {
		String expected = "SingleLine\tLine1" + System.lineSeparator() + "\tLine2" + System.lineSeparator();
		String cmdline = "paste " + filePathTwoS + " " + filePathOneS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldAddTabWhenFirstFileMoreLinesThanSecond() throws Exception {
		String expected = "Line1\tSingleLine" + System.lineSeparator() + "Line2\t" + System.lineSeparator();
		String cmdline = "paste " + filePathOneS + " " + filePathTwoS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldPrintFileTwiceWhenBothFilesAreSame() throws Exception {
		String expected = "Line1\tLine1" + System.lineSeparator() + "Line2\tLine2" + System.lineSeparator();
		String cmdline = "paste " + filePathOneS + " " + filePathOneS;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenFileNotExist() throws Exception {	
		String cmdline = "paste missing.txt";
		shell.parseAndEvaluate(cmdline, System.out);	
	}
	
	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenFileIsDirectory() throws Exception {	
		Path temp = Files.createTempDirectory("temporary");
		temp.toFile().deleteOnExit();
		String tempFolder = temp.toString();
		String cmdline = "paste " + tempFolder;
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldThrowExceptionWhenStdoutIsNull() throws Exception {
		String cmdline = "paste " + filePathOneS;
		shell.parseAndEvaluate(cmdline, null);
	}
}
