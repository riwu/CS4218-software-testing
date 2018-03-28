package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class DiffApplicationTest {

    private static final String FILE_CONTENT = "Line 1" + System.lineSeparator() + "Line 2";
    private static final String DIR_NAME_ONE = "diffTestDir1";
    private static final String DIR_NAME_TWO = "diffTestDir2";
    private static final String RESOURCE_FOLDER = "testresource";
    private static final String FILENAME = "diffFile1";
    private static final String FILENAME1 = DIR_NAME_ONE + File.separator + FILENAME;
    private static final String FILENAME2 = DIR_NAME_TWO + File.separator + FILENAME;
    private static final String NO_OUTPUT = "";
    private static final String IDENTICAL_OUTPUT = "Files " + FILENAME1 + " and " + FILENAME2 + " are identical";
    private static final String FILE_KEYWORD = "Files ";
    private static final String AND_KEYWORD = " and ";
    private DiffApplication diffApplication;
    private String currentDir;
    private File file1;
    private File file2;
    private File diffTestDir1;
    private File diffTestDir2;
    private ByteArrayInputStream stdin;
    private ByteArrayOutputStream stdout;
    private boolean isShowSame, isNoBlank, isSimple;




    @Before
    public void setUp() throws Exception {
        currentDir = Environment.currentDirectory;
        diffApplication = new DiffApplication();
        diffTestDir1 = new File(currentDir + File.separator + DIR_NAME_ONE);
        diffTestDir2 = new File(currentDir + File.separator + DIR_NAME_TWO);
        diffTestDir1.mkdir();
        diffTestDir2.mkdir();

        file1 = new File(currentDir + File.separator + FILENAME1);
        file2 = new File(currentDir + File.separator + FILENAME2);
        Files.write(file1.toPath(), FILE_CONTENT.getBytes());
        Files.write(file2.toPath(), FILE_CONTENT.getBytes());
        isShowSame = true;
        isNoBlank = true;
        isSimple = true;
        stdin = new ByteArrayInputStream(FILE_CONTENT.getBytes());
        stdout = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        file1.delete();
        file2.delete();
        diffTestDir1.delete();
        diffTestDir2.delete();
    }


    @Test(expected=DiffException.class)
    public void whenNullStdoutExpectDiffException() throws Exception {
        String[] args = {FILENAME1, FILENAME2};
        diffApplication.run(args, null, null);
    }


    @Test(expected=DiffException.class)
    public void whenInsufficientArgExpectDiffException() throws Exception {
        String[] args = {FILENAME1};
        diffApplication.run(args, stdin, stdout);
    }
    
    @Test(expected=DiffException.class)
    public void whenStdinInArgButNullExpectDiffException() throws Exception {
        String[] args1 = {FILENAME1, "-"};
        diffApplication.run(args1, null, stdout);
    }

    @Test(expected=DiffException.class)
    public void whenDoubleStdinExpectDiffException() throws Exception {
        String[] args = {"-", "-"};
        diffApplication.run(args, stdin, stdout);
    }

    @Test(expected=DiffException.class)
    public void whenInvalidOptionExpectException() throws Exception {
        String[] args = {"-SB", FILENAME1, FILENAME2};
        diffApplication.run(args, null, stdout);
    }


    @Test
    public void whenSingleValidOptionExpectNoException() throws Exception {
        String[] args = {"-s", FILENAME1, FILENAME2};
        diffApplication.run(args, null, stdout);
        assertEquals(IDENTICAL_OUTPUT, stdout.toString());
    }
    
    @Test
    public void whenMultipleValidOptionExpectNoException() throws Exception {
        String[] args = {"-s", "-B", FILENAME1, FILENAME2};
        diffApplication.run(args, null, stdout);
        assertEquals(IDENTICAL_OUTPUT, stdout.toString());
    }
    
    @Test
    public void whenMultipleValidCombinedOptionExpectNoException() throws Exception {
        String[] args = {"-s", "-qB", FILENAME1, FILENAME2};
        diffApplication.run(args, null, stdout);
        assertEquals(IDENTICAL_OUTPUT, stdout.toString());
    }
    
    @Test
    public void whenMultipleValidRepeatedOptionExpectNoException() throws Exception {
        String[] args = {"-sq", "-qB", FILENAME1, FILENAME2};
        diffApplication.run(args, null, stdout);
        assertEquals(IDENTICAL_OUTPUT, stdout.toString());
    }

    @Test
    public void whenSameTwoFileNosFlagExpectEmptyString() throws Exception {
    	String expected = "";
        String output = diffApplication.diffTwoFiles(FILENAME1, FILENAME2,
                    !isShowSame, !isNoBlank, !isSimple);
        assertEquals(expected, output);
    }


    @Test
    public void whenSameTwoFileWithsFlagExpectIdenticalMsg() throws Exception {
        String output = diffApplication.diffTwoFiles(FILENAME1, FILENAME2,
                    isShowSame, isNoBlank, isSimple);
        assertEquals(IDENTICAL_OUTPUT, output);
    }


    @Test
    public void whenDiffTwoFileWithqFlagExpectDiffMsg() throws Exception {
        String expected = FILE_KEYWORD + FILENAME1 + " and " + FILENAME2 + " differ";
        String newFileContent = FILE_CONTENT + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());

        String output = diffApplication.diffTwoFiles(FILENAME1, FILENAME2,
                    isShowSame, !isNoBlank, isSimple);
        assertEquals(expected, output);
    }


    @Test
    public void whenDiffTwoFileWithBlankLineWithBFlagExpectNoOutput() throws Exception {
        String newFileContent = FILE_CONTENT + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffTwoFiles(FILENAME1, FILENAME2,
                    !isShowSame, isNoBlank, isSimple);
        assertEquals(NO_OUTPUT, output);
    }

    @Test
    public void whenDiffTwoFileWithSpaceWithBFlagExpectOutput() throws Exception {
        String newFileContent = FILE_CONTENT + " ";
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffTwoFiles(FILENAME1, FILENAME2,
                    !isShowSame, isNoBlank, !isSimple);
        String expected = "< Line 2 " + System.lineSeparator() + "> Line 2";
        assertEquals(expected, output);
    }

    @Test
    public void whenDiffFileWithBlankLineWithsBFlagExpectIdentical() throws Exception {
        String newFileContent = FILE_CONTENT + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffTwoFiles(FILENAME1, FILENAME2,
                    isShowSame, isNoBlank, !isSimple);
        assertEquals(IDENTICAL_OUTPUT, output);
    }


    @Test
    public void whenDiffBinaryFileExpectBinaryDiffOutput() throws Exception {
        String binaryFile1 = RESOURCE_FOLDER + File.separator + "example.jpg";
        String binaryFile2 = RESOURCE_FOLDER + File.separator + "example2.jpg";
        String expected = "Binary files " + binaryFile1 + AND_KEYWORD + binaryFile2 + " differ";
        String output = diffApplication.diffTwoFiles(binaryFile1, binaryFile2,
                    !isShowSame, !isNoBlank, !isSimple);
        assertEquals(expected, output);
    }

    @Test
    public void whenSameBinaryFileWithsFlagExpectBinarySameOutput() throws Exception {
        String binaryFile1 = RESOURCE_FOLDER + File.separator + "example.jpg";
        String binaryFile2 = RESOURCE_FOLDER + File.separator + "example3.jpg";
        String expected = FILE_KEYWORD + binaryFile1 + AND_KEYWORD + binaryFile2 + " are identical";
        String output = diffApplication.diffTwoFiles(binaryFile1, binaryFile2,
                    isShowSame, !isNoBlank, !isSimple);

        assertEquals(expected, output);
    }

    @Test
    public void whenSameDirExpectNoOutput() throws Exception {
        String output = diffApplication.diffTwoDir(DIR_NAME_ONE, DIR_NAME_TWO,
                    !isShowSame, !isNoBlank, !isSimple);
        assertEquals("", output);
    }


    @Test
    public void whenDirWithSameSubDirExpectCommonDirOutputNoRecursive() throws Exception {
        String subDirName = "subDirTest";
        File subDir1 = new File(currentDir + File.separator + DIR_NAME_ONE + File.separator + subDirName);
        File subDir2 = new File(currentDir + File.separator + DIR_NAME_TWO + File.separator + subDirName);
        subDir1.mkdir();
        subDir2.mkdir();
        String subSubDirName = "subSubDir";
        File subSubDir = new File(currentDir + File.separator + DIR_NAME_ONE + File.separator + subDirName + File.separator + subSubDirName);
        subSubDir.mkdir();
        String expected = "Common subdirectories: " + DIR_NAME_ONE + File.separator + subDirName + AND_KEYWORD +
                        DIR_NAME_TWO + File.separator + subDirName;

        String output = diffApplication.diffTwoDir(DIR_NAME_ONE, DIR_NAME_TWO,
                    !isShowSame, !isNoBlank, !isSimple);
        subSubDir.delete();
        subDir1.delete();
        subDir2.delete();
        assertEquals(expected, output);

    }

    @Test
    public void whenDirWithExtraFileExpectOnlyInAOutput() throws Exception {
        String extraFileName = "extraFile";
        File extraFile = new File(diffTestDir1 + File.separator + extraFileName);
        extraFile.createNewFile();
        String expected = "Only in " + diffTestDir1.getName() + ": " + extraFileName;
        String output = diffApplication.diffTwoDir(DIR_NAME_ONE, DIR_NAME_TWO,
                    !isShowSame, !isNoBlank, !isSimple);
        extraFile.delete();
        assertEquals(expected, output);

    }


    @Test
    public void whenDirsWithDiffFileExpectDiffOutput() throws Exception {
        String newFileContent = FILE_CONTENT + " extra byte";
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffTwoDir(DIR_NAME_ONE, DIR_NAME_TWO,
                    !isShowSame, isNoBlank, !isSimple);

        String expected = "diff " + DIR_NAME_ONE + File.separator + file1.getName() + " " +
                        DIR_NAME_TWO + File.separator + file2.getName() + System.lineSeparator() +
                        "< Line 2 extra byte" + System.lineSeparator() + "> Line 2";
        assertEquals(expected, output);
    }

    @Test
    public void whenDirsWithDiffFileWithqFlagExpectDiffOutput() throws Exception {
        String newFileContent = FILE_CONTENT + " extra byte";
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffTwoDir(DIR_NAME_ONE, DIR_NAME_TWO,
                    !isShowSame, !isNoBlank, isSimple);
        
        String expected = FILE_KEYWORD + DIR_NAME_ONE + File.separator + file1.getName() + AND_KEYWORD +
                DIR_NAME_TWO + File.separator + file2.getName() + " differ";
        assertEquals(expected, output);
    }

    @Test
    public void whenDiffTwoFileWithSpaceWithBFlagExpectOutputStdin() throws Exception {
        String newFileContent = FILE_CONTENT + " ";
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffFileAndStdin(FILENAME1, stdin,
                !isShowSame, isNoBlank, !isSimple);
        String expected = "< Line 2 " + System.lineSeparator() + "> Line 2";
        assertEquals(expected, output);
    }

    @Test
    public void whenDiffTwoFileWithBlankWithsBFlagExpectIdenticalStdin() throws Exception {
        String newFileContent = FILE_CONTENT + System.lineSeparator() + System.lineSeparator();
        Files.write(file1.toPath(), newFileContent.getBytes());
        String output = diffApplication.diffFileAndStdin(FILENAME1, stdin,
                isShowSame, isNoBlank, !isSimple);
        String expected = FILE_KEYWORD + FILENAME1 + " and - are identical";
        assertEquals(expected, output);
    }
}
