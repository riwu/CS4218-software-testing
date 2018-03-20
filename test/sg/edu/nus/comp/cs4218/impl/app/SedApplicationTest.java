package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;

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
	private String original = "This\tis testtest\nthe\ttest text test\n";
	private String replacedSecondIndex = "This\tis testt\nthe\ttest text t\n";
    private String replacedFirstIndex = "This\tis ttest\nthe\tt text test\n";
    private String deleteText = "This\tis test\nthe\t text test\n";
	private static final String MISSING_S = "/\t/ /";
	private static final String INVALID_INDEX = "s/is/was/-2";
	private static final String DIR_NAME = "sedTestDir";
	private static final String FILENAME1 = "sedFile1";
	private static final String FILENAME2 = "sedFile2";
	private File dir;
	private File file1;
	private File file2;
	private InputStream stdin;
	private OutputStream stdout;
    private String expected;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sedApp = new SedApplication();
	}

	@Before
	public void setUp() throws Exception {
		currentDir = Environment.currentDirectory;
		dir = new File(currentDir + File.separator + DIR_NAME);
		dir.mkdir();

		file1 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME1);
		Files.write(file1.toPath(), original.getBytes());
		file2 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME2);
		Files.write(file2.toPath(), original.getBytes());

		stdin = new ByteArrayInputStream(file1.toString().getBytes());
		stdout = new ByteArrayOutputStream();
	}

	@After
	public void tearDown() throws Exception {
		stdin.close();
		file1.delete();
		dir.delete();
	}

    @Test
    public void whenEmptyRegexExpectNoChangeStdin() throws Exception {
        String pattern = "";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(original, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

    @Test
    public void whenRegexMatchAddLeadBracketStdin() throws Exception {
        expected = "> This\tis testtest\n> the\ttest text test\n";
        String pattern = "^";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(expected, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
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
		String pattern = "test";
		String replacement = "t";
		int replacementIndex = 2;
		assertEquals(replacedSecondIndex, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}

    @Test
    public void whenEmptyReplacementExpectDeleteStdin() throws Exception {
        Assume.assumeTrue(isImplemented);
        String pattern = "test";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(deleteText, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
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
		InputStream dirStream = new FileInputStream(file1.toString());
		sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, dirStream);
	}

    @Test
    public void whenEmptyRegexExpectNoChangeFile() throws Exception {
        String pattern = "";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void whenRegexMatchExpectAddLeadBracketFile() throws Exception {
        expected = "> This\tis testtest\n> the\ttest text test\n";
        String pattern = "^";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(expected, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }
	
	@Test
	public void whenNothingToReplaceExpectNoChangeToContentFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = ">";
		String replacement = "";
		int replacementIndex = -1;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}
	
	@Test
	public void whenNeverReachReplacementIndexExpectNoChangeToContentFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 50;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "test";
		String replacement = "t";
		int replacementIndex = 2;
		assertEquals(replacedSecondIndex, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}

    @Test
    public void whenEmptyReplacementExpectDeleteFile() throws Exception {
        Assume.assumeTrue(isImplemented);
        String pattern = "test";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(deleteText, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

	@Test
	public void shouldNotReplaceWhenIndexIsZeroFile() throws Exception {
		Assume.assumeTrue(isImplemented);
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;
		assertEquals(original, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
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
		String path = file1.toString();
		sedApp.run(new String[] {path}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissingS() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = file1.toString();
		sedApp.run(new String[] {MISSING_S, path}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleNegativeIndex() throws Exception {
		Assume.assumeTrue(isImplemented);
		String path = file1.toString();
		sedApp.run(new String[] {INVALID_INDEX, path}, System.in, System.out);
	}

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenIndexLessThanZero() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/is/b/-1", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenNoStdInAndNoInputFile() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/test/t/"};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenInvalidSyntax() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/hello", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenStdoutMissing() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/test/t/", file1.toString()};
        sedApp.run(args, null, null);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenUseDelimiterInReplacement() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/test/t//", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenHave2InputFile() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/test/t/", file1.toString(), file2.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenEmptyArguments() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenInvalidReplaceIndex() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/test/t/??", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenInvalidCommandPosition() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {file1.toString(), "s/test/t/"};
        sedApp.run(args, null, stdout);
    }

    @Test
    public void whenMissingReplacementIndexExpectReplaceFirst() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s/test/t/", file1.toString()};
        sedApp.run(args, null, stdout);
        assertEquals(replacedFirstIndex, stdout.toString());
    }

    @Test
    public void whenMissingRegexExpectNoChange() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"s//t/", file1.toString()};
        sedApp.run(args, null, stdout);
        assertEquals(original, stdout.toString());
    }

    @Test
    public void whenOtherSeparatingCharExpectReplacement() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"sxtestxtx", file1.toString()};
        sedApp.run(args, null, stdout);
        assertEquals(replacedFirstIndex, stdout.toString());
    }

}
