package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SplitException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SplitApplicationTest {

    private SplitApplication splitApplication = new SplitApplication();
    private static String initial_dir;
    private File testDir = null;
    private File file = null;
    private String currentDir = "";
    private String testDirName = "testCdDir";
    private String filename = "";

    @Before
    public void setUp() throws Exception {
        initial_dir = Environment.currentDirectory;
        currentDir = initial_dir;
        testDir = new File(currentDir + File.separator + testDirName);
        testDir.mkdir();

        filename = currentDir + File.separator + testDirName + File.separator + "newfile";
        file = new File(filename);
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
        testDir.delete();
    }

    private String generateString(int lines) {
        String s = "a" + System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<lines; i++) {
            sb.append(s);
        }

        return sb.toString().trim();
    }

    private String generateStringByte(int bytes) {
        String s = "a";
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<bytes; i++) {
            sb.append(s);
        }

        return sb.toString();
    }


    @Test
    public void Should_ThrowSplitException_When_NoStdout() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {filename};

        try {
            splitApplication.run(args, null, null);
        } catch (SplitException e) {
            thrownCdException = true;
        } catch (NullPointerException e) {
            thrownCdException = false;
        }
        assertTrue(thrownCdException);
    }


    @Test
    public void Should_ThrowSplitException_When_NoInput() throws AbstractApplicationException {
        boolean thrownCdException = false;

        try {
            splitApplication.run(null, null, null);
        } catch (SplitException e) {
            thrownCdException = true;
        } catch (NullPointerException e) {
            thrownCdException = false;
        }
        assertTrue(thrownCdException);
    }


    @Test
    public void Should_ThrowSplitException_When_InputDir() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {testDirName};

        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        } catch (Exception e) {
            thrownCdException = false;
        }
        assertTrue(thrownCdException);
    }


    @Test
    public void Should_ThrowSplitException_When_InvalidOption() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {"-L", filename};

        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);

        thrownCdException = false;
        String[] args1 = {"-B", filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);

        thrownCdException = false;
        String[] args2 = {"-lb", filename};
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);
    }



    @Test
    public void Should_ThrowSplitException_When_InvalidOptionArgument() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {"-l", filename};

        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args1 = {"-b", filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args2 = {"-l", "x", filename};
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args3 = {"-l", "x", filename};
        try {
            splitApplication.run(args3, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args4 = {"-b", "1g", filename};
        try {
            splitApplication.run(args4, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args5 = {"-b", "1M", filename};
        try {
            splitApplication.run(args5, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);
    }



    @Test
    public void Should_ThrowSplitException_When_ZeroByteOption() throws AbstractApplicationException {
        boolean thrownCdException = false;
        String[] args = {"-b", "0", filename};

        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args1 = {"-b", "0b", filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);


        thrownCdException = false;
        String[] args2 = {"-b", "0k", filename};
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);

        thrownCdException = false;
        String[] args3 = {"-b", "0m", filename};
        try {
            splitApplication.run(args3, null, System.out);
        } catch (SplitException e) {
            thrownCdException = true;
        }
        assertTrue(thrownCdException);
    }



    @Test
    public void Should_SplitInto1Files_When_InputLineLessThan1001() throws Exception {
        String inputString = generateString(1);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] args = {filename};
        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        File firstFile = new File(currentDir + File.separator + testDirName + File.separator + "xaa");
        File nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xab");
        assertTrue(firstFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();


        inputString = generateString(1000);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args1 = {filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
    }


    @Test
    public void Should_SplitInto2Files_When_InputLineMoreThan1000LessThan2001() throws Exception {
        String inputString = generateString(1001);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] args = {filename};
        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        File firstFile = new File(currentDir + File.separator + testDirName + File.separator + "xaa");
        File secondFile = new File(currentDir + File.separator + testDirName + File.separator + "xab");
        File thirdFile = new File(currentDir + File.separator + testDirName + File.separator + "xac");
        assertTrue(firstFile.exists());
        assertTrue(secondFile.exists());
        assertFalse(thirdFile.exists());
        firstFile.delete();
        secondFile.delete();


        inputString = generateString(2000);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args1 = {filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertTrue(secondFile.exists());
        assertFalse(thirdFile.exists());
    }


    @Test
    public void Should_SplitIntoMultipleFiles_When_CorrectLineInput() throws Exception {
        String inputString = generateString(10);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] args = {"-l", "2", filename};
        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        File firstFile = new File(currentDir + File.separator + testDirName + File.separator + "xaa");
        File lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xae");
        File nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xaf");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateString(10);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args1 = {"-l", "3", filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xad");
        nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xae");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateString(27);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args2 = {"-l", "1", filename};
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xba");
        nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xbb");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateString(677);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        lastFile = new File(currentDir + File.separator + testDirName + File.separator + "zaa");
        nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "zab");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();
    }



    @Test
    public void Should_SplitIntoMultipleFiles_When_CorrectByteInput() throws Exception {
        String inputString = generateStringByte(10);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] args = {"-b", "5", filename};
        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        File firstFile = new File(currentDir + File.separator + testDirName + File.separator + "xaa");
        File lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xab");
        File nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xac");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(10);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args1 = {"-l", "3", filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xad");
        nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xae");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(27);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args2 = {"-l", "1", filename};
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xba");
        nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xbb");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(677);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        lastFile = new File(currentDir + File.separator + testDirName + File.separator + "zaa");
        nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "zab");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();
    }



    @Test
    public void Should_SplitIntoMultipleFiles_When_CorrectByteTypeInput() throws Exception {
        String inputString = generateStringByte(1024);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] args = {"-b", "1b", filename};
        try {
            splitApplication.run(args, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        File firstFile = new File(currentDir + File.separator + testDirName + File.separator + "xaa");
        File lastFile = new File(currentDir + File.separator + testDirName + File.separator + "xab");
        File nonExistantFile = new File(currentDir + File.separator + testDirName + File.separator + "xac");
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(513);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args1 = {"-l", "1b", filename};
        try {
            splitApplication.run(args1, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(2048);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args2 = {"-l", "1k", filename};
        try {
            splitApplication.run(args2, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(1025);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args3 = {"-l", "1k", filename};
        try {
            splitApplication.run(args3, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(2097152);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args4 = {"-l", "1m", filename};
        try {
            splitApplication.run(args4, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();


        inputString = generateStringByte(1048577);
        try {
            Files.write(file.toPath(), inputString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args5 = {"-l", "1m", filename};
        try {
            splitApplication.run(args5, null, System.out);
        } catch (SplitException e) {
            throw e;
        }
        assertTrue(firstFile.exists());
        assertTrue(lastFile.exists());
        assertFalse(nonExistantFile.exists());
        firstFile.delete();
        lastFile.delete();
    }
}
