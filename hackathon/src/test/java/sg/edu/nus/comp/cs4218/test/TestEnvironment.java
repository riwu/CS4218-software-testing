package sg.edu.nus.comp.cs4218.test;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Base test class used to initialize test environments. It copies all files, contents,
 * and directories from ENV_ROOT in the resources folder into a TemporaryFolder which
 * will be cleaned up by JUnit after each test is complete.
 *
 * This reduces the amount of boilerplate code required for each test case.
 */
@EnableRuleMigrationSupport
public class TestEnvironment {
    // Can be overriden if you want a test environment different from the original one.
    protected final static String ENV_ROOT = "env";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Reinitializes the user environment on each test case.
     */
    @BeforeEach
    public void setUp() throws Exception {
        initializeTempFolder();

        Path src = Paths.get(getClass().getClassLoader().getResource(ENV_ROOT).toURI());
        Path dest = Paths.get(tempFolder.getRoot().getAbsolutePath(), ENV_ROOT);
        initializeEnvironment(src, dest);

        setUserCwd();
    }

    private void initializeTempFolder() throws IOException {
        tempFolder.create();
        tempFolder.newFolder(ENV_ROOT);
    }

    /**
     * Initializes the content of temporary folder with contents found in a particular directory.
     * We do this by creating all files in the current directory, and do the same for all
     * subdirectories in the current directory recursively.
     *
     * NOTE: There are libraries that can help us out, but the default Java 8 library doesn't have
     * all the goodies. If there's a better way to do this, we should refactor. :)
     *
     * @param src - Path of the directory where the content should come from.
     * @param dest - Path of the directory to copy the content to.
     */
    private void initializeEnvironment(Path src, Path dest) throws Exception {
        File srcDirectory = src.toFile();
        File destDirectory = dest.toFile();

        for (File file : srcDirectory.listFiles()) {
            if (file.isDirectory()) {
                // Make the directory
                try {
                    Path newDir = Paths.get(destDirectory.getAbsolutePath(), file.getName());
                    Files.createDirectory(newDir);
                } catch (Exception e) {
                    throw new Exception(String.format("%s not made.", file.getName()), e);
                }

                // RECURSE
                Path newSrc = file.toPath();
                Path newDest = Paths.get(destDirectory.getAbsolutePath(), file.getName());
                initializeEnvironment(newSrc, newDest);
            } else {
                // Copy file to new directory
                Path newFile = Paths.get(destDirectory.getAbsolutePath(), file.getName());
                Files.copy(file.toPath(), newFile);
            }
        }
    }

    /**
     * Resets the user current working directory (cwd) to the root of the temporary folder.
     */
    private void setUserCwd() {
        String root = tempFolder.getRoot().getAbsolutePath();
        Environment.currentDirectory = Paths.get(root, ENV_ROOT)
                                            .toAbsolutePath()
                                            .toString();
    }
}
