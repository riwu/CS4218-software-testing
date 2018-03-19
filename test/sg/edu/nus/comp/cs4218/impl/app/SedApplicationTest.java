package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SedException;

public class SedApplicationTest {
	boolean isImplemented = true;
	private static SedApplication sedApp;
	private String currentDir;
	private String original = "This\tis\t\nthe\ttext\n";
	private String replacedAll = "This is \nthe text\n";
	private String replacedIndex = "This\twas\t\nthe\ttext\n";
	private static final String MISSING_S = "/\t/ /";
	private static final String INVALID_INDEX = "s/is/was/-2";
	private static final String DIR_NAME = "sedTestDir";
	private static final String FILENAME = "sedFile";
	private File dir;
	private File file;
	private InputStream stdin;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sedApp = new SedApplication();
	}

	@Before
	public void setUp() throws Exception {
		currentDir = Environment.currentDirectory;
		dir = new File(currentDir + File.separator + DIR_NAME);
		dir.mkdir();

		file = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME);
		Files.write(file.toPath(), original.getBytes());

		stdin = new ByteArrayInputStream(file.toString().getBytes());
	}

	@After
	public void tearDown() throws Exception {
		stdin.close();
		file.delete();
		dir.delete();
	}


//	@Test(expected=SedException.class)
//	public void shouldThrowExceptionWhenIndexLessThanZeroStdin() throws Exception {
//		Assume.assumeTrue(isImplemented);
//		String pattern = "is";
//		String replacement = "";
//		int replacementIndex = -1;
//		sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin);
//	}

//    @Test(expected=SedException.class)
//    public void shouldThrowExceptionWhenIndexLessThanZeroFile() throws Exception {
//        Assume.assumeTrue(isImplemented);
//        String pattern = "is";
//        String replacement = "";
//        int replacementIndex = -1;
//        sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file.toString());
//    }

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
	public void shouldNotReplaceWhenIndexIsZeroStdin() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;

        assertEquals(original, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}

	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenStdinIsDirectory() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 0;
		InputStream dirStream = new FileInputStream(file.toString());
		sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, dirStream);
	}
	
	@Test
	public void whenNothingToReplaceExpectNoChangeToContentFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = ">";
		String replacement = "";
		int replacementIndex = 0;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file.toString()));
	}
	
	@Test
	public void whenNeverReachReplacementIndexExpectNoChangeToContentFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 50;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file.toString()));
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "was";
		int replacementIndex = 2;
		assertEquals(replacedIndex, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file.toString()));
	}

	@Test
	public void shouldNotReplaceWhenIndexIsZeroFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file.toString()));
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
		sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, dir.toString());
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissing() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = file.toString();
		sedApp.run(new String[] {path}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissingS() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = file.toString();
		sedApp.run(new String[] {path, MISSING_S}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleNegativeIndex() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = file.toString();
		sedApp.run(new String[] {path, INVALID_INDEX}, System.in, System.out);
	}
}
