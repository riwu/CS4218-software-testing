package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CatApplicationTest {

    private CatApplication catApplication = new CatApplication();
    private File emptyFile, existingFile;
    private String existingFileContent;

    @Before
    public void setUp() throws Exception {
        emptyFile = File.createTempFile("cat", "empty");

        existingFile = File.createTempFile("cat", "existing");
        existingFileContent = "Line 1" + System.lineSeparator() + "Line 2";
        Files.write(existingFile.toPath(), existingFileContent.getBytes());
    }

    @After
    public void tearDown() throws Exception {
        emptyFile.delete();
        existingFile.delete();
    }

    @Test
    public void getContentForEmptyFileTest() throws Exception {
        assertEquals(catApplication.getContent(emptyFile), "");
    }

    @Test
    public void getContentForExistingFileTest() throws Exception {
        assertEquals(catApplication.getContent(existingFile), existingFileContent);
    }

    @Test(expected = CatException.class)
    public void getContentForNonExistingFileTest() throws CatException {
        assertTrue(existingFile.delete());
        catApplication.getContent(existingFile);
    }
}
