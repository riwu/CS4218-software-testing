package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.MkdirException;

public class MkdirApplicationTest {
	
	static MkdirApplication mkdir;
	private static final Path BASE_PATH = Paths.get(Environment.currentDirectory);
	private static final String TEST_FOLDER = "undertest";
	private static Path testPath;
	private static final String LEVEL_ONE = "Level1";
	private static final String MULTI_LEVEL_ONE = "Level1" + File.separator + "Level12";
	private static final String MULTI_LEVEL_TWO = "Level11" + File.separator + "Level12";
	private static final String INVALID_PATH = "Level1" + File.separator + "Level*2";
	private static final boolean IS_WINDOWS = System.getProperty("os.name").contains("Wind");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testPath = BASE_PATH.resolve(TEST_FOLDER);
		mkdir = new MkdirApplication();
	}

	@Before
	public void setUp() throws Exception {
		testPath.toFile().mkdir();
	}

	@After
	public void tearDown() throws Exception {
		testPath.toFile().delete();
		File duplicate = testPath.resolve(LEVEL_ONE).toFile();
		if(duplicate.exists()) {
			duplicate.delete();
		}
	}

	@Test
	public void shouldIgnoreWhenFolderAlreadyExists() throws Exception {
		Path folderPath = testPath.resolve(LEVEL_ONE);
		mkdir.createFolder(folderPath.toString(), folderPath.toString());
		assertTrue(folderPath.toFile().isDirectory());
		folderPath.toFile().delete();
	}
	
	@Test
	public void shouldCreateFolderWhenSingleLevelFolderNotExists() throws Exception {
		Path folderPath = testPath.resolve(LEVEL_ONE);
		mkdir.createFolder(folderPath.toString());
		assertTrue(folderPath.toFile().isDirectory());
		folderPath.toFile().delete();
	}
	
	@Test
	public void shouldCreateFoldersWhenMultiLevelFolderNotExists() throws Exception {
		Path folderPath = testPath.resolve(MULTI_LEVEL_ONE);
		mkdir.createFolder(folderPath.toString());
		assertTrue(folderPath.toFile().isDirectory());
		folderPath.toFile().delete();
		folderPath.getParent().toFile().delete();
	}
	
	@Test
	public void shouldCreateFoldersWhenMultipleArgsExists() throws Exception {
		Path folderPathOne = testPath.resolve(LEVEL_ONE);
		Path folderPathTwo = testPath.resolve(MULTI_LEVEL_TWO);
		mkdir.createFolder(new String[] {folderPathOne.toString(), folderPathTwo.toString()});
		assertTrue(folderPathOne.toFile().isDirectory());
		assertTrue(folderPathTwo.toFile().isDirectory());
		folderPathOne.toFile().delete();
		folderPathTwo.toFile().delete();
		folderPathTwo.getParent().toFile().delete();
	}
	
	@Test
	public void shouldCreateFolderWhenValidAbsolutePath() throws Exception {
		String path = testPath.toString() + File.separator + LEVEL_ONE;
		mkdir.run(new String[] {path}, System.in, System.out);
		File folderPath = new File(path);
		assertTrue(folderPath.isDirectory());
		folderPath.delete();
	}
	
	@Test(expected=MkdirException.class)
	public void shouldThrowExceptionWhenInvalidDirectoryPath() throws Exception {
		String path = testPath.toString() + File.separator + LEVEL_ONE;
		File duplicate = new File(path);
		duplicate.createNewFile();
		mkdir.run(new String[] {path}, System.in, System.out);
	}
	
	@Test(expected=MkdirException.class)
	public void shouldThrowExceptionWhenInvalidCharacterExists() throws Exception {
		Assume.assumeTrue(IS_WINDOWS);
		mkdir.run(new String[] {INVALID_PATH}, System.in, System.out);
	}
	
	@Test(expected=MkdirException.class)
	public void shouldThrowExceptionWhenNoFoldersSpecified() throws Exception {
		mkdir.run(new String[] {}, System.in, System.out);		
	}
	
	@Test(expected=MkdirException.class)
	public void shouldThrowExceptionWhenInvalidInArgsExists() throws Exception {
		Assume.assumeTrue(IS_WINDOWS);
		mkdir.run(new String[] {LEVEL_ONE, INVALID_PATH}, System.in, System.out);
	}
}
