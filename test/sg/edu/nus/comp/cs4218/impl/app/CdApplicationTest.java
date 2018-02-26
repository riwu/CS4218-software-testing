package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CdApplicationTest {

    private CdApplication cdApplication = new CdApplication();
    private static String initial_dir;
    private File testDir = null;
    private String currentDir = "";
    private String testDirName = "testCdDir";

    @Before
    public void setUp() throws Exception {
        initial_dir = Environment.currentDirectory;
        currentDir = initial_dir;
        testDir = new File(currentDir + File.separator + testDirName);
        testDir.mkdir();
    }

    @After
    public void tearDown() throws Exception {
        testDir.delete();
    }

    @Test
    public void Should_ThrowCdException_When_NoPath() throws AbstractApplicationException {
        boolean thrownCdException = false;

        try {
            cdApplication.run(null, null, System.out);
        } catch (CdException e) {
            thrownCdException = true;
        } catch (NullPointerException e) {
            thrownCdException = false;
        }
        assertTrue(thrownCdException);
    }

    @Test
    public void Should_ThrowCdException_When_NoOutputSteam() throws AbstractApplicationException {
        boolean thrownCdException = false;

        String[] args = {testDirName};

        try {
            cdApplication.run(args, null, null);
        } catch (CdException e) {
            thrownCdException = true;
        }

        assertTrue(thrownCdException);
    }

    @Test
    public void Should_ThrowCdException_When_MultipleInputPath() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {testDirName, "extraPath"};

        try {
            cdApplication.run(args, null, System.out);
        } catch (CdException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);
    }

    @Test
    public void Should_ThrowCdException_When_InvalidPath() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {"InvalidCdPath"};

        try {
            cdApplication.run(args, null, System.out);
        } catch (CdException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);
    }


    @Test
    public void Should_ThrowCdException_When_FilePath() throws AbstractApplicationException {
        boolean thrownCdException = false;
        File f = new File(currentDir + File.separator + testDirName + File.separator + "newfile");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args = {testDirName + File.separator + "newfile"};
        try {
            cdApplication.run(args, null, System.out);
        } catch (CdException e) {
            f.delete();
            thrownCdException = true;
        }
        if (f.exists()) {
            f.delete();
        }
        assertTrue(thrownCdException);
    }

    @Test
    public void When_ValidPath_Expect_ChangeOfDirectory() throws AbstractApplicationException {
        String expectedDir = initial_dir + File.pathSeparator + testDirName;
        String[] args = {testDirName};

        try {
            cdApplication.run(args, null, System.out);
        } catch (CdException e) {
            throw e;
        }
        currentDir = Environment.currentDirectory;
        assertEquals(expectedDir, currentDir);
    }
}
