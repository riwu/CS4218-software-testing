package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SplitException;

import java.io.File;

public class SplitApplicationTest {
    private SplitApplication splitApplication = new SplitApplication();
    private static final String DEFAULT_DIR = Environment.currentDirectory;
    private static final String CURRENT_DIR = Environment.currentDirectory + File.separator + "SplitApplicationTest";
    private static final String RESOURCE_FOLDER = "testresource";
    private static final String FILENAME = CURRENT_DIR + File.separator + "newfile";
    private File testDir;
    private File file;

    @Before
    public void setUp() throws Exception {
        Environment.currentDirectory = CURRENT_DIR;
        testDir = new File(CURRENT_DIR);
        testDir.mkdir();
        file = new File(FILENAME);
        file.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        for (File file : testDir.listFiles()) {
            file.delete();
        }
        testDir.delete();
        Environment.currentDirectory = DEFAULT_DIR;
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenZeroByteOption() throws Exception {
        String[] args = {"-b", "0", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenZeroByteWithModifierOption() throws Exception {
        String[] args = {"-b", "0b", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenZeroLineOption() throws Exception {
        String[] args = {"-l", "0", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNegativeByteOption() throws Exception {
        String[] args = {"-b", "-1", FILENAME};
        splitApplication.run(args, null, System.out);
    }

    @Test(expected = SplitException.class)
    public void shouldThrowSplitExceptionWhenNegativeLineOption() throws Exception {
        String[] args = {"-l", "-1", FILENAME};
        splitApplication.run(args, null, System.out);
    }
}
