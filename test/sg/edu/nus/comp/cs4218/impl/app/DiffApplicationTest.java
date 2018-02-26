package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class DiffApplicationTest {

    private DiffApplication diffApplication = new DiffApplication();
    private String currentDir;
    private String fileContent;
    private String diffTestDirName1 = "diffTestDir1";
    private String diffTestDirName2 = "diffTestDir2";
    private String filename = "diffFile1";
    private String filename1 = diffTestDirName1 + File.separator + filename;
    private String filename2 = diffTestDirName2 + File.separator + filename;
    private File file1;
    private File file2;
    private File diffTestDir1;
    private File diffTestDir2;

    @Before
    public void setUp() throws Exception {
        currentDir = Environment.currentDirectory;
        diffTestDir1 = new File(currentDir + File.separator + diffTestDirName1);
        diffTestDir2 = new File(currentDir + File.separator + diffTestDirName2);
        diffTestDir1.mkdir();
        diffTestDir2.mkdir();

        file1 = new File(currentDir + File.separator + filename1);
        file2 = new File(currentDir + File.separator +  filename2);
        fileContent = "Line 1" + System.lineSeparator() + "Line 2";
        Files.write(file1.toPath(), fileContent.getBytes());
        Files.write(file2.toPath(), fileContent.getBytes());
    }

    @After
    public void tearDown() throws Exception {
        file1.delete();
        file2.delete();
        diffTestDir1.delete();
        diffTestDir2.delete();
    }


    @Test
    public void When_NullStdout_Expect_DiffException() throws Exception {
        String[] args = {filename1, filename2};
        boolean diffException = false;

        try {
            diffApplication.run(args, null, null);
        } catch (DiffException e) {
            diffException = true;
        } catch (NullPointerException e) {
            diffException = false;
        }
        Assert.assertTrue(diffException);
    }


    @Test
    public void When_InsufficientArg_Expect_DiffException() throws Exception {
        String[] args = {filename1};
        boolean diffException = false;

        try {
            diffApplication.run(args, System.in, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);


        String[] args1 = {filename1, "-"};
        diffException = false;

        try {
            diffApplication.run(args1, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);
    }

    @Test
    public void When_DoubleStdin_Expect_DiffException() throws Exception {
        String[] args = {"-", "-"};
        boolean diffException = false;

        try {
            diffApplication.run(args, System.in, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);
    }

    @Test
    public void When_ValidOption_Expect_NoException() throws Exception {
        String[] args = {"-s", filename1, filename2};
        boolean diffException = false;

        try {
            diffApplication.run(args, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertFalse(diffException);


        String[] args1 = {"-sBq", filename1, filename2};
        diffException = false;
        try {
            diffApplication.run(args1, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertFalse(diffException);


        String[] args2 = {filename1, "-sB", filename2};
        diffException = false;
        try {
            diffApplication.run(args2, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertFalse(diffException);


        String[] args3 = {"-sB", "-q", filename1, filename2};
        diffException = false;
        try {
            diffApplication.run(args3, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertFalse(diffException);
    }

    @Test
    public void When_InvalidOption_Expect_Exception() throws Exception {
        String[] args = {"-a", filename1, filename2};
        boolean diffException = false;

        try {
            diffApplication.run(args, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);


        String[] args1 = {"s", filename1, filename2};
        diffException = false;
        try {
            diffApplication.run(args1, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);


        String[] args2 = {"-S", filename1, filename2};
        diffException = false;
        try {
            diffApplication.run(args2, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);


        String[] args3 = {"-b", filename1, filename2};
        diffException = false;
        try {
            diffApplication.run(args3, null, System.out);
        } catch (DiffException e) {
            diffException = true;
        }
        Assert.assertTrue(diffException);
    }


    @Test
    public void When_SameTwoFile_Expect_NoOutput() throws Exception {
        String output = "-1";
        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("", output);


        output = "-1";
        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("", output);


        output = "-1";
        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("", output);


        output = "-1";
        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("", output);
    }


    @Test
    public void When_SameTwoFileWithsFlag_Expect_IdenticalOutput() throws Exception {
        String expected = "Files " + filename1 + " " + filename2 + " are identical";
        String output = "";
        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    true, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);


        output = "";
        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    true, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);


        output = "";
        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    true, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);


        output = "";
        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    true, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);
    }


    @Test
    public void When_DiffTwoFileWithqFlag_Expect_DiffOutput() throws Exception {
        String expected =  "Files " + filename1 + " " + filename2 + " differ";
        String output = "-1";
        String newFileContent = fileContent + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);


        newFileContent = fileContent + System.lineSeparator() + "Line 3";
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);
    }


    @Test
    public void When_DiffTwoFileWithBlankLineWithBFlag_Expect_NoOutput() throws Exception {
        String output = "-1";
        String newFileContent = fileContent + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("", output);


        output = "-1";
        newFileContent = fileContent + System.lineSeparator() + "   " + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoFiles(filename1 , filename2,
                    false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("", output);
    }

    @Test
    public void When_DiffTwoFileWithSpaceWithBFlag_Expect_Output() throws Exception {
        String output = "";
        String newFileContent = fileContent + " ";
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String expected = "< Line 2 " + System.lineSeparator() + "> Line 2";
        assertEquals(expected, output);
    }

    @Test
    public void When_DiffFileWithBlankLineWithsBFlag_Expect_Identical() throws Exception {
        String expected = "Files " + filename1 + " " + filename2 + " are identical";
        String output = "";
        String newFileContent = fileContent + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    true, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);


        output = "";
        try {
            output = diffApplication.diffTwoFiles(filename1, filename2,
                    true, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, output);
    }


    @Test
    public void When_DiffBinaryFile_Expect_BinaryDiffOutput() throws Exception {
        String binaryFile1 = "testresource" + File.separator + "example.jpg";
        String binaryFile2 = "testresource" + File.separator + "example2.jpg";
        String expected = "Binary files " + binaryFile1 + " and " + binaryFile2 + " differ";
        String output = "";

        try {
            output = diffApplication.diffTwoFiles(binaryFile1, binaryFile2,
                    false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(expected, output);
    }

    @Test
    public void When_SameBinaryFileWithsFlag_Expect_BinarySameOutput() throws Exception {
        String binaryFile1 = "testresource" + File.separator + "example.jpg";
        String binaryFile2 = "testresource" + File.separator + "example3.jpg";
        String expected = "Binary files " + binaryFile1 + " and " + binaryFile2 + " are identical";
        String output = "";

        try {
            output = diffApplication.diffTwoFiles(binaryFile1, binaryFile2,
                    true, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(expected, output);
    }

    @Test
    public void When_SameDir_Expect_NoOutput() throws Exception {
        String output = "-1";

        try {
            output = diffApplication.diffTwoDir(diffTestDirName1, diffTestDirName2,
                    false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("", output);
    }


    @Test
    public void When_DirWithSameSubDir_Expect_CommonDirOutputNoRecusive() throws Exception {
        String output = "";
        String subDirName = "subDirTest";
        File subDir1 = new File(currentDir + File.separator + diffTestDirName1 + File.separator + subDirName);
        File subDir2 = new File(currentDir + File.separator + diffTestDirName2 + File.separator + subDirName);
        subDir1.mkdir();
        subDir2.mkdir();
        String subSubDirName = "subSubDir";
        File subSubDir = new File(currentDir + File.separator + diffTestDirName1 + File.separator + subDirName + File.separator + subSubDirName);
        subSubDir.mkdir();
        String expected = "Common subdirectories: " + diffTestDirName1 + File.separator + subDirName + " and " +
                        diffTestDirName2 + File.separator + subDirName;


        try {
            output = diffApplication.diffTwoDir(diffTestDirName1, diffTestDirName2,
                    false, false, false);
        } catch (Exception e) {
            subSubDir.delete();
            subDir1.delete();
            subDir2.delete();
            e.printStackTrace();
        }

        subSubDir.delete();
        subDir1.delete();
        subDir2.delete();
        assertEquals(expected, output);

    }

    @Test
    public void When_DirWithExtraFile_Expect_OnlyInAOutput() throws Exception {
        String output = "";
        String extraFileName = "extraFile";
        File extraFile = new File(diffTestDir1 + File.separator + extraFileName);
        extraFile.createNewFile();

        String expected = "Only in " + diffTestDir1.getName() + ": " + extraFileName;

        try {
            output = diffApplication.diffTwoDir(diffTestDirName1, diffTestDirName2,
                    false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        extraFile.delete();
        assertEquals(expected, output);

    }


    @Test
    public void When_DirsWithDiffFile_Expect_DiffOutput() throws Exception {
        String output = "";
        String newFileContent = fileContent + " extra byte";
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoDir(diffTestDirName1, diffTestDirName2,
                    false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String expected = "diff " + diffTestDirName1 + File.separator + file1.getName() + " " +
                        diffTestDirName2 + File.separator + file2.getName() + System.lineSeparator() +
                        "< Line 2 extra byte" + System.lineSeparator() + "> Line 2";
        assertEquals(expected, output);
    }

    @Test
    public void When_DirsWithDiffFileWithqFlag_Expect_DiffOutput() throws Exception {
        String output = "";
        String newFileContent = fileContent + " extra byte";
        Files.write(file1.toPath(), newFileContent.getBytes());

        try {
            output = diffApplication.diffTwoDir(diffTestDirName1, diffTestDirName2,
                    false, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String expected = "Files " + diffTestDirName1 + File.separator + file1.getName() + " " +
                diffTestDirName2 + File.separator + file2.getName() + " differ";
        assertEquals(expected, output);
    }
}
