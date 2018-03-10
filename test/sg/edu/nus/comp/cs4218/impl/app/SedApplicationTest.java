package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.SedException;

public class SedApplicationTest {
	boolean isImplemented = false;
	private static SedApplication sedApp;
	String original = "This\tis\t\nthe\ttext\n";
	String replacedAll = "This is \nthe text\n";
	String replacedIndex = "This\twas\t\nthe\ttext\n";
	private static final String MISSING_S = "/\t/ /";
	private static final String INVALID_INDEX = "s/is/was/-2";
	Path filePath;
	Path folderPath;
	InputStream stdin;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sedApp = new SedApplication();
	}

	@Before
	public void setUp() throws Exception {
		filePath = Files.createTempFile("temp", null);
		OutputStream outStream = new FileOutputStream(filePath.toFile());
		outStream.write(original.getBytes());
		outStream.close();
		stdin = new FileInputStream(new File(filePath.toString()));
		folderPath = Files.createTempDirectory("sedapptest");
	}

	@After
	public void tearDown() throws Exception {
		stdin.close();
		folderPath.toFile().delete();
		filePath.toFile().delete();
	}

	@Test
	public void whenNothingToReplaceExpectNoChangeToContentStdin() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = ">";
		String replacement = "";
		int replacementIndex = 0;
		assertEquals(original, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}
	
	@Test
	public void whenNeverReachReplacementIndexExpectNoChangeToContentStdin() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 50;
		assertEquals(original, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexStdin() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "was";
		int replacementIndex = 2;
		assertEquals(replacedIndex, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}

	@Test
	public void shouldReplaceAllMatchesWhenIndexIsZeroStdin() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;
		assertEquals(replacedAll, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenIndexLessThanZeroStdin() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = -1;
		sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenStdinIsDirectory() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 0;
		InputStream dirStream = new FileInputStream(folderPath.toString());
		sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, dirStream);
	}
	
	@Test
	public void whenNothingToReplaceExpectNoChangeToContentFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = ">";
		String replacement = "";
		int replacementIndex = 0;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, filePath.toString()));
	}
	
	@Test
	public void whenNeverReachReplacementIndexExpectNoChangeToContentFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 50;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, filePath.toString()));
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "was";
		int replacementIndex = 2;
		assertEquals(replacedIndex, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, filePath.toString()));
	}

	@Test
	public void shouldReplaceAllMatchesWhenIndexIsZeroFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;
		assertEquals(replacedAll, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, filePath.toString()));
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenIndexLessThanZeroFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = -1;
		sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, filePath.toString());
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenFileNotExists() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 0;
		sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, "missing.txt");
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenFileIsDirectory() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 0;
		sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, folderPath.toString());
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissing() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = filePath.toString();
		sedApp.run(new String[] {path}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissingS() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = filePath.toString();
		sedApp.run(new String[] {path, MISSING_S}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleNegativeIndex() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = filePath.toString();
		sedApp.run(new String[] {path, INVALID_INDEX}, System.in, System.out);
	}
}
