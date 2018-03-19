package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class CdApplicationTest {

    private CdApplication cdApplication = new CdApplication();
    private static final String INITIAL_DIR = System.getProperty("user.dir");
    private static final String RESOURCE_FOLDER = "testCdDir";
    private File testDir = null;
    private String currentDir = "";

    @Before
    public void setUp() throws Exception {
        currentDir = INITIAL_DIR;
        testDir = new File(currentDir + File.separator + RESOURCE_FOLDER);
        testDir.mkdir();
    }

    @After
    public void tearDown() throws Exception {
    	Environment.currentDirectory = INITIAL_DIR;
        testDir.deleteOnExit();
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenInvalidPath() throws Exception {
        String[] args = {"InvalidCdPath"};
        cdApplication.run(args, null, System.out);
    }

    @Test(expected = CdException.class)
    public void shouldThrowCdExceptionWhenFilePath() throws Exception {
        File file = new File(currentDir + File.separator + RESOURCE_FOLDER + File.separator + "newfile");
        file.createNewFile();
        file.deleteOnExit();
        String[] args = {RESOURCE_FOLDER + File.separator + "newfile"};
        cdApplication.run(args, null, System.out);
    }
    
    @Test(expected = CdException.class)
    public void shouldThrowExceptionWhenMultipleSpacesArg() throws Exception {
        String[] args = {"      "};
        cdApplication.run(args, null, System.out);
    }
    
    @Test
    public void whenNullArgsExpectNoChange() throws Exception {
        String expectedDir = INITIAL_DIR;
        cdApplication.run(null, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }

    
    @Test
    public void whenNoArgsExpectNoChange() throws Exception {
        String expectedDir = INITIAL_DIR;
        String[] args = {};
        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }

    @Test
    public void whenValidAbsolutePathExpectChangeOfDirectory() throws Exception {
        String expectedDir = INITIAL_DIR + File.separator + RESOURCE_FOLDER;
        String[] args = {RESOURCE_FOLDER};

        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }
    
    @Test
    public void whenSamePathExpectNoChange() throws Exception {
        String expectedDir = currentDir;
        String[] args = {currentDir};

        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }
    
    @Test
    public void whenSameRelativePathExpectNoChange() throws Exception {
        String expectedDir = currentDir;
        String[] args = {"."};

        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }
    
    @Test
    public void whenParentRelativePathExpectParentDirectory() throws Exception {
    	File file = new File(currentDir);
        String expectedDir = file.getParent();
        String[] args = {".."};
        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }
    
    @Test
    public void whenRelativePathToCurrentDirectoryExpectNoChange() throws Exception {
    	File file = new File(currentDir);
    	String expectedDir = currentDir;
        String pathName = ".." + File.separator + file.getName();
        String[] args = {pathName};
        cdApplication.run(args, null, System.out);
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }
    
    @Test
    public void shouldChangeToFirstArgWhenMultipleArgs() throws Exception {
    	String expectedDir = testDir.getCanonicalPath();
    	String[] args = {RESOURCE_FOLDER, "extraPath"};
    	cdApplication.run(args, null, null);
    	currentDir = Environment.currentDirectory;
    	assertEquals(expectedDir, currentDir);
    }
}