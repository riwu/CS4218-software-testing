package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class SemicolonTest {

    private static final String FILE_CONTENT = "file1content";
    private static final Path FILE = Paths.get("file1");
    private static final Path NON_EXISTENT_FILE = Paths.get("non-existent");

    private InputStream inputStream;
    private OutputStream outputStream;
    private Shell shell = new ShellImpl();

    private void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @Before
    public void setUp() throws Exception {
        writeToFile(FILE, FILE_CONTENT);
        this.inputStream = new ByteArrayInputStream("".getBytes());
        this.outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(FILE);
        this.inputStream.close();
        this.outputStream.close();
    }

    @Test
    public void shouldExecuteCommandsWhenNoSpacesNearSemicolon() throws Exception {
        Shell shell = new ShellImpl();
        OutputStream outputStream = new ByteArrayOutputStream();
        String fileContent = "echoToFileContent";
        shell.parseAndEvaluate("echo " + fileContent + " > " + FILE.toString() + ";cat " + FILE.toString(),
                outputStream);
        assertEquals(fileContent + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void shouldExecuteCommandsWhenSpacesNearSemicolon() throws Exception {
        Shell shell = new ShellImpl();
        OutputStream outputStream = new ByteArrayOutputStream();
        String fileContent = "echoToFileContent";
        shell.parseAndEvaluate("echo " + fileContent + " > " + FILE.toString() + " ; cat " + FILE.toString(),
                outputStream);
        assertEquals(fileContent + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void shouldExecuteSecondCommandWhenFirstCommandFailed() throws Exception {
        Shell shell = new ShellImpl();
        OutputStream outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate("cat " + NON_EXISTENT_FILE.toString() + ";cat " + FILE.toString(),
                outputStream);
        assertEquals(FILE_CONTENT, outputStream.toString());
    }

    /**
     *
     * Following test cases uses pairwise independent combinatorial testing tool by Microsoft
     Pairwise Combinatorial Script for MS PICT

     Semicolon:	one, many
     Inside Backquote: yes, no, both
     suffix: explicit, implicit

     IF [Semicolon] = "one"	THEN [Inside Backquote] <> "both";

     -----------------------------------------------
     id 	Semicolon 	Inside Backquote 	suffix
     1 	    many 	    no              	explicit
     2  	one 	    yes             	implicit
     3 	    many     	both            	explicit
     4 	    many 	    no 	                implicit
     5 	    one     	no              	explicit
     6 	    many    	both            	implicit
     7 	    many    	yes             	explicit

     */

    @Test
    // semicolon    inside backquote    suffix
    // many         no                  explicit
    public void PICT_1() throws Exception {
        shell.parseAndEvaluate("echo hello; echo awesome; echo world;", outputStream);

        String expected = "hello" + System.lineSeparator() + "awesome" + System.lineSeparator() + "world"  + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @Test
    // semicolon    inside backquote    suffix
    // one          yes                  implicit
    public void PICT_2() throws Exception {
        shell.parseAndEvaluate("echo `echo hello; echo awesome`", outputStream);

        String expected = "helloawesome" + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @Test
    // semicolon    inside backquote    suffix
    // many         both                explicit
    public void PICT_3() throws Exception {
        shell.parseAndEvaluate("echo `echo hello; echo awesome;`; echo world;", outputStream);

        String expected = "helloawesome" + System.lineSeparator() + "world" + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @Test
    // semicolon    inside backquote    suffix
    // many         no                  implicit
    public void PICT_4() throws Exception {
        shell.parseAndEvaluate("echo hello; echo awesome; echo world", outputStream);

        String expected = "hello" + System.lineSeparator() + "awesome" + System.lineSeparator() + "world" + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @Test
    // semicolon    inside backquote    suffix
    // one          no                  explicit
    public void PICT_5() throws Exception {
        shell.parseAndEvaluate("echo hello; echo awesome;", outputStream);

        String expected = "hello" + System.lineSeparator() + "awesome" + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @Test
    // semicolon    inside backquote    suffix
    // many         both                implicit
    public void PICT_6() throws Exception {
        shell.parseAndEvaluate("echo `echo hello; echo awesome`; echo world", outputStream);

        String expected = "helloawesome" + System.lineSeparator() + "world" + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @Test
    // semicolon    inside backquote    suffix
    // many         yes                  explicit
    public void PICT_7() throws Exception {
        shell.parseAndEvaluate("echo `echo hello; echo awesome; echo world;`;", outputStream);

        String expected = "helloawesomeworld" + System.lineSeparator();
        String actual = outputStream.toString();

        assertEquals(expected, actual);
    }
}
