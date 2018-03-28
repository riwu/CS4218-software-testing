package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PasteSedIntegrationTest {
	private final ShellImpl shell = new ShellImpl();
	private static final String PART_ONE = "This" + System.lineSeparator() + "the" + System.lineSeparator();
	private static final String PART_TWO = "is testtest" + System.lineSeparator() + "test text test" + System.lineSeparator();
	private static final String ORIGINAL_FULL = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator();
	private static final String REPLACED_TABS = "This is testtest" + System.lineSeparator() + "the test text test" + System.lineSeparator();
	private static final String REPLACED_SECOND = "This\tis testt" + System.lineSeparator() + "the\ttest text t" + System.lineSeparator();
    private static final String REPLACED_FIRST = "This\tis ttest" + System.lineSeparator() + "the\tt text test" + System.lineSeparator();
    private static final String DELETE_TEXT = "This\tis test" + System.lineSeparator() + "the\t text test" + System.lineSeparator();
	private static Path fileA;
	private static Path fileB;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileA = Files.createTempFile("fileA", null);
		fileB = Files.createTempFile("fileB", null);
		fileA.toFile().deleteOnExit();
		fileB.toFile().deleteOnExit();
		OutputStream outStream = new FileOutputStream(fileA.toFile());
		outStream.write(PART_ONE.getBytes());
		outStream.close();
		outStream = new FileOutputStream(fileB.toFile());
		outStream.write(PART_TWO.getBytes());
		outStream.close();
	}
	
	@Test
	public void shouldMergeLinesWithTabWhenBothFilesSameNumberedLinesStdin() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s///";
		String expected = ORIGINAL_FULL;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldNotReplaceTabsWithSpaceWhenIndexOutOfBoundsStdin() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/\\t/ /50";
		String expected = ORIGINAL_FULL;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
	
	@Test
	public void whenReachReplacementIndexExpectChangeAtIndexStdin() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/test/t/2";
    	String expected = REPLACED_SECOND;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
	
    @Test
    public void whenEmptyReplacementExpectDeleteStdin() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/test//";
    	String expected = DELETE_TEXT;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void whenOtherSeparatingCharExpectReplacementFile() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s@test@t@";
    	String expected = REPLACED_FIRST;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
	
	@Test
	public void shouldReplaceTabsWithSpaceWhenBothFilesMerge() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/\\t/ /";
		String expected = REPLACED_TABS;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
	
	@Test
	public void shouldReplaceFileOnlyWhenSedFileSpecified() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/\\t/ / " + fileA.toString();
		String expected = PART_ONE;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}

	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenFileNotExist() throws Exception {	
		String cmdline = "paste missing.txt | sed s/^/a/";
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenMissingS() throws Exception {	
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed /\\t/ /";
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenInvalidIndex() throws Exception {	
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/is/was/-2";
		shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=PasteException.class)
	public void shouldThrowExceptionWhenFileIsDirectory() throws Exception {	
		Path temp = Files.createTempDirectory("temporary");
		temp.toFile().deleteOnExit();
		String cmdline = "paste " + temp.toString() + "|" + "sed s/\\t/ / ";
		shell.parseAndEvaluate(cmdline, System.out);
	}

}
