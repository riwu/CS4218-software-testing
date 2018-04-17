package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PasteDiffIntegrationTest {
	private final ShellImpl shell = new ShellImpl();
	private static final String PART_ONE = "This" + System.lineSeparator() + "the" + System.lineSeparator();
	private static final String PART_TWO = "is testtest" + System.lineSeparator() + "test text test" + System.lineSeparator();
	private static final String ORIGINAL_FULL = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator();
	private static final String REPLACED_TABS = "This is testtest" + System.lineSeparator() + "the test text test" + System.lineSeparator();
	private static final String EXTRA_LINE = "This\tis testtest" + System.lineSeparator() + "the\ttest text test" + System.lineSeparator() + System.lineSeparator();
	private static Path fileA;
	private static Path fileB;
	private static Path fileSame;
	private static Path fileDiff;
	private static Path fileDiffBlank;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileA = Files.createTempFile("fileA", null);
		fileB = Files.createTempFile("fileB", null);
		fileSame = Files.createTempFile("fileSame", null);
		fileDiff = Files.createTempFile("fileDiff", null);
		fileDiffBlank = Files.createTempFile("fileDiffBlank", null);
		fileA.toFile().deleteOnExit();
		fileB.toFile().deleteOnExit();
		fileSame.toFile().deleteOnExit();
		fileDiff.toFile().deleteOnExit();
		fileDiffBlank.toFile().deleteOnExit();
		Files.write(fileA, PART_ONE.getBytes());
		Files.write(fileB, PART_TWO.getBytes());
		Files.write(fileSame, ORIGINAL_FULL.getBytes());
		Files.write(fileDiff, REPLACED_TABS.getBytes());
		Files.write(fileDiffBlank, EXTRA_LINE.getBytes());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
    @Test
    public void whenSameContentsNoFlagsExpectEmptyString() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + " | " + "diff " + fileSame.toString() + " - ";
		String expected = "";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void whenSameContentsShowIdenticalFlagExpectIdenticalMsg() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff -s " + fileSame.toString() + " - ";
		String expected = "Files " + fileSame.toString() + " and " + "-" + " are identical";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void whenSameContentsShowDifferFlagExpectDifferMsg() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff -q " + fileDiff.toString() + " - ";
		String expected = "Files " + fileDiff.toString() + " and " + "-" + " differ";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void whenDiffAreBlankLinesWithIgnoreBlankAndIdenticalFlagExpectIdenticalMsg() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff -sB - " + fileDiffBlank.toString();
		String expected = "Files - and " + fileDiffBlank.toString() + " are identical";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void whenDiffAreBlankLinesWithIgnoreBlankFlagExpectEmptyString() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff -B " + fileDiffBlank.toString() + " - ";
		String expected = "";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
	
    @Test(expected=PasteException.class)
    public void whenMissingFileFromPasteExpectPasteException() throws Exception {
    	String cmdline = "paste missing " + "|" + "diff ";
    	shell.parseAndEvaluate(cmdline, System.out);
    }
	
	@Test(expected=DiffException.class)
	public void whenMissingFileFromDiffExpectDiffException() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff missing.txt -";
    	shell.parseAndEvaluate(cmdline, System.out);
	}

    @Test(expected=DiffException.class)
    public void whenInsufficientArgExpectDiffException() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff ";
    	shell.parseAndEvaluate(cmdline, System.out);
    }
    
    @Test(expected=DiffException.class)
    public void whenDoubleStdinExpectDiffException() throws Exception {
    	String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "diff - -";
    	shell.parseAndEvaluate(cmdline, System.out);
    }

}
