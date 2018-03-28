package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.CmpException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class EchoCmpIntegrationTest {
	private final ShellImpl shell = new ShellImpl();
	private static final String TEST_TEXT_A = "The departure point of the paper is the skip-gram model";
	private static final String TEST_TEXT_B = "Tha departure point of the paper is the skip-gram modal";
	private static final String TEST_TEXT_C = "The departure point of the paper is the skip-gram model" + System.lineSeparator() + "a";
	private static Path fileA;
	private static Path fileB;
	private static Path fileC;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileA = Files.createTempFile("fileA", null);
		fileB = Files.createTempFile("fileB", null);
		fileC = Files.createTempFile("fileC", null);
		OutputStream outStream = new FileOutputStream(fileA.toFile());
		outStream.write(TEST_TEXT_A.getBytes());
		outStream.write(System.lineSeparator().getBytes());
		outStream.close();
		outStream = new FileOutputStream(fileB.toFile());
		outStream.write(TEST_TEXT_B.getBytes());
		outStream.close();
		outStream = new FileOutputStream(fileC.toFile());
		outStream.write(TEST_TEXT_C.getBytes());
		outStream.write(System.lineSeparator().getBytes());
		outStream.close();
		fileA.toFile().deleteOnExit();
		fileB.toFile().deleteOnExit();
		fileC.toFile().deleteOnExit();
	}

	@Test
	public void shouldSimplifiedWhenSimplifyOption() throws Exception {
		String cmdline = "echo " + TEST_TEXT_A + "|" + "cmp -scl" + " - " + fileB.toString();
        String expected = "Files differ" + System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenOverTwoComparisons() throws Exception {
		String cmdline = "echo " + TEST_TEXT_A + "|" + "cmp -scl" + " - " + fileB.toString() + " " + fileB.toString();
        shell.parseAndEvaluate(cmdline, System.out);
	}
	
	@Test(expected=CmpException.class)
	public void shouldThrowExceptionWhenUnderTwoComparisons() throws Exception {
		String cmdline = "echo " + TEST_TEXT_A + "|" + "cmp -scl" + " - ";
        shell.parseAndEvaluate(cmdline, System.out);
	}
}
