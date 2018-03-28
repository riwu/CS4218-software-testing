package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.SedApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@EnableRuleMigrationSupport
public class SedApplicationTest {
    protected static final String REPLACEMENT_FMT = "s/%s/%s/%d";
    protected static final String PATTERN = "PATTERN";
    protected static final String REPLACEMENT = "REPLACED";
    protected static final String FILENAME = "test_file.txt";

    @Nested
    class FileTests {

        private SedApplication sut;
        private SedApplicationFileContent content;

        @Rule
        TemporaryFolder tempFolder = new TemporaryFolder();

        @BeforeEach
        void setUp() throws Exception {
            tempFolder.create();
            Environment.currentDirectory = tempFolder.getRoot().getAbsolutePath();
            sut = new SedApplication();
        }

        /**
         * [SINGLE]
         * Empty file should be tested once only.
         *
         * @throws Exception
         */
        @Test
        void emptyFileTest() throws Exception {
            content = new SedApplicationFileContent(0, 0, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * [SINGLE]
         * Empty occurence should be tested once only.
         */
        @Test
        void emptyOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(10, 0, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Single occurrence of pattern in a line.
         * @throws Exception
         */
        @Test
        void singleLineSingleOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(1, 1, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Many occurrences of a pattern in a line.
         * @throws Exception
         */
        @Test
        void singleLineManyOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(1, 5, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Replaces the N-th index in the single line.
         *
         * @throws Exception
         */
        @Test
        void singleLineManyOccurenceNthIndexTest() throws Exception {
            int index = 5;
            content = new SedApplicationFileContent(1, 5, PATTERN, REPLACEMENT, index);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, index, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Same test, but for many lines.
         *
         * @throws Exception
         */
        @Test
        void manyLineSingleOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(20, 1, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Same test, but for many lines.
         *
         * @throws Exception
         */
        @Test
        void manyLineManyOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(20, 10, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Same test, but for many lines in the file.
         *
         * @throws Exception
         */
        @Test
        void manyLineManyOccurenceNthIndexTest() throws Exception {
            int index = 5;
            content = new SedApplicationFileContent(20, 10, PATTERN, REPLACEMENT, index);
            content.createFile(tempFolder, FILENAME);

            String result = sut.replaceSubstringInFile(PATTERN, REPLACEMENT, index, FILENAME);
            assertEquals(content.getExpected(), result);
        }

        @Test
        void fileDoesNotExistTest() {
            assertThrows(SedException.class, () -> {
                sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 1, FILENAME);
            });
        }

        @Test
        void indexZeroTest() {
            assertThrows(SedException.class, () -> {
                sut.replaceSubstringInFile(PATTERN, REPLACEMENT, 0, FILENAME);
            });
        }
    }

    @Nested
    class StdinTests {
        private SedApplication sut;
        private SedApplicationFileContent content;

        private InputStream inputStream = null;

        @BeforeEach
        void setUp() {
            sut = new SedApplication();
        }

        /**
         * [SINGLE]
         * Empty file should be tested once only.
         *
         * @throws Exception
         */
        @Test
        void emptyFileTest() throws Exception {
            content = new SedApplicationFileContent(0, 0, PATTERN, REPLACEMENT, 1);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, 1, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * [SINGLE]
         * Empty occurence should be tested once only.
         */
        @Test
        void emptyOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(10, 0, PATTERN, REPLACEMENT, 1);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, 1, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Single occurrence of pattern in a line.
         * @throws Exception
         */
        @Test
        void singleLineSingleOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(1, 1, PATTERN, REPLACEMENT, 1);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, 1, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Many occurrences of a pattern in a line.
         * @throws Exception
         */
        @Test
        void singleLineManyOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(1, 5, PATTERN, REPLACEMENT, 1);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, 1, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Replaces the N-th index in the single line.
         *
         * @throws Exception
         */
        @Test
        void singleLineManyOccurenceNthIndexTest() throws Exception {
            int index = 5;
            content = new SedApplicationFileContent(1, 10, PATTERN, REPLACEMENT, index);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, index, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Same test, but for many lines.
         *
         * @throws Exception
         */
        @Test
        void manyLineSingleOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(20, 1, PATTERN, REPLACEMENT, 1);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, 1, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Same test, but for many lines.
         *
         * @throws Exception
         */
        @Test
        void manyLineManyOccurenceTest() throws Exception {
            content = new SedApplicationFileContent(20, 10, PATTERN, REPLACEMENT, 1);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, 1, inputStream);
            assertEquals(content.getExpected(), result);
        }

        /**
         * Same test, but for many lines in the file.
         *
         * @throws Exception
         */
        @Test
        void manyLineManyOccurenceNthIndexTest() throws Exception {
            int index = 5;
            content = new SedApplicationFileContent(20, 10, PATTERN, REPLACEMENT, index);
            inputStream = content.createInputStream();

            String result = sut.replaceSubstringInStdin(PATTERN, REPLACEMENT, index, inputStream);
            assertEquals(content.getExpected(), result);
        }
    }

    /**
     * Test cases for testing replacement string syntax.
     */
    @Nested
    class SyntaxTests {
        private final static String INVALID_SYNTAX = "/abc/def/ghi";
        private final static String VALID_SYNTAX = "s/abc/def/";
        private final static String NO_TRAIL_SLASH = "s/abc/def";

        @Rule
        private final TemporaryFolder tempFolder = new TemporaryFolder();

        private final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        private final OutputStream outputStream = new ByteArrayOutputStream();
        private SedApplication sut;
        private SedApplicationFileContent content;

        @BeforeEach
        void setUp() throws Exception {
            tempFolder.create();
            Environment.currentDirectory = tempFolder.getRoot().getAbsolutePath();

            sut = new SedApplication();
            content = new SedApplicationFileContent(0, 0, PATTERN, REPLACEMENT, 1);
            content.createFile(tempFolder, FILENAME);
        }

        /**
         * Valid replacement string.
         */
        @Test
        void validReplacementSyntaxTest() {
            String[] args = { VALID_SYNTAX, FILENAME };
            try {
                sut.run(args, inputStream, outputStream);
            } catch (Exception e) {
                fail("Valid syntax should not fail test");
            }
        }

        /**
         * Replacement string with a missing trailing slash
         */
        @Test
        void missingTrailingSlashTest() {
            String[] args = { NO_TRAIL_SLASH, FILENAME };

            assertThrows(SedException.class, () -> {
                sut.run(args, inputStream, outputStream);
            });
        }

        /**
         * Totally incorrect replacement string
         */
        @Test
        void invalidReplacementSyntaxTest() {
            String[] args = { INVALID_SYNTAX, FILENAME };

            assertThrows(SedException.class, () -> {
                sut.run(args, inputStream, outputStream);
            });
        }
    }

    /**
     * Encapsulates contents of a single file to test `sed`.
     */
    class SedApplicationFileContent {
        private final static String FILLER = "abslkjdaglakbasdfj";
        private final int lines;
        private final int occurrences;
        private final int index;
        private final String pattern;
        private final String replacement;

        public SedApplicationFileContent(int lines, int occurrences, String pattern,
                                         String replacement, int index) {
            this.lines = lines;
            this.occurrences = occurrences;
            this.pattern = pattern;
            this.replacement = replacement;
            this.index = index;
        }

        public File createFile(TemporaryFolder tempFolder, String fileName) throws Exception {
            File file = tempFolder.newFile(fileName);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(getContent());

            bufferedWriter.close();
            fileWriter.close();

            file.setReadable(true);

            return file;
        }

        public InputStream createInputStream() {
            byte[] buffer = getContent().getBytes();
            return new ByteArrayInputStream(buffer);
        }

        public String getContent() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lines; i++) {
                builder.append(FILLER);
                for (int j = 0; j < occurrences; j++) {
                    builder.append(pattern)
                           .append(FILLER);
                }
                builder.append(StringUtils.STRING_NEWLINE);
            }

            return builder.toString();
        }

        public String getExpected() {
            if (index > occurrences) {
                return getContent();
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lines; i++) {
                builder.append(FILLER);
                for (int j = 0; j < occurrences; j++) {
                    if (j == index - 1) {
                        builder.append(replacement);
                    } else {
                        builder.append(pattern);
                    }
                    builder.append(FILLER);
                }
                builder.append(StringUtils.STRING_NEWLINE);
            }

            return builder.toString();
        }
    }
}
