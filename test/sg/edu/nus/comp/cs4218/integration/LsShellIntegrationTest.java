package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class LsShellIntegrationTest {
	
	private final ShellImpl shell = new ShellImpl();
	private static final Path BASE_PATH = Paths.get(Environment.currentDirectory);
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
		testPath = Files.createTempDirectory(BASE_PATH, TEST_FOLDER);
		folderPath = Files.createTempDirectory(testPath, FOLDER_NAME);
		filePath = Files.createTempFile(testPath, FILE_NAME, "");
		folderPathNested = Files.createTempDirectory(folderPath, FOLDER_NAME);
		filePathNested = Files.createTempFile(folderPath, FILE_NAME, "");
		testPath.toFile().deleteOnExit();
		folderPath.toFile().deleteOnExit();
		filePath.toFile().deleteOnExit();
		folderPathNested.toFile().deleteOnExit();
		filePathNested.toFile().deleteOnExit();
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = BASE_PATH.toString();
	}
	

	@Test
	public void shouldListAllContentsWhenValidAbsolutePathNoOptions() throws Exception {
		String expected = getContentString(testPath, false) + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String cmdline = "ls " + testPath.toString();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldListAllContentsWhenCurrentDirectoryNoOptions() throws Exception {
		Environment.currentDirectory = testPath.toString();
		String expected = getContentString(testPath, false) + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String cmdline = "ls .";
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldShowCurrentDirectoryWhenNoArgs() throws Exception {
		Environment.currentDirectory = testPath.toString();
		String expected = getContentString(testPath, false) + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String cmdline = "ls";
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldListAllContentsWhenRelativePathNoOptions() throws Exception {
		Environment.currentDirectory = folderPath.toString();
		String expected = getContentString(testPath, false) + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String cmdline = "ls ..";
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldListEachFolderContentsWhenMultipleValidPaths() throws Exception {
		String path = testPath.toString();
		String secondPath = folderPathNested.toString();
		String cmdline = "ls " + path + " " + secondPath;
		String expected = path + ":" + System.lineSeparator()
						+ getContentString(testPath, false) + System.lineSeparator()
						+ System.lineSeparator()
						+ secondPath + ":" + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldListFoldersOnlyWhenValidPathDirectoryOnly() throws Exception {
		String path = testPath.toString();
		String cmdline = "ls -d " + path;
		String expected = getContentString(testPath, true) + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldRecursiveListAllContentsWhenValidPathRecursiveOnly() throws Exception {
		String path = testPath.toString();
		String cmdline = "ls -R " + path;
		String expected = path + ":" + System.lineSeparator() 
				+ getContentString(testPath, false) + System.lineSeparator()
				+ System.lineSeparator()
				+ folderPath + ":" + System.lineSeparator()
				+ getContentString(folderPath, false) + System.lineSeparator()
				+ System.lineSeparator()
				+ folderPathNested + ":" + System.lineSeparator() + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldRecursiveListFoldersOnlyWhenValidPathBothOptions() throws Exception {
		String path = testPath.toString();
		String cmdline = "ls -d -R " + path;
		String expected = path + ":" + System.lineSeparator()
						+ getContentString(testPath, true) + System.lineSeparator()
						+ System.lineSeparator()
						+ folderPath + ":" + System.lineSeparator()
						+ getContentString(folderPath, true) + System.lineSeparator()
						+ System.lineSeparator()
						+ folderPathNested + ":" + System.lineSeparator() + System.lineSeparator();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdline, baos);
		assertEquals(expected, baos.toString());
	}
	
	@Test(expected=LsException.class)
	public void shouldThrowExceptionWhenNotDirectoryPath() throws Exception {
		String path = filePath.toString();
		String cmdline = "ls " + path;
		shell.parseAndEvaluate(cmdline, System.out);
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
