package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CallCommandTest {

    private static final Path BASE_PATH = Paths.get(System.getProperty("user.dir"));
    private static final boolean IS_WINDOWS = System.getProperty("os.name").contains("Wind");
    private static Path testFolder;
    private static Path a;
    private static Path b;
    private static Path a_1;
    private static Path a_2;
    private static Path b_1;
    private static Path b_2;
    private static Path file_1;
    private static Path file_2;
    private static Path file_3;
    private static Path file_4;
    private static Path file_5;
    private static Path file_6;

    /*
    testFolder
        - a
            - a_1
               - file_1
            - a_2
               - file_2
               - file_3
        - b
            - b_1
            - b_2
               - file_4
            - file_5
        - file_6

     */

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testFolder = Files.createTempDirectory(BASE_PATH, "CallCommandTest");
        a = Files.createTempDirectory(testFolder, "a");
        b = Files.createTempDirectory(testFolder, "b");
        a_1 = Files.createTempDirectory(a, "a_1");
        a_2 = Files.createTempDirectory(a, "a_2");
        b_1 = Files.createTempDirectory(b, "b_1");
        b_2 = Files.createTempDirectory(b, "b_2");
        file_1 = Files.createTempDirectory(a_1, "file_1");
        file_2 = Files.createTempDirectory(a_2, "file_2");
        file_3 = Files.createTempDirectory(a_2, "file_3");
        file_4 = Files.createTempDirectory(b_2, "file_4");
        file_5 = Files.createTempDirectory(b, "file_5");
        file_6 = Files.createTempDirectory(testFolder, "file_6");

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        testFolder.toFile().deleteOnExit();
        a.toFile().deleteOnExit();
        b.toFile().deleteOnExit();
        a_1.toFile().deleteOnExit();
        a_2.toFile().deleteOnExit();
        b_1.toFile().deleteOnExit();
        b_2.toFile().deleteOnExit();
        file_1.toFile().deleteOnExit();
        file_2.toFile().deleteOnExit();
        file_3.toFile().deleteOnExit();
        file_4.toFile().deleteOnExit();
        file_5.toFile().deleteOnExit();
        file_6.toFile().deleteOnExit();
    }

    @Test
    public void Should_ExpandGlob_When_EvaluatingSingleLevel() throws Exception{
    	Assume.assumeFalse(IS_WINDOWS);
        CallCommand command = new CallCommand();
        String[] globbed = command.globFilesDirectories(testFolder.toAbsolutePath() + "/*");

        Set<String> expected = new HashSet<>();
        expected.add(a.toAbsolutePath().toString());
        expected.add(b.toAbsolutePath().toString());
        expected.add(file_6.toAbsolutePath().toString());

        Set<String> actual = new HashSet<>();
        actual.addAll(Arrays.asList(globbed));

        assertTrue(expected.containsAll(actual)); // expected is a superset of actual
        assertTrue(actual.containsAll(expected)); // actual is a superset of expected

        // then we can conclude that both set are identical and same.
    }

    @Test
    public void Should_ExpandGlobRecursively_When_EvaluatingMultilevel() throws Exception{
    	Assume.assumeFalse(IS_WINDOWS);
        CallCommand command = new CallCommand();
        String[] globbed = command.globFilesDirectories(testFolder.toAbsolutePath() + "/**");

        // list of path to files and folders
        Set<String> expected = new HashSet<>();
        expected.add(a.toAbsolutePath().toString());
        expected.add(b.toAbsolutePath().toString());
        expected.add(a_1.toAbsolutePath().toString());
        expected.add(a_2.toAbsolutePath().toString());
        expected.add(b_1.toAbsolutePath().toString());
        expected.add(b_2.toAbsolutePath().toString());
        expected.add(file_1.toAbsolutePath().toString());
        expected.add(file_2.toAbsolutePath().toString());
        expected.add(file_3.toAbsolutePath().toString());
        expected.add(file_4.toAbsolutePath().toString());
        expected.add(file_5.toAbsolutePath().toString());
        expected.add(file_6.toAbsolutePath().toString());

        Set<String> actual = new HashSet<>();
        actual.addAll(Arrays.asList(globbed));

        assertTrue(expected.containsAll(actual)); // expected is a superset of actual
        assertTrue(actual.containsAll(expected)); // actual is a superset of expected

        // then we can conclude that both set are identical and same.
    }

    @Test
    public void Should_ExpandGlobForFiles_When_EvaluatingFileGlob() throws Exception{
    	Assume.assumeFalse(IS_WINDOWS);
        CallCommand command = new CallCommand();
        String[] globbed = command.globFilesDirectories(testFolder.toAbsolutePath() + "/**/file_*");

        // list of path to files and folders
        Set<String> expected = new HashSet<>();
        expected.add(file_1.toAbsolutePath().toString());
        expected.add(file_2.toAbsolutePath().toString());
        expected.add(file_3.toAbsolutePath().toString());
        expected.add(file_4.toAbsolutePath().toString());
        expected.add(file_5.toAbsolutePath().toString());

        Set<String> actual = new HashSet<>();
        actual.addAll(Arrays.asList(globbed));

        assertTrue(expected.containsAll(actual)); // expected is a superset of actual
        assertTrue(actual.containsAll(expected)); // actual is a superset of expected

        // then we can conclude that both set are identical and same.
    }
}
