//package sg.edu.nus.comp.cs4218.impl.cmd;
//
//import org.junit.*;
//import sg.edu.nus.comp.cs4218.exception.ShellException;
//
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//
//public class CallCommandTest {
//
//    private static final Path BASE_PATH = Paths.get(System.getProperty("user.dir"));
//    private static final boolean IS_WINDOWS = System.getProperty("os.name").contains("Wind");
//    private static Path testFolder;
//    private static Path a;
//    private static Path b;
//    private static Path a_1;
//    private static Path a_2;
//    private static Path b_1;
//    private static Path b_2;
//    private static Path file_1;
//    private static Path file_2;
//    private static Path file_3;
//    private static Path file_4;
//    private static Path file_5;
//    private static Path file_6;
//
//    /*
//    testFolder
//        - a
//            - a_1
//               - file_1
//            - a_2
//               - file_2
//               - file_3
//        - b
//            - b_1
//            - b_2
//               - file_4
//            - file_5
//        - file_6
//
//     */
//
//    private InputStream inputStream;
//    private OutputStream outputStream;
//
//    @Before
//    public void setUp() throws Exception {
//        this.inputStream = new ByteArrayInputStream("".getBytes());
//        this.outputStream = new ByteArrayOutputStream();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        this.inputStream.close();
//        this.outputStream.close();
//    }
//
//
//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        testFolder = Files.createTempDirectory(BASE_PATH, "CallCommandTest");
//        a = Files.createTempDirectory(testFolder, "a");
//        b = Files.createTempDirectory(testFolder, "b");
//        a_1 = Files.createTempDirectory(a, "a_1");
//        a_2 = Files.createTempDirectory(a, "a_2");
//        b_1 = Files.createTempDirectory(b, "b_1");
//        b_2 = Files.createTempDirectory(b, "b_2");
//        file_1 = Files.createTempDirectory(a_1, "file_1");
//        file_2 = Files.createTempDirectory(a_2, "file_2");
//        file_3 = Files.createTempDirectory(a_2, "file_3");
//        file_4 = Files.createTempDirectory(b_2, "file_4");
//        file_5 = Files.createTempDirectory(b, "file_5");
//        file_6 = Files.createTempDirectory(testFolder, "file_6");
//
//    }
//
//    @AfterClass
//    public static void tearDownAfterClass() throws Exception {
//        testFolder.toFile().deleteOnExit();
//        a.toFile().deleteOnExit();
//        b.toFile().deleteOnExit();
//        a_1.toFile().deleteOnExit();
//        a_2.toFile().deleteOnExit();
//        b_1.toFile().deleteOnExit();
//        b_2.toFile().deleteOnExit();
//        file_1.toFile().deleteOnExit();
//        file_2.toFile().deleteOnExit();
//        file_3.toFile().deleteOnExit();
//        file_4.toFile().deleteOnExit();
//        file_5.toFile().deleteOnExit();
//        file_6.toFile().deleteOnExit();
//    }
//
//    @Test
//    public void Should_ExpandGlob_When_EvaluatingSingleLevel() throws Exception{
//    	Assume.assumeTrue(!IS_WINDOWS);
//        CallCommand command = new CallCommand();
//        String[] globbed = command.globFilesDirectories(testFolder.toAbsolutePath() + "/*");
//
//        Set<String> expected = new HashSet<>();
//        expected.add(a.toAbsolutePath().toString());
//        expected.add(b.toAbsolutePath().toString());
//        expected.add(file_6.toAbsolutePath().toString());
//
//        Set<String> actual = new HashSet<>();
//        actual.addAll(Arrays.asList(globbed));
//
//        assertTrue(expected.containsAll(actual)); // expected is a superset of actual
//        assertTrue(actual.containsAll(expected)); // actual is a superset of expected
//
//        // then we can conclude that both set are identical and same.
//    }
//
//    @Test
//    public void Should_ExpandGlobRecursively_When_EvaluatingMultilevel() throws Exception{
//    	Assume.assumeTrue(!IS_WINDOWS);
//        CallCommand command = new CallCommand();
//        String[] globbed = command.globFilesDirectories(testFolder.toAbsolutePath() + "/**");
//
//        // list of path to files and folders
//        Set<String> expected = new HashSet<>();
//        expected.add(a.toAbsolutePath().toString());
//        expected.add(b.toAbsolutePath().toString());
//        expected.add(a_1.toAbsolutePath().toString());
//        expected.add(a_2.toAbsolutePath().toString());
//        expected.add(b_1.toAbsolutePath().toString());
//        expected.add(b_2.toAbsolutePath().toString());
//        expected.add(file_1.toAbsolutePath().toString());
//        expected.add(file_2.toAbsolutePath().toString());
//        expected.add(file_3.toAbsolutePath().toString());
//        expected.add(file_4.toAbsolutePath().toString());
//        expected.add(file_5.toAbsolutePath().toString());
//        expected.add(file_6.toAbsolutePath().toString());
//
//        Set<String> actual = new HashSet<>();
//        actual.addAll(Arrays.asList(globbed));
//
//        assertTrue(expected.containsAll(actual)); // expected is a superset of actual
//        assertTrue(actual.containsAll(expected)); // actual is a superset of expected
//
//        // then we can conclude that both set are identical and same.
//    }
//
//    @Test
//    public void Should_ExpandGlobForFiles_When_EvaluatingFileGlob() throws Exception{
//    	Assume.assumeTrue(!IS_WINDOWS);
//        CallCommand command = new CallCommand();
//        String[] globbed = command.globFilesDirectories(testFolder.toAbsolutePath() + "/**/file_*");
//
//        // list of path to files and folders
//        Set<String> expected = new HashSet<>();
//        expected.add(file_1.toAbsolutePath().toString());
//        expected.add(file_2.toAbsolutePath().toString());
//        expected.add(file_3.toAbsolutePath().toString());
//        expected.add(file_4.toAbsolutePath().toString());
//        expected.add(file_5.toAbsolutePath().toString());
//
//        Set<String> actual = new HashSet<>();
//        actual.addAll(Arrays.asList(globbed));
//
//        assertTrue(expected.containsAll(actual)); // expected is a superset of actual
//        assertTrue(actual.containsAll(expected)); // actual is a superset of expected
//
//        // then we can conclude that both set are identical and same.
//    }
//
//    @Test
//    public void Should_DisableGlobing_When_SingleQuoting() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo '"+ testFolder.toAbsolutePath()+"/*'");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = testFolder.toAbsolutePath()+"/*" + System.lineSeparator();
//        String actual = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void Should_DisableGlobing_When_DoubleQuoting() throws Exception{
//        PipeCommand pipeCommand = new PipeCommand("echo \""+ testFolder.toAbsolutePath()+"/*\"");
//        pipeCommand.parse();
//        pipeCommand.evaluate(inputStream, outputStream);
//
//        String expected = testFolder.toAbsolutePath()+"/*" + System.lineSeparator();
//        String actual = pipeCommand.getResultStream().toString();
//
//        assertEquals(expected, actual);
//    }
//
//
//    @Test
//    public void testParseAppNameWithOneSpace() throws Exception {
//        CallCommand callCommand = new CallCommand(" ls -d");
//        callCommand.parse();
//        assertEquals("ls", callCommand.app);
//    }
//
//
//    @Test
//    public void testParseAppNameWithMultipleSpace() throws ShellException {
//        CallCommand callCommand = new CallCommand("  ls -d");
//        callCommand.parse();
//        assertEquals("ls", callCommand.app);
//    }
//
//    @Test
//    public void testParseAppNameWithOneTab() throws ShellException {
//        CallCommand callCommand = new CallCommand("\tls -d");
//        callCommand.parse();
//        assertEquals("ls", callCommand.app);
//    }
//
//    @Test
//    public void testParseAppNameWithWhitespace() throws ShellException {
//        CallCommand callCommand = new CallCommand("\t \t ls -d");
//        callCommand.parse();
//        assertEquals("ls", callCommand.app);
//    }
//
//    @Test
//    public void testParseAppNameWithMultipleTab() throws ShellException {
//        CallCommand callCommand = new CallCommand("\t\tls -d");
//        callCommand.parse();
//        assertEquals("ls", callCommand.app);
//    }
//
//    @Test
//    public void testParseArgs() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"hello1", "world1"};
//
//        CallCommand callCommand = new CallCommand("cat \"hello1\" \"world1\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseArgsWithMulSpace() throws ShellException {
//        int expectedNumArgs = 2;
//        final int inOutNumArgs = 2; // need to substract from total
//
//        CallCommand callCommand = new CallCommand("cat   \"hello2\"   \"world2\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length - inOutNumArgs);
//    }
//
//    @Test
//    public void testParseArgsWithTab() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"hello3", "world3"};
//
//        CallCommand callCommand = new CallCommand("cat\t\"hello3\"\t\"world3\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseArgsWithMulTab() throws ShellException {
//        int expectedNumArgs = 2;
//        final int inOutNumArgs = 2; // need to substract from total
//
//        CallCommand callCommand = new CallCommand("cat\t\t\t\"hello4\"\t\t\t\"world4\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length - inOutNumArgs);
//    }
//
//    @Test
//    public void testParseArgsWithWhitespace() throws ShellException {
//        int expectedNumArgs = 2;
//        final int inOutNumArgs = 2; // need to substract from total
//
//        CallCommand callCommand = new CallCommand("cat\t \"hello5\" \t\"world5\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length - inOutNumArgs);
//    }
//
//
//    @Test
//    public void testParseStartSingleSpaceInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {" hello6", " world6"};
//
//        CallCommand callCommand = new CallCommand("cat \" hello6\" \" world6\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseStartMulSpaceInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"  hello7", "  world7"};
//
//        CallCommand callCommand = new CallCommand("cat \"  hello7\" \"  world7\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseEndSingleSpaceInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"hello8 ", "world8 "};
//
//        CallCommand callCommand = new CallCommand("cat \"hello8 \" \"world8 \"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseEndMulSpaceInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"hello9  ", "world9  "};
//
//        CallCommand callCommand = new CallCommand("cat \"hello9  \" \"world9  \"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseStartSingleTabInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"\thello10", "\tworld10"};
//
//        CallCommand callCommand = new CallCommand("cat \"\thello10\" \"\tworld10\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseStartMulTabInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"\t\thello11", "\t\tworld11"};
//
//        CallCommand callCommand = new CallCommand("cat \"\t\thello11\" \"\t\tworld11\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseEndSingleTabInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"hello12\t", "world12\t"};
//
//        CallCommand callCommand = new CallCommand("cat \"hello12\t\" \"world12\t\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//
//    @Test
//    public void testParseEndMulTabInArg() throws ShellException {
//        int expectedNumArgs = 2;
//        String[] expectedArgs = {"hello13\t\t", "world13\t\t"};
//
//        CallCommand callCommand = new CallCommand("cat \"hello13\t\t\" \"world13\t\t\"");
//        callCommand.parse();
//
//        assertEquals(expectedNumArgs, callCommand.argsArray.length);
//        assertArrayEquals(expectedArgs, callCommand.argsArray);
//    }
//}
