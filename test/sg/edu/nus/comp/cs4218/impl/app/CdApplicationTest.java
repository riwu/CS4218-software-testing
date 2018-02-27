package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class CdApplicationTest {

    private CdApplication cdApplication = new CdApplication();
    private static final String INITIAL_DIR = Environment.currentDirectory;
    private static final String RESOURCE_FOLDER = "testCdDir";
    private File testDir = null;
    private String currentDir = "";
    boolean isImplemented = false;

    @Before
    public void setUp() throws Exception {
        currentDir = INITIAL_DIR;
        testDir = new File(currentDir + File.separator + RESOURCE_FOLDER);
        testDir.mkdir();
    }

    @After
    public void tearDown() throws Exception {
        testDir.delete();
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenNoPath() throws AbstractApplicationException {
        Assume.assumeTrue(isImplemented);
        cdApplication.run(null, null, System.out);
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenNoOutputSteam() throws AbstractApplicationException {
        Assume.assumeTrue(isImplemented);
        String[] args = {RESOURCE_FOLDER};
        cdApplication.run(args, null, null);
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenMultipleInputPath() throws AbstractApplicationException {
        Assume.assumeTrue(isImplemented);
        String[] args = {RESOURCE_FOLDER, "extraPath"};
        cdApplication.run(args, null, System.out);
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenInvalidPath() throws Exception {
        Assume.assumeTrue(isImplemented);
        String[] args = {"InvalidCdPath"};
        cdApplication.run(args, null, System.out);
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenFilePath() throws Exception {
        Assume.assumeTrue(isImplemented);
        File file = new File(currentDir + File.separator + RESOURCE_FOLDER + File.separator + "newfile");
        file.createNewFile();
        String[] args = {RESOURCE_FOLDER + File.separator + "newfile"};
        try {
            cdApplication.run(args, null, System.out);
        } catch (CdException e) {
            file.delete();
        }
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void whenValidPathExpectChangeOfDirectory() throws Exception {
        Assume.assumeTrue(isImplemented);
        String expectedDir = INITIAL_DIR + File.pathSeparator + RESOURCE_FOLDER;
        String[] args = {RESOURCE_FOLDER};

        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
        
        cdApplication.changeToDirectory(INITIAL_DIR);
        currentDir = Environment.currentDirectory;
        assertEquals(INITIAL_DIR, currentDir);

    }
}