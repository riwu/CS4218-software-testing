package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SedException;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SedApplicationTest {
	private static SedApplication sedApp;
	private static final String ORIGINAL = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator();
	private static final String REPLACED_SECOND = "This\tis testt" + System.lineSeparator() + "the\ttest text t" + System.lineSeparator();
    private static final String REPLACED_FIRST = "This\tis ttest" + System.lineSeparator() + "the\tt text test" + System.lineSeparator();
    private static final String DELETE_TEXT = "This\tis test" + System.lineSeparator() + "the\t text test" + System.lineSeparator();
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
		String currentDir = Environment.currentDirectory;
		dir = new File(currentDir + File.separator + DIR_NAME);
		dir.mkdir();

		file1 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME1);
		Files.write(file1.toPath(), ORIGINAL.getBytes());
		file2 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME2);
		Files.write(file2.toPath(), ORIGINAL.getBytes());

		stdin = new ByteArrayInputStream(ORIGINAL.getBytes());
		stdout = new ByteArrayOutputStream();
	}

	@After
	public void tearDown() throws Exception {
		stdin.close();
		file1.delete();
		file2.delete();
		dir.delete();
		stdin.close();
		stdout.close();
	}

    @Test
    public void whenEmptyRegexExpectNoChangeStdin() throws Exception {
        String pattern = "";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(ORIGINAL, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

    @Test
    public void whenRegexMatchAddLeadBracketStdin() throws Exception {
        expected = "> This\tis testtest" + System.lineSeparator() + "> the\ttest text test" + System.lineSeparator();
        String pattern = "^";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(expected, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

	@Test
	public void whenNothingToReplaceExpectNoChangeToContentStdin() throws Exception {
		String pattern = ">";
		String replacement = "";
		int replacementIndex = 0;
		assertEquals(ORIGINAL, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}
	
	@Test
	public void whenNeverReachReplacementIndexExpectNoChangeToContentStdin() throws Exception {
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 50;
		assertEquals(ORIGINAL, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexStdin() throws Exception {
		String pattern = "test";
		String replacement = "t";
		int replacementIndex = 2;
		assertEquals(REPLACED_SECOND, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}

    @Test
    public void whenEmptyReplacementExpectDeleteStdin() throws Exception {
        String pattern = "test";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(DELETE_TEXT, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

	@Test
	public void shouldNotReplaceWhenIndexIsZeroStdin() throws Exception {
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;

        assertEquals(ORIGINAL, sedApp.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
	}

    @Test
    public void whenEmptyRegexExpectNoChangeFile() throws Exception {
        String pattern = "";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(ORIGINAL, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void whenRegexMatchExpectAddLeadBracketFile() throws Exception {
        expected = "> This\tis testtest" + System.lineSeparator() + "> the\ttest text test" + System.lineSeparator();
        String pattern = "^";
        String replacement = "> ";
        int replacementIndex = -1;
        assertEquals(expected, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }
	
	@Test
	public void whenNothingToReplaceExpectNoChangeToContentFile() throws Exception {
		String pattern = ">";
		String replacement = "";
		int replacementIndex = -1;
		assertEquals(ORIGINAL, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}
	
	@Test
	public void whenNeverReachReplacementIndexExpectNoChangeToContentFile() throws Exception {
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 50;
		assertEquals(ORIGINAL, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexFile() throws Exception {
		String pattern = "test";
		String replacement = "t";
		int replacementIndex = 2;
		assertEquals(REPLACED_SECOND, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}

    @Test
    public void whenEmptyReplacementExpectDeleteFile() throws Exception {
        String pattern = "test";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(DELETE_TEXT, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

	@Test
	public void shouldNotReplaceWhenIndexIsZeroFile() throws Exception {
		String pattern = "\t";
		String replacement = " ";
		int replacementIndex = 0;
		assertEquals(ORIGINAL, sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
	}
	
	@Test(expected=Exception.class)
	public void shouldThrowExceptionWhenFileNotExists() throws Exception {
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 0;
		sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, "missing.txt");
	}
	
	@Test(expected=Exception.class)
	public void shouldThrowExceptionWhenFileIsDirectory() throws Exception {
		String pattern = "is";
		String replacement = "";
		int replacementIndex = 0;
		sedApp.replaceSubstringInFile(pattern, replacement, replacementIndex, dir.toString());
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissing() throws Exception {
		String path = file1.toString();
		sedApp.run(new String[] {path}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleMissingS() throws Exception {
		String path = file1.toString();
		sedApp.run(new String[] {MISSING_S, path}, System.in, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenReplacementRuleNegativeIndex() throws Exception {
		String path = file1.toString();
		sedApp.run(new String[] {INVALID_INDEX, path}, System.in, System.out);
	}

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenIndexLessThanZero() throws Exception {
        String[] args = {"s/is/b/-1", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenNoStdInAndNoInputFile() throws Exception {
		String[] args = {"s/test/t/"};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenInvalidSyntax() throws Exception {
        String[] args = {"s/hello", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenStdoutMissing() throws Exception {
        String[] args = {"s/test/t/", file1.toString()};
        sedApp.run(args, null, null);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenUseDelimiterInReplacement() throws Exception {
        String[] args = {"s/test/t//", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenHave2InputFile() throws Exception {
        String[] args = {"s/test/t/", file1.toString(), file2.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenEmptyArguments() throws Exception {
        String[] args = {};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenInvalidReplaceIndex() throws Exception {
        String[] args = {"s/test/t/??", file1.toString()};
        sedApp.run(args, null, stdout);
    }

    @Test(expected=SedException.class)
    public void shouldThrowExceptionWhenInvalidCommandPosition() throws Exception {
        String[] args = {file1.toString(), "s/test/t/"};
        sedApp.run(args, null, stdout);
    }

    @Test
    public void whenMissingReplacementIndexExpectReplaceFirst() throws Exception {
        String[] args = {"s/test/t/", file1.toString()};
        sedApp.run(args, null, stdout);
        assertEquals(REPLACED_FIRST, stdout.toString());
    }

    @Test
    public void whenMissingRegexExpectNoChange() throws Exception {
        String[] args = {"s//t/", file1.toString()};
        sedApp.run(args, null, stdout);
        assertEquals(ORIGINAL, stdout.toString());
    }

    @Test
    public void whenOtherSeparatingCharExpectReplacement() throws Exception {
        String[] args = {"sxtestxtx", file1.toString()};
        sedApp.run(args, null, stdout);
        assertEquals(REPLACED_FIRST, stdout.toString());
    }

    @Test
    public void whenOtherSeparatingCharExpectReplacementFile() throws Exception {
        String[] args = {"s|test|t|"};
        sedApp.run(args, stdin, stdout);
        assertEquals(REPLACED_FIRST, stdout.toString());
    }

}
