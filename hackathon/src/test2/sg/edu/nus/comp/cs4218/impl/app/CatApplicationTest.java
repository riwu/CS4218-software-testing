package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CatApplicationTest {

    private final CatApplication catApplication = new CatApplication();
    private static final Path EMPTY_FILE = Paths.get("empty");
    private static final Path FILE_1 = Paths.get("CatApplicationTest1file 1");
    private static final Path FILE_2 = Paths.get("CatApplicationTest2FILE_2");
    private static final Path FILE_WITH_SPACE = Paths.get("CatApplicationTest3  file- 3");
    private static final Path NON_EXISTENT_FILE = Paths.get("CatApplicationTestnon-existent");
    private static final String FILE_1_CONTENT = "FILE_1-1" + System.lineSeparator() + "FILE_1-2";
    private static final String FILE_2_CONTENT = "FILE_2-1" + System.lineSeparator();
    private static final String CONTENT_SPACED = "FILE_WITH_SPACE-1" + System.lineSeparator() + "FILE_WITH_SPACE-2" + System.lineSeparator();

    private OutputStream outputStream;

    private void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @Before
    public void setUp() throws Exception {
        Files.createFile(EMPTY_FILE);
        writeToFile(FILE_1, FILE_1_CONTENT);
        writeToFile(FILE_2, FILE_2_CONTENT);
        writeToFile(FILE_WITH_SPACE, CONTENT_SPACED);
        outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(EMPTY_FILE);
        Files.delete(FILE_1);
        Files.delete(FILE_2);
        Files.delete(FILE_WITH_SPACE);
    }



    @Test
    public void shouldReadFromStdinWhenNoFiles() throws CatException {
        InputStream inputStream = new ByteArrayInputStream(FILE_1_CONTENT.getBytes(StandardCharsets.UTF_8));
        catApplication.run(new String[]{}, inputStream, outputStream);
        assertEquals(FILE_1_CONTENT, outputStream.toString());
    }

    @Test
    public void shouldReturnFileContentWhenGivenFileName() throws CatException {
        catApplication.run(new String[]{FILE_1.toString()}, System.in, outputStream);
        assertEquals(FILE_1_CONTENT, outputStream.toString());
    }

    @Test
    public void shouldReturnFileContentsWhenGivenFileNames() throws CatException {
        catApplication.run(new String[]{FILE_1.toString(), FILE_2.toString(), FILE_WITH_SPACE.toString()}, System.in, outputStream);
        assertEquals(FILE_1_CONTENT + FILE_2_CONTENT + CONTENT_SPACED, outputStream.toString());
    }

    @Test
    public void shouldReturnFileContentsInCorrectOrderWhenGivenFileNames() throws CatException {
        catApplication.run(new String[]{FILE_WITH_SPACE.toString(), FILE_2.toString()}, System.in, outputStream);
        assertEquals(CONTENT_SPACED + FILE_2_CONTENT, outputStream.toString());
    }


}
