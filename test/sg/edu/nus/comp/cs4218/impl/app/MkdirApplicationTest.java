package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.MkdirException;

public class MkdirApplicationTest {
	
	MkdirApplication mkdir = new MkdirApplication();
	private static final Path basePath = Paths.get(System.getProperty("user.dir"));
	private static final String TEST_FOLDER = "undertest";
	private static Path testPath;
	private static final String LEVEL_ONE = "Level1";
	private static final String LEVEL_TWO = "Level2";
	private static final String MULTI_LEVEL_ONE = "Level1/Level12";
	private static final String MULTI_LEVEL_TWO = "Level11/Level12";
	private static final String INVALID_PATH = "Level1/Level*2";
	ArrayList<String> paths;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testPath = basePath.resolve("undertest");
	}

	@Before
	public void setUp() throws Exception {
		testPath.toFile().mkdir();
	}

	@After
	public void tearDown() throws Exception {
		deleteDirectory(testPath.toFile());
	}

	@Test
	public void Should_Ignore_When_FolderAlreadyExists() throws Exception {
		Path folderPath = testPath.resolve(LEVEL_ONE);
		mkdir.createFolder(folderPath.toString(), folderPath.toString());
		assertTrue(folderPath.toFile().exists());
	}
	
	@Test
	public void Should_CreateFolder_When_SingleLevelFolderNotExists() throws Exception {
		Path folderPath = testPath.resolve(LEVEL_ONE);
		mkdir.createFolder(folderPath.toString());
		assertTrue(folderPath.toFile().exists());
	}
	
	@Test
	public void Should_CreateFolders_When_MultiLevelFolderNotExists() throws Exception {
		Path folderPath = testPath.resolve(MULTI_LEVEL_ONE);
		mkdir.createFolder(folderPath.toString());
		assertTrue(folderPath.toFile().exists());
	}
	
	@Test
	public void Should_CreateFolders_When_MultipleArgsExists() throws Exception {
		Path folderPathOne = testPath.resolve(LEVEL_ONE);
		Path folderPathTwo = testPath.resolve(MULTI_LEVEL_TWO);
		mkdir.createFolder(new String[] {folderPathOne.toString(), folderPathTwo.toString()});
		assertTrue(folderPathOne.toFile().exists());
		assertTrue(folderPathTwo.toFile().exists());
	}
	
	@Test(expected=MkdirException.class)
	public void Should_ThrowException_When_InvalidArgumentExists() throws Exception {
		mkdir.run(new String[] {INVALID_PATH}, System.in, System.out);
	}
	
	@Test(expected=MkdirException.class)
	public void Should_ThrowException_When_NoFoldersSpecified() throws Exception {
		mkdir.run(new String[] {}, System.in, System.out);		
	}
	
	@Test(expected=MkdirException.class)
	public void Should_ThrowException_When_InvalidInArgsExists() throws Exception {
		mkdir.run(new String[] {LEVEL_ONE, INVALID_PATH}, System.in, System.out);
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
