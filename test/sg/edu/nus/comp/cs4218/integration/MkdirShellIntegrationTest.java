package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.MkdirException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MkdirShellIntegrationTest {
	private final ShellImpl shell = new ShellImpl();
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
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testPath.toFile().deleteOnExit();
	}

	@Before
	public void setUp() throws Exception {
		Environment.currentDirectory = testPath.toString();
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
	public void shouldCreateDirectoryWhenNotExists() throws Exception {
		String cmdline = "mkdir " + LEVEL_ONE;
		Path folder = testPath.resolve(LEVEL_ONE);
		assertTrue(!folder.toFile().exists());
		shell.parseAndEvaluate(cmdline, System.out);
		assertTrue(folder.toFile().exists());
		folder.toFile().delete();
	}
	
	@Test
	public void shouldIgnoreWhenDirectoryExists() throws Exception {
		String cmdline = "mkdir " + LEVEL_ONE;
		Path folder = testPath.resolve(LEVEL_ONE);
		shell.parseAndEvaluate(cmdline, System.out);
		assertTrue(folder.toFile().exists());
		folder.toFile().delete();
	}
	
	@Test
	public void shouldCreateFoldersWhenMultiLevelFolderNotExists() throws Exception {
		String cmdline = "mkdir " + MULTI_LEVEL_ONE;
		Path folderPath = testPath.resolve(MULTI_LEVEL_ONE);
		shell.parseAndEvaluate(cmdline, System.out);
		assertTrue(folderPath.toFile().isDirectory());
		folderPath.toFile().delete();
		folderPath.getParent().toFile().delete();
	}
	
	@Test
	public void shouldCreateFoldersWhenMultipleArgsExists() throws Exception {
		String cmdline = "mkdir " + LEVEL_ONE + " " + MULTI_LEVEL_TWO;
		Path folderPathOne = testPath.resolve(LEVEL_ONE);
		Path folderPathTwo = testPath.resolve(MULTI_LEVEL_TWO);
		shell.parseAndEvaluate(cmdline, System.out);
		assertTrue(folderPathOne.toFile().isDirectory());
		assertTrue(folderPathTwo.toFile().isDirectory());
		folderPathOne.toFile().delete();
		folderPathTwo.toFile().delete();
		folderPathTwo.getParent().toFile().delete();
	}
	
	@Test
	public void shouldCreateFolderWhenValidAbsolutePath() throws Exception {
		Path folder = testPath.resolve(LEVEL_ONE);
		assertTrue(folder.isAbsolute());
		String cmdline = "mkdir " + folder.toString();
		shell.parseAndEvaluate(cmdline, System.out);
		assertTrue(folder.toFile().isDirectory());
		folder.toFile().delete();
	}
	
	@Test(expected=MkdirException.class)
	public void shouldThrowExceptionWhenInvalidDirectoryPath() throws Exception {
		String cmdline = "mkdir " + LEVEL_ONE;
		Path path = testPath.resolve(LEVEL_ONE);
		path.toFile().createNewFile();
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=InvalidPathException.class)
	public void shouldThrowExceptionWhenInvalidCharacterExists() throws Exception {
		Assume.assumeTrue(IS_WINDOWS);
		String cmdline = "mkdir " + INVALID_PATH;
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=MkdirException.class)
	public void shouldThrowExceptionWhenNoFoldersSpecified() throws Exception {
		String cmdline = "mkdir";
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=InvalidPathException.class)
	public void shouldThrowExceptionWhenInvalidInArgsExists() throws Exception {
		Assume.assumeTrue(IS_WINDOWS);
		String cmdline = "mkdir " + LEVEL_ONE + " " + INVALID_PATH;
		shell.parseAndEvaluate(cmdline, System.out);
	}
}
