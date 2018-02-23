package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.LsException;

public class LsApplicationTest {
	
	LsApplication lsApp = new LsApplication();
	private static final String TEST_FOLDER = "undertest";
	private static final String NAME_ONE = "One";
	private static final String NAME_TWO = "Two";
	private static Path testPath; // undertest
	private static Path pathOne; // undertest/One [folder]
	private static Path pathTwo; // undertest/Two [file]
	private static Path pathThree; // undertest/One/One [folder]
	private static Path pathFour; // undertest/One/Two [file]
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testPath = Paths.get(TEST_FOLDER);
		testPath.toFile().mkdir();
		pathOne = Paths.get(TEST_FOLDER, NAME_ONE);
		pathOne.toFile().mkdir();
		pathTwo = Paths.get(TEST_FOLDER, NAME_TWO);
		pathTwo.toFile().createNewFile();
		pathThree = Paths.get(TEST_FOLDER, NAME_ONE, NAME_ONE);
		pathThree.toFile().mkdir();
		pathFour = Paths.get(TEST_FOLDER, NAME_ONE, NAME_TWO);
		pathFour.toFile().createNewFile();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		deleteDirectory(testPath.toFile());
	}
	
	@Test
	public void Should_ListAllContents_When_ValidPathNoOptions() throws Exception {
		String path = testPath.toString();
		// undertest:
		// One\
		// Two
		String expected = path + ":\n" + NAME_ONE + File.separator + "\n" + NAME_TWO + "\n\n";
		assertEquals(expected, lsApp.listFolderContent(false, false, path));
	}
	
	@Test
	public void Should_ListEachFolderContents_When_MultipleValidPaths() throws Exception {
		String path = testPath.toString();
		String secondPath = pathThree.toString();
		// undertest:
		// One\
		// Two
		//
		// undertest\One\One:
		//
		String expected = path + ":\n" 
						+ NAME_ONE + File.separator + "\n" + NAME_TWO + "\n\n"
						+ secondPath + ":\n" + "\n";
		assertEquals(expected, lsApp.listFolderContent(false, false, path, secondPath));
	}
	
	@Test
	public void Should_ListFoldersOnly_When_ValidPathDirectoryOnly() throws Exception {
		String path = testPath.toString();
		// undertest:
		// One\
		String expected = path + ":\n" + NAME_ONE + File.separator + "\n\n";
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
		//
		String expected = path + ":\n" 
				+ NAME_ONE + File.separator + "\n" + NAME_TWO + "\n\n"
				+ pathOne + ":\n" 
				+ NAME_ONE + File.separator + "\n" + NAME_TWO + "\n\n"
				+ pathThree + ":\n"+ "\n";
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
		//
		String expected = path + ":\n" + NAME_ONE + File.separator + "\n\n" 
						+ pathOne + ":\n"+ NAME_ONE + File.separator + "\n\n"
						+ pathThree + ":\n"+ "\n";
		assertEquals(expected, lsApp.listFolderContent(true, true, path));
	}
	
	@Test(expected=LsException.class)
	public void Should_ThrowException_When_NotDirectoryPath() throws Exception {
		String path = pathTwo.toString();
		lsApp.run(new String[] {path}, System.in, System.out);
	}

	@Test(expected=LsException.class)
	public void Should_ThrowException_When_StdOutIsNull() throws Exception {
		lsApp.run(new String[] {}, System.in, null);	
	}
	
	private static boolean deleteDirectory(File directory) {
		File[] children = directory.listFiles();
		if(children != null && children.length != 0) {
			for(File child: children) {
				deleteDirectory(child);
			}
		}
		return directory.delete();
	}
}
