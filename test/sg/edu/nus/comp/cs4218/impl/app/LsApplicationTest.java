package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
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
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lsApp = new LsApplication();
		testPath = Files.createTempDirectory(basePath, TEST_FOLDER);
		folderPath = Files.createTempDirectory(testPath, FOLDER_NAME);
		filePath = Files.createTempFile(testPath, FILE_NAME, "");
		folderPathNested = Files.createTempDirectory(folderPath, FOLDER_NAME);
		filePathNested = Files.createTempFile(folderPath, FILE_NAME, "");
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
		String expected = getContentString(testPath, false) + System.lineSeparator();
		assertEquals(expected, lsApp.listFolderContent(false, false, path));
	}
	
	@Test
	public void Should_ListEachFolderContents_When_MultipleValidPaths() throws Exception {
		String path = testPath.toString();
		String secondPath = folderPathNested.toString();
		String expected = path + ":" + System.lineSeparator()
						+ getContentString(testPath, false) + System.lineSeparator()
						+ System.lineSeparator()
						+ secondPath + ":" + System.lineSeparator();
		assertEquals(expected, lsApp.listFolderContent(false, false, path, secondPath));
	}
	
	@Test
	public void Should_ListFoldersOnly_When_ValidPathDirectoryOnly() throws Exception {
		String path = testPath.toString();
		String expected = getContentString(testPath, true) + System.lineSeparator();;
		assertEquals(expected, lsApp.listFolderContent(true, false, path));
	}
	
	@Test
	public void Should_RecursiveListAllContents_When_ValidPathRecursiveOnly() throws Exception {
		String path = testPath.toString();
		String expected = path + ":" + System.lineSeparator() 
				+ getContentString(testPath, false) + System.lineSeparator()
				+ System.lineSeparator()
				+ folderPath + ":" + System.lineSeparator()
				+ getContentString(folderPath, false) + System.lineSeparator()
				+ System.lineSeparator()
				+ folderPathNested + ":" + System.lineSeparator();
		assertEquals(expected, lsApp.listFolderContent(false, true, path));
	}
	
	@Test
	public void Should_RecursiveListFoldersOnly_When_ValidPathBothOptions() throws Exception {
		String path = testPath.toString();
		String expected = path + ":" + System.lineSeparator()
						+ getContentString(testPath, true) + System.lineSeparator()
						+ System.lineSeparator()
						+ folderPath + ":" + System.lineSeparator()
						+ getContentString(folderPath, true) + System.lineSeparator()
						+ System.lineSeparator()
						+ folderPathNested + ":" + System.lineSeparator();
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
	
	private String getContentString(Path path, boolean isDirectoryOnly) {
		FileFilter filter = generateFileFilter(isDirectoryOnly);
		File[] contents = path.toFile().listFiles(filter);
		String expected = "";
		int limit = contents.length - 1;
		for(int i=0; i<contents.length; i++) {
			expected += contents[i].getName();
			if(contents[i].isDirectory()) {
				expected += File.separator;
			}
			if(i < limit) {
				expected += System.lineSeparator();
			}
		}
		return expected;
		
	}
	
	private FileFilter generateFileFilter(Boolean isFoldersOnly) {
		return new FileFilter() {
			@Override
			public boolean accept(File file) {
				boolean accepted = true;
				if(isFoldersOnly) {
					accepted = file.isDirectory();
				}
				accepted &= !file.isHidden();
				return accepted;
			}
		};
	}
}
