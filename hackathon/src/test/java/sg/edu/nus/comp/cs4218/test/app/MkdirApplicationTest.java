package sg.edu.nus.comp.cs4218.test.app;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import sg.edu.nus.comp.cs4218.impl.app.MkdirApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@EnableRuleMigrationSupport
class MkdirApplicationTest {
    private static final String FOLDER_NAMES[] = { "folder0", "folder1", "folder2" };
    private static final String ERR_MSG = "Folders should be created";

    private MkdirApplication sut;
    private Path relativePaths[] = new Path[FOLDER_NAMES.length];
    private Path absolutePaths[] = new Path[FOLDER_NAMES.length];

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    public void setUp() {
        sut = new MkdirApplication();
        // temporary folder rule runs before setup
        Path tempPath = tempFolder.getRoot().toPath();

        for (int i = 0; i < FOLDER_NAMES.length; ++i) {
            relativePaths[i] = tempPath.resolve(FOLDER_NAMES[i]);
            absolutePaths[i] = tempPath.toAbsolutePath().resolve(FOLDER_NAMES[i]);
        }
    }

    @Test
    public void createFolderInCurrentDirWithAbsolutePath() {
        try {
            sut.createFolder(absolutePaths[0].toString());
        } catch (Exception e) {
            fail(ERR_MSG);
        }

        assertTrue(absolutePaths[0].toFile().isDirectory());
        assertTrue(Files.exists(absolutePaths[0]));
        assertEquals(1, tempFolder.getRoot().list().length);
    }

    @Test
    public void createFolderInCurrentDirWithRelativePath() {
        try {
            sut.createFolder(relativePaths[0].toString());
        } catch (Exception e) {
            fail(ERR_MSG);
        }

        assertTrue(relativePaths[0].toFile().isDirectory());
        assertTrue(Files.exists(relativePaths[0]));
        assertEquals(1, tempFolder.getRoot().list().length);
    }

    // Create multiple non existent folders
    @Test
    public void createMultipleFoldersInCurrentDir() {
        try {
            sut.createFolder(relativePaths[0].toString(), relativePaths[1].toString());
        } catch (Exception e) {
            fail(ERR_MSG);
        }

        assertTrue(Files.exists(relativePaths[0]));
        assertTrue(Files.exists(relativePaths[1]));
        assertEquals(2, tempFolder.getRoot().list().length);
    }

    // Create existing folder should not overwrite
    @Test
    public void createExistingFolder() throws Exception {
        String parentFolder = FOLDER_NAMES[0];
        String childFolder = FOLDER_NAMES[1];

        Path parentPath = tempFolder.getRoot().toPath().resolve(parentFolder);
        Path childPath = tempFolder.newFolder(parentFolder, childFolder).toPath();

        assertThrows(Exception.class, () -> {
            sut.createFolder(parentPath.toString());
        });

        assertTrue(Files.exists(childPath));
        assertEquals(1, tempFolder.getRoot().list().length);
    }

    @Test
    public void createNestedFolderWithNonExistentParent() {
        String parentFolder = FOLDER_NAMES[0];
        String childFolder = FOLDER_NAMES[1];

        Path childPath = tempFolder.getRoot().toPath().resolve(parentFolder).resolve(childFolder);

        assertThrows(Exception.class, () -> {
           sut.createFolder(childPath.toString());
        });
        assertEquals(0, tempFolder.getRoot().list().length);
    }

    // should get created but complain subsequent creations
    @Test
    public void createDuplicateFolders() {
        assertThrows(Exception.class, () -> {
            sut.createFolder(relativePaths[0].toString(), relativePaths[0].toString(), relativePaths[1].toString());
        });

        assertTrue(Files.exists(relativePaths[0]));
        assertTrue(Files.exists(relativePaths[1]));
        assertEquals(2, tempFolder.getRoot().list().length);
    }

    @Test
    public void createDuplicateFoldersWithDifferentPathStyle() {
        assertThrows(Exception.class, () -> {
            sut.createFolder(relativePaths[0].toString(), absolutePaths[0].toString(), relativePaths[1].toString());
        });

        assertTrue(Files.exists(relativePaths[0]));
        assertTrue(Files.exists(relativePaths[1]));
        assertEquals(2, tempFolder.getRoot().list().length);
    }

    // Folders with padded spaces names are considered unique
    @Test
    public void createFoldersWithPaddedSpaces() {
        String folderName = FOLDER_NAMES[0];
        String paddedFolderName = FOLDER_NAMES[0] + "   ";
        Path tempFolderPath = tempFolder.getRoot().toPath();

        Path folderPath = tempFolderPath.resolve(folderName);
        Path paddedFolderPath = tempFolderPath.resolve(paddedFolderName);

        try {
            sut.createFolder(folderPath.toString(), paddedFolderPath.toString());
        } catch (Exception e) {
            fail(ERR_MSG);
        }

        assertTrue(Files.exists(folderPath));
        assertTrue(Files.exists(paddedFolderPath));
        assertEquals(2, tempFolder.getRoot().list().length);
    }

    // Created directory permissions should be same as the group the user belongs to
    @Test
    public void foldersShouldHaveCorrectPermissions() throws Exception {
        try {
            sut.createFolder(relativePaths[0].toString());
        } catch (Exception e) {
            fail(ERR_MSG);
        }

        Set<PosixFilePermission> folderPermissions = Files.getPosixFilePermissions(relativePaths[0]);
        Set<PosixFilePermission> expectedRights =
                Files.getPosixFilePermissions(tempFolder.getRoot().toPath());

        assertTrue(folderPermissions.equals(expectedRights));
    }
}