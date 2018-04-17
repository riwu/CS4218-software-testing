package sg.edu.nus.comp.cs4218.test;

import org.junit.Rule;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.test.stub.ApplicationRunnerStub;

import java.io.*;

@EnableRuleMigrationSupport
public class FileSystemTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Creates a new test file in the temporary folder with specified contents.
     *
     * @param fileName
     * @param testString
     * @param readable
     *
     * @return File that was created.
     *
     * @throws IOException
     */
    protected File createTestFileWithContents(String fileName, String testString, boolean readable)
            throws IOException {
        File file = tempFolder.newFile(fileName);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(testString);

        bufferedWriter.close();
        fileWriter.close();

        file.setReadable(readable);

        return file;
    }

    /**
     * Creates a readable test file with the specified contents.
     */
    protected File createTestFileWithContents(String fileName, String testString)
            throws IOException {
        return createTestFileWithContents(fileName, testString, true);
    }

    /**
     * Reads the contents of a test file.
     *
     * @param fileName
     *
     * @return Contents of the specified test file.
     *
     * @throws ShellException
     */
    protected String getContentsFromTestFile(String fileName) throws ShellException {
        InputStream inputStream = IOUtils.openInputStream(fileName);
        return ApplicationRunnerStub.getStringFromInputStream(inputStream);
    }
}
