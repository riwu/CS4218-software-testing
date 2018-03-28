package sg.edu.nus.comp.cs4218.test.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.SplitApplication;
import sg.edu.nus.comp.cs4218.test.TestEnvironment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

@EnableRuleMigrationSupport
public class SplitApplicationTest extends TestEnvironment {
    private static final String DEFAULT_SUFFIX = "xaa";
    private static final int[] SEQUENCE_LENGTH = { 676, 17576, 45697 }; // max length with 2, 3, 4 alphabets suffix
    private static final String[] FILES = {
            "small1.txt", "small2.txt", "small3.txt", // 26^2 - 1, 26^2. 26^2 + 1 lines
            "medium1.txt", "medium2.txt", "medium3.txt", // 26^3 - 1, 26^3. 26^3 + 1 lines
            "large1.txt", "large2.txt", "large3.txt" // 26^4 - 1, 26^4. 26^4 + 1 lines
    };
    private static final String TEST_DIR = "split-test" + CHAR_FILE_SEP;
    private static final String XAR = "xar";
    private SplitApplication sut;
    private Path tempPath;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        
        sut = new SplitApplication();
        tempPath = tempFolder.getRoot().toPath().resolve(ENV_ROOT).resolve(TEST_DIR);
        Environment.currentDirectory = tempPath.toString();
    }

    @Test
    public void smallFileSingleLineSplitUnderTwoSuffixLimit() throws Exception {
        String file = FILES[0];
        sut.splitFileByLines(file, "x", 1);

        Stream<Path> dirListing = Files.list(tempPath);

        // Check file content sequence
        String startContent = getFirstLineContent(DEFAULT_SUFFIX);
        String endContent = getFirstLineContent("xzy");

        assertEquals("0", startContent);
        assertEquals(String.valueOf(SEQUENCE_LENGTH[0] - 2), endContent);

        // Check number of chunks
        assertEquals(SEQUENCE_LENGTH[0] - 1, dirListing.count() - FILES.length);

        // Check naming sequence
        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve("xzy")));
    }

    @Test
    public void smallFileSingleLineSplitAtTwoSuffixLimit() throws Exception {
        String file = FILES[1];
        sut.splitFileByLines(file, "x", 1);

        Stream<Path> dirListing = Files.list(tempPath);

        String startContent = getFirstLineContent(DEFAULT_SUFFIX);
        String endContent = getFirstLineContent("xzz");

        assertEquals("0", startContent);
        assertEquals(String.valueOf(SEQUENCE_LENGTH[0] - 1), endContent);

        assertEquals(SEQUENCE_LENGTH[0], dirListing.count() - FILES.length);

        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve("xzz")));
    }

    @Test
    public void smallFileSingleLineSplitOverTwoSuffixLimit() throws Exception {
        String file = FILES[2];
        sut.splitFileByLines(file, "x", 1);

        Stream<Path> dirListing = Files.list(tempPath);

        String startContent = getFirstLineContent(DEFAULT_SUFFIX);
        String endContent = getFirstLineContent("xzaaa");

        assertEquals("0", startContent);
        assertEquals(String.valueOf(SEQUENCE_LENGTH[0]), endContent);

        assertEquals(SEQUENCE_LENGTH[0] + 1, dirListing.count() - FILES.length);

        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve("xzaaa")));
    }

    @Test
    public void linesInFileNotMultipleOfLinesPrefix() throws Exception {
        int defaultChunkSize = 1000;
        String file = FILES[4];
        sut.splitFileByLines(file, "x", defaultChunkSize); // default case

        Stream<Path> dirListing = Files.list(tempPath);
        int chunks = SEQUENCE_LENGTH[1] / defaultChunkSize + 1;
        String startContent = getFirstLineContent(DEFAULT_SUFFIX);
        String endContent = getFirstLineContent(XAR);

        assertEquals("0", startContent);
        assertEquals(String.valueOf((chunks - 1) * defaultChunkSize), endContent);

        assertEquals(chunks, dirListing.count() - FILES.length);

        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve(XAR)));
    }

    @Test
    public void bytesInFileNotMultipleOfBytesPrefix() throws Exception {
        String file = FILES[1]; // files is about 3kb
        String lastPart = "xab";
        int twoKB = 2048;
        long fileSize = Files.size(Paths.get(tempPath.toString()).resolve(file));
        sut.splitFileByBytes(file, "x", "2k");

        Stream<Path> dirListing = Files.list(tempPath);
        long chunks = fileSize / twoKB + 1;

        String part1FirstLine = getFirstLineContent(DEFAULT_SUFFIX);
        String part1LastLine = getLastLineContent(DEFAULT_SUFFIX);
        String part2FirstLine = getFirstLineContent(lastPart);

        assertEquals("0", part1FirstLine);
        // Single line is split into 2 files
        assertEquals("53", part1LastLine);
        assertEquals("9", part2FirstLine);

        assertEquals(chunks, dirListing.count() - FILES.length);

        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve(lastPart)));
    }

    @Test
    public void linesInStdinNotMultipleOfLinesPrefix() throws Exception {
        int defaultChunkSize = 1000;
        String file = FILES[4];
        Path filePath = tempPath.resolve(file);

        sut.splitFileByLines(filePath.toString(), "x", defaultChunkSize); // default case

        Stream<Path> dirListing = Files.list(tempPath);
        int chunks = SEQUENCE_LENGTH[1] / defaultChunkSize + 1;
        String startContent = getFirstLineContent(DEFAULT_SUFFIX);
        String endContent = getFirstLineContent(XAR);

        assertEquals("0", startContent);
        assertEquals(String.valueOf((chunks - 1) * defaultChunkSize), endContent);

        assertEquals(chunks, dirListing.count() - FILES.length);

        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve(XAR)));
    }

    @Test
    public void bytesInStdinNotMultipleOfBytesPrefix() throws Exception {
        String file = FILES[1]; // files is about 3kb
        int twoKB = 2048;
        Path filePath = tempPath.resolve(file);
        long fileSize = Files.size(filePath);
        byte[] content = Files.readAllBytes(filePath);
        InputStream stream = new ByteArrayInputStream(content);
        sut.splitStdinByBytes(stream, "x", "2k");

        Stream<Path> dirListing = Files.list(tempPath);
        long chunks = fileSize / twoKB + 1;

        String part1FirstLine = getFirstLineContent(DEFAULT_SUFFIX);
        String part1LastLine = getLastLineContent(DEFAULT_SUFFIX);
        String part2FirstLine = getFirstLineContent("xab");

        assertEquals("0", part1FirstLine);
        // Single line is split into 2 files
        assertEquals("53", part1LastLine);
        assertEquals("9", part2FirstLine);

        assertEquals(chunks, dirListing.count() - FILES.length);

        assertTrue(Files.exists(tempPath.resolve(DEFAULT_SUFFIX)));
        assertTrue(Files.exists(tempPath.resolve("xab")));
    }

    @Test
    public void originalFileIntegrity() throws Exception {
        String file = FILES[1];
        Path filePath = Paths.get(tempPath.toString()).resolve(file);
        long fileSizeBefore = Files.size(Paths.get(tempPath.toString()).resolve(file));

        sut.splitFileByLines(file, "", 1000);

        long fileSizeAfter = Files.size(Paths.get(tempPath.toString()).resolve(file));
        assertTrue(Files.exists(tempPath.resolve(file)));
        assertEquals(fileSizeBefore, fileSizeAfter);
    }

    @Test
    public void illegalChunkSize() {
        String file = FILES[1];

        assertThrows(Exception.class, () -> {
            sut.splitFileByLines(tempPath.resolve(file).toString(), "", 0);
            sut.splitFileByLines(tempPath.resolve(file).toString(), "", -1);
            sut.splitFileByBytes(tempPath.resolve(file).toString(), "", "");
            sut.splitFileByBytes(tempPath.resolve(file).toString(), "", "1g");
            sut.splitFileByBytes(tempPath.resolve(file).toString(), "", "one");
            sut.splitFileByBytes(tempPath.resolve(file).toString(), "", "1K");
        });
    }

    @Test
    public void prefixIsEmptyForSplitByLines() throws Exception {
        String file = FILES[1];

        sut.splitFileByLines(tempPath.resolve(file).toString(), "", 1);
        assertTrue(Files.exists(tempPath.resolve("aa")));
        assertTrue(Files.exists(tempPath.resolve("zz")));
        assertEquals(SEQUENCE_LENGTH[0], Files.list(tempPath).count() - FILES.length);
    }

    @Test
    public void prefixIsEmptyForSplitByBytes() throws Exception {
        String file = FILES[1];

        sut.splitFileByBytes(tempPath.resolve(file).toString(), "", "1k");
        assertTrue(Files.exists(tempPath.resolve("aa")));
        assertTrue(Files.exists(tempPath.resolve("ab")));
        assertEquals(3, Files.list(tempPath).count() - FILES.length);
    }

    @Test
    public void prefixIsSpecialCharacterForSplitByLines() throws Exception {
        String file = FILES[1];

        sut.splitFileByLines(tempPath.resolve(file).toString(), "∂", 1);
        assertTrue(Files.exists(tempPath.resolve("∂aa")));
        assertTrue(Files.exists(tempPath.resolve("∂zz")));
        assertEquals(SEQUENCE_LENGTH[0], Files.list(tempPath).count() - FILES.length);
    }

    @Test
    public void prefixIsSpecialCharacterForSplitByBytes() throws Exception {
        String file = FILES[1];

        sut.splitFileByBytes(tempPath.resolve(file).toString(), "∂", "1k");
        assertTrue(Files.exists(tempPath.resolve("∂aa")));
        assertTrue(Files.exists(tempPath.resolve("∂ac")));
        assertEquals(3, Files.list(tempPath).count() - FILES.length);
    }

    @Test
    public void longPrefixForSplitByLines() throws Exception {
        String file = FILES[1];
        String longPrefix = makeLongPrefix('a');

        sut.splitFileByLines(tempPath.resolve(file).toString(), longPrefix, 1);
        assertTrue(Files.exists(tempPath.resolve(longPrefix + "aa")));
        assertTrue(Files.exists(tempPath.resolve(longPrefix + "zz")));
        assertEquals(SEQUENCE_LENGTH[0], Files.list(tempPath).count() - FILES.length);
    }

    @Test
    public void longPrefixForSplitByBytes() throws Exception {
        String file = FILES[1];
        String longPrefix = makeLongPrefix('a');

        sut.splitFileByBytes(tempPath.resolve(file).toString(), longPrefix, "1k");
        assertTrue(Files.exists(tempPath.resolve(longPrefix + "aa")));
        assertTrue(Files.exists(tempPath.resolve(longPrefix + "ac")));
        assertEquals(3, Files.list(tempPath).count() - FILES.length);
    }

    private String getFirstLineContent(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(tempPath.resolve(fileName).toString()));
        return reader.readLine();
    }

    private String getLastLineContent(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(tempPath.resolve(fileName).toString()));
        String line;
        String lastLine = null;

        while ((line = reader.readLine()) != null) {
            lastLine = line;
        }

        return lastLine;
    }

    private byte[] getContent(String fileName) throws IOException {
        return Files.readAllBytes(tempPath.resolve(fileName));
    }

    private String makeLongPrefix(char seed) {
        // max file length depends on OS but generally should be 255 including the counter name
        int maxFileNameLength = 100;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < maxFileNameLength; ++i) {
            builder.append(seed);
        }

        return builder.toString();
    }
}
