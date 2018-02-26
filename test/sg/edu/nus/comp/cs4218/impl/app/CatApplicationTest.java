package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CatApplicationTest {

    private CatApplication catApplication = new CatApplication();
    private Path emptyFile, file1, file2, file3;
    private String file1Content, file2Content, file3Content;
    private OutputStream outputStream;

    private void writeToFile(Path file, String content) throws Exception {
        Files.createFile(file);
        Files.write(file, content.getBytes());
    }

    @Before
    public void setUp() throws Exception {
        emptyFile = Paths.get("empty");
        Files.createFile(emptyFile);

        file1 = Paths.get("file1");
        file1Content = "file1-1" + System.lineSeparator() + "file1-2";
        writeToFile(file1, file1Content);

        file2 = Paths.get("file2");
        file2Content = "file2-1" + System.lineSeparator();
        writeToFile(file2, file2Content);

        file3 = Paths.get("file3");
        file3Content = "file3-1" + System.lineSeparator() + "file3-2" + System.lineSeparator();
        writeToFile(file3, file3Content);

        InputStream inputStream = new ByteArrayInputStream(file1Content.getBytes(StandardCharsets.UTF_8));
        System.setIn(inputStream);
        outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(emptyFile);
        Files.delete(file1);
        Files.delete(file2);
        Files.delete(file3);
    }

    @Test
    public void Should_ReturnEmptyString_When_GivenEmptyFile() throws CatException {
        assertEquals("", new String(catApplication.getContent(emptyFile)));
    }

    @Test
    public void Should_ReturnFileContent_When_GivenFileWithContent() throws CatException {
        assertEquals(file1Content, new String(catApplication.getContent(file1)));
    }

    @Test(expected = CatException.class)
    public void Should_ThrowException_When_GivenNonExistingFile() throws CatException {
        catApplication.getContent(Paths.get("Non-existent"));
    }

    @Test
    public void Should_ReadFromStdin_When_NoFiles() throws CatException {
        catApplication.run(new String[]{}, System.in, outputStream);
        assertEquals(file1Content, outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContent_When_GivenFileName() throws CatException {
        catApplication.run(new String[]{file1.toString()}, System.in, outputStream);
        assertEquals(file1Content, outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContents_When_GivenFileNames() throws CatException {
        catApplication.run(new String[]{file1.toString(), file2.toString(), file3.toString()}, System.in, outputStream);
        assertEquals(String.join("", new String[]{file1Content, file2Content, file3Content}), outputStream.toString());
    }

    @Test
    public void Should_ReturnFileContentsInCorrectOrder_When_GivenFileNames() throws CatException {
        catApplication.run(new String[]{file3.toString(), file2.toString()}, System.in, outputStream);
        assertEquals(String.join("", new String[]{file3Content, file2Content}), outputStream.toString());
    }
}
