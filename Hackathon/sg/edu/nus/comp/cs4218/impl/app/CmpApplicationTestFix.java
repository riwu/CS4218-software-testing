package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CmpException;
import sg.edu.nus.comp.cs4218.impl.app.CmpApplication;

public class CmpApplicationTestFix {
	private static CmpApplication cmpApp;
	private static final Path BASE_PATH = Paths.get(Environment.currentDirectory);
	private static final String TEST_FOLDER = "undertest";
	private static final String TEXT_A = "This is\nthe texta";
	private static final String TEXT_B = "That is\nthe textb";
	private static Path testFolder;
	private static Path fileA;
	private static Path fileB;
	private static ByteArrayOutputStream baos;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cmpApp = new CmpApplication();
		testFolder = Files.createTempDirectory(BASE_PATH, TEST_FOLDER);
		baos = new ByteArrayOutputStream();
		fileA = Files.createTempFile(testFolder, "fileA", null);
		fileB = Files.createTempFile(testFolder, "fileB", null);
		Files.write(fileA, TEXT_A.getBytes());
		Files.write(fileB, TEXT_B.getBytes());
		fileA.toFile().deleteOnExit();
		fileB.toFile().deleteOnExit();
		testFolder.toFile().deleteOnExit();
	}

	@Test
	public void shouldShowLongFormatCharDiffWhenLongFormatCharDiffOnFilesOnly() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		String expected = "3 151 i 141 a" + System.lineSeparator() + 
				"4 163 s 164 t" + System.lineSeparator() + 
				"17 141 a 142 b";
		assertEquals(expected, cmpApp.cmpTwoFiles(fileNameA, fileNameB, true, false, true));
	}

	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenSimplifyWithLongFormat() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		cmpApp.run(new String[] {fileNameB, fileNameA, "-ls"}, null, baos);
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenSimplifyWithCharFormat() throws Exception {
		String fileNameA = fileA.toString();
		String fileNameB = fileB.toString();
		cmpApp.run(new String[] {fileNameB, fileNameA, "-cs"}, null, baos);
	}

}
