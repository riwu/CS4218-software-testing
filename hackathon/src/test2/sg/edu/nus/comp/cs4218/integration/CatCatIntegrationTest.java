package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.ShellImpl;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CatCatIntegrationTest {
	private final ShellImpl shell = new ShellImpl();
	private static final String NO_NEWLINE = "This text has no new line";
	private static Path emptyFile;
	private static Path fileNoNewLine;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		emptyFile = Files.createTempFile("emptyfile", null);
		fileNoNewLine = Files.createTempFile("nonewline", null);
		emptyFile.toFile().deleteOnExit();
		fileNoNewLine.toFile().deleteOnExit();
		Files.write(fileNoNewLine, NO_NEWLINE.getBytes());
	}

    @Test
    public void shouldReturnEmptyStringWhenGivenEmptyFile() throws Exception {
		String cmdline = "cat " + emptyFile.toString() + "|" + "cat ";
    	String expected = "";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldNotHaveNewLineWhenFileNotEndingWithNewLine() throws Exception {
    	String cmdline = "cat " + fileNoNewLine.toString() + "|" + "cat ";
    	String expected = NO_NEWLINE;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldReadFromFileWhenFileSpecified() throws Exception {
    	String cmdline = "cat " + "|" + "cat " + fileNoNewLine.toString();
    	String expected = NO_NEWLINE;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldReturnEmptyStringWhenNoFilesSpecified() throws Exception {
    	String cmdline = "cat " + "|" + "cat ";
    	String expected = "";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
}
