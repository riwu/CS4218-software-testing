package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.impl.CommandTestUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CmdSubstitutionTest {
    private static final String FILE_NAME = "CmdSubstitutionTestFile";
    private static final String FILE_CONTENT = FILE_NAME + System.lineSeparator() + "l 2" + System.lineSeparator() + "l3";

    private static void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        writeToFile(Paths.get(FILE_NAME), FILE_CONTENT);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Files.delete(Paths.get(FILE_NAME));
    }

    @Test
    public void shouldReturnFileContentWhenEchoFileName() throws Exception {
        assertEquals(FILE_CONTENT, CommandTestUtil.getCommandOutput("cat `echo " + FILE_NAME + "`"));
    }

    @Test
    public void shouldReturnFileContentWhenDoubleQuotedEcho() throws Exception {
        assertEquals(FILE_CONTENT, CommandTestUtil.getCommandOutput("cat \"`echo " + FILE_NAME + "`\""));
    }

    @Test
    public void shouldReturnStringWhenSingleQuotedEcho() throws Exception {
        String catCommand = "`cat " + FILE_NAME + "`";
        assertEquals(catCommand + System.lineSeparator(), CommandTestUtil.getCommandOutput("echo '" + catCommand +
                "'"));
    }

    @Test
    public void shouldReturnFileContentsWhenEchoMultipleTimes() throws Exception {
        String echoCommand = "`echo " + FILE_NAME + "`";
        assertEquals(FILE_CONTENT + FILE_CONTENT, CommandTestUtil.getCommandOutput("cat " + echoCommand + " " +
                echoCommand + System.lineSeparator()));
    }


    @Test
    public void shouldReplaceNewLinesWithSpacesWhenSubstituted() throws Exception {
        String catCommand = "`cat " + FILE_NAME + "`";
        assertEquals("CmdSubstitutionTestFilel 2l3" + System.lineSeparator(), CommandTestUtil.getCommandOutput("echo" +
                " " + catCommand));
    }

}
