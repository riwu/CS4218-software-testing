//package sg.edu.nus.comp.cs4218.impl.cmd;
//
//import static org.junit.Assert.*;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.nio.file.Files;
//import java.util.Arrays;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import sg.edu.nus.comp.cs4218.exception.ShellException;
//
//public class IORedirectionTest {
//	private File inputFile;
//	private File outputFile;
//	private File tempFile;
//	private static final String INPUT_FILE_NAME = "testIO.txt";
//	private static final String OUTPUT_FILE_NAME = "output.dat";
//	private static final String TEMP_FILE_NAME = "output.txt";
//	private static final String FILE_CONTENT = "Text" + System.lineSeparator() + "file";
//	private static final String EXISTING_CONTENT = "Existing Content";
//	private static final String CAT_IN = "cat < ";
//	private static final String REDIRECT_OUT = " > ";
//
//	@Before
//	public void setUp() throws Exception {
//		inputFile = new File(INPUT_FILE_NAME);
//		outputFile = new File(OUTPUT_FILE_NAME);
//		tempFile = new File(TEMP_FILE_NAME);
//		Files.write(inputFile.toPath(), FILE_CONTENT.getBytes());
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		inputFile.delete();
//		outputFile.delete();
//		tempFile.delete();
//	}
//
//	@Test
//	public void shouldPassWhenInputFileExists() throws Exception {
//		CallCommand callCommand = new CallCommand(CAT_IN + INPUT_FILE_NAME);
//		callCommand.parse();
//		//redirect output to file to check if contents match
//		callCommand.evaluate(System.in, new FileOutputStream(outputFile));
//		assertTrue(Arrays.equals(FILE_CONTENT.getBytes(), Files.readAllBytes(outputFile.toPath())));
//	}
//
//	@Test
//	public void shouldPassWhenInputOutputFilesExist() throws Exception {
//		CallCommand callCommand = new CallCommand(CAT_IN + INPUT_FILE_NAME + REDIRECT_OUT + OUTPUT_FILE_NAME);
//		callCommand.parse();
//		Files.write(outputFile.toPath(), EXISTING_CONTENT.getBytes());
//		callCommand.evaluate(System.in, System.out);
//		assertTrue(Arrays.equals(FILE_CONTENT.getBytes(), Files.readAllBytes(outputFile.toPath())));
//	}
//
//	@Test(expected = ShellException.class)
//	public void shouldThrowExceptionWhenNoOutputFileSpecified() throws Exception {
//		CallCommand callCommand = new CallCommand(CAT_IN + INPUT_FILE_NAME + REDIRECT_OUT);
//		callCommand.parse();
//	}
//
//	@Test(expected = ShellException.class)
//	public void shouldThrowExceptionWhenNoInputFileSpecified() throws Exception {
//		CallCommand callCommand = new CallCommand(CAT_IN);
//		callCommand.parse();
//	}
//
//	@Test(expected = ShellException.class)
//	public void shouldThrowExceptionWhenMultipleFilesSpecified() throws Exception {
//		CallCommand callCommand = new CallCommand(CAT_IN + INPUT_FILE_NAME + REDIRECT_OUT + OUTPUT_FILE_NAME + " > " + TEMP_FILE_NAME);
//		callCommand.parse();
//	}
//
//	@Test(expected = ShellException.class)
//	public void shouldThrowExceptionWhenInputFileNotExist() throws Exception {
//		CallCommand callCommand = new CallCommand("cat < test");
//		callCommand.parse();
//		callCommand.evaluate(System.in, System.out);
//	}
//
//	@Test(expected = ShellException.class)
//	public void shouldThrowExceptionWhenInputOutputSameFile() throws Exception {
//		CallCommand callCommand = new CallCommand(CAT_IN + INPUT_FILE_NAME + REDIRECT_OUT + INPUT_FILE_NAME);
//		callCommand.parse();
//		callCommand.evaluate(System.in, System.out);
//	}
//
//	@Test
//	public void shouldCreateFileWhenOutputFileNotExist() throws Exception {
//		CallCommand callCommand = new CallCommand("cat " + INPUT_FILE_NAME + REDIRECT_OUT + OUTPUT_FILE_NAME);
//		callCommand.parse();
//		assertFalse(outputFile.exists());
//		callCommand.evaluate(System.in, System.out);
//		assertTrue(outputFile.exists());
//	}
//}
