package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CmpException;

public class CmpApplicationTest {
	private static CmpApplication cmpApp;
	private static final Path BASE_PATH = Paths.get(Environment.currentDirectory);
	private static final String TEST_FOLDER = "undertest";
	private static final String TEXT_A = "This is\tthe texta";
	private static final String TEXT_B = "That is\nthe textb";
	private static Path testFolder;
	private static Path fileA;
	private static Path fileB;
	private static Path fileC;
	private static byte[] bytesA;
	private static byte[] bytesB;
	private static byte[] bytesC;
	private static ByteArrayOutputStream baos;
	private FileInputStream stdin;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cmpApp = new CmpApplication();
		testFolder = Files.createTempDirectory(BASE_PATH, TEST_FOLDER);
		baos = new ByteArrayOutputStream();
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
		baos.reset();
		Environment.currentDirectory = BASE_PATH.toString();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testFolder.toFile().deleteOnExit();
		baos.close();
	}
	
	@Test
	public void shouldReturnEmptyStringWhenFileStdinContentAreSame() throws Exception {
		String fileNameA = fileA.toString();
		stdin = new FileInputStream(fileC.toFile());
		assertEquals("", cmpApp.cmpFileAndStdin(fileNameA, stdin, false, false, false));
	}
	
	//General runs
	@Test
	public void shouldPrintEmptyStringWhenTwoFilesSameNoOptions() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameC = fileC.toString();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		cmpApp.run(new String[] {fileNameC, fileNameA}, null, baos);
		assertEquals("", baos.toString());
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
	public void shouldThrowExceptionWhenInvalidFlagFormat() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameC = fileC.toString();
		cmpApp.run(new String[] {fileNameC, fileNameA, "-s-s-s-s-s-s-s-s-s-"}, null, baos);
	}
}
