package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.LsException;

public class LsApplicationTest {
	
	private static LsApplication lsApp;
	private static final Path basePath = Paths.get(System.getProperty("user.dir"));
	private static final String TEST_FOLDER = "undertest";
	private static final String FOLDER_NAME = "One";
	private static final String FILE_NAME = "Two";
	private static Path testPath; // undertest
	private static Path folderPath; // undertest/One [folder]
	private static Path filePath; // undertest/Two [file]
	private static Path folderPathNested; // undertest/One/One [folder]
	private static Path filePathNested; // undertest/One/Two [file]
	private static String folderPathName;
	private static String filePathName;
	private static String folderPathNestedName;
	private static String filePathNestedName;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lsApp = new LsApplication();
		testPath = Files.createTempDirectory(basePath, TEST_FOLDER);
		folderPath = Files.createTempDirectory(testPath, FOLDER_NAME);
		folderPathName = folderPath.getName(folderPath.getNameCount()-1).toString();
		filePath = Files.createTempFile(testPath, FILE_NAME, "");
		filePathName = filePath.getName(filePath.getNameCount()-1).toString();
		folderPathNested = Files.createTempDirectory(folderPath, FOLDER_NAME);
		folderPathNestedName = folderPathNested.getName(folderPathNested.getNameCount()-1).toString();
		filePathNested = Files.createTempFile(folderPath, FILE_NAME, "");
		filePathNestedName = filePathNested.getName(filePathNested.getNameCount()-1).toString();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testPath.toFile().deleteOnExit();
		folderPath.toFile().deleteOnExit();
		filePath.toFile().deleteOnExit();
		folderPathNested.toFile().deleteOnExit();
		filePathNested.toFile().deleteOnExit();
	}
	
	@Test
	public void Should_ListAllContents_When_ValidPathNoOptions() throws Exception {
		String path = testPath.toString();
		// One\
		// Two
		String expected = folderPathName + File.separator + "\n"
						+ filePathName;
		assertEquals(expected, lsApp.listFolderContent(false, false, path));
	}
	
	@Test
	public void Should_ListEachFolderContents_When_MultipleValidPaths() throws Exception {
		String path = testPath.toString();
		String secondPath = folderPathNested.toString();
		// undertest:
		// One\
		// Two
		//
		// undertest\One\One:
		String expected = path + ":" + "\n"
						+ folderPathName + File.separator + "\n"
						+ filePathName + "\n"
						+ "\n"
						+ secondPath + ":";
		assertEquals(expected, lsApp.listFolderContent(false, false, path, secondPath));
	}
	
	@Test
	public void Should_ListFoldersOnly_When_ValidPathDirectoryOnly() throws Exception {
		String path = testPath.toString();
		// One\
		String expected = folderPathName + File.separator;
		assertEquals(expected, lsApp.listFolderContent(true, false, path));
	}
	
	@Test
	public void Should_RecursiveListAllContents_When_ValidPathRecursiveOnly() throws Exception {
		String path = testPath.toString();
		// undertest:
		// One\
		// Two
		//
		// undertest\One:
		// One\
		// Two
		//
		// undertest\One\One:
		String expected = path + ":" + "\n" 
				+ folderPathName + File.separator + "\n"
				+ filePathName + "\n"
				+ "\n"
				+ folderPath + ":" + "\n"
				+ folderPathNestedName + File.separator + "\n"
				+ filePathNestedName + "\n"
				+ "\n"
				+ folderPathNested + ":";
		assertEquals(expected, lsApp.listFolderContent(false, true, path));
	}
	
	@Test
	public void Should_RecursiveListFoldersOnly_When_ValidPathBothOptions() throws Exception {
		String path = testPath.toString();
		// undertest:
		// One\
		//
		// undertest\One:
		// One\
		//
		// undertest\One\One:
		String expected = path + ":" + "\n"
						+ folderPathName + File.separator + "\n"
						+ "\n"
						+ folderPath + ":" + "\n"
						+ folderPathNestedName + File.separator + "\n"
						+ "\n"
						+ folderPathNested + ":";
		assertEquals(expected, lsApp.listFolderContent(true, true, path));
	}
	
	@Test(expected=LsException.class)
	public void Should_ThrowException_When_NotDirectoryPath() throws Exception {
		String path = filePath.toString();
		lsApp.run(new String[] {path}, System.in, System.out);
	}

	@Test(expected=LsException.class)
	public void Should_ThrowException_When_StdOutIsNull() throws Exception {
		lsApp.run(new String[] {}, System.in, null);	
	}
}
