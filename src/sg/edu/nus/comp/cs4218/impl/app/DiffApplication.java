package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * The cat command concatenates the content of given files and prints on the
 * standard output.
 * 
 * <p>
 * <b>Command format:</b> <code>[Options] FILES...</code>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */
public class DiffApplication implements DiffInterface {
    static String DIFFER_KEYWORD = "differ";
    static HashMap<Character, Integer> optionsMap = new HashMap<>();
    static {
        optionsMap.put('s', 0);
        optionsMap.put('B', 1);
        optionsMap.put('q', 2);
    }
    private boolean isStdinFirst = false;

	/**
	 * Runs the cat application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application. Each array element is
	 *            the path to a file. If no files are specified stdin is used.
	 * @param stdin
	 *            An InputStream. The input for the command is read from this
	 *            InputStream if no files are specified.
	 * @param stdout
	 *            An OutputStream. The output of the command is written to this
	 *            OutputStream.
	 * 
	 * @throws DiffException
	 *             If the file(s) specified do not exist or are unreadable.
	 */
	@Override
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws DiffException {
        Boolean[] options = parseOptions(args);
        String[] inputFiles = parseFile(args);

        if (stdout == null) {
            throw new DiffException("null stdout");
        }
        boolean hasInputArg = inputFiles[1].equals("-");
        String result = "";

        if (hasInputArg && stdin == null) {
            throw new DiffException("Input arg missing");
        }
        else if (hasInputArg) {
            try {
                result = diffFileAndStdin(inputFiles[0], stdin,
                        options[optionsMap.get('s')], options[optionsMap.get('B')], options[optionsMap.get('q')]);
            } catch (Exception e) {
                throw new DiffException(e.getMessage());
            }
        } else {
            File file1 = new File(inputFiles[0]);
            File file2 = new File(inputFiles[1]);

            if ((file1.isDirectory() && file2.isFile()) || file1.isFile() && file2.isDirectory()) {
                throw new DiffException("Invalid comparison between directory and file");
            } else if (file1.isDirectory()) {
                try {
                    result = diffTwoDir(inputFiles[0], inputFiles[1],
                            options[optionsMap.get('s')], options[optionsMap.get('B')], options[optionsMap.get('q')]);
                } catch (Exception e) {
                    throw new DiffException(e.getMessage());
                }

            } else if (file1.isFile()) {
                try {
                    result = diffTwoFiles(inputFiles[0], inputFiles[1],
                            options[optionsMap.get('s')], options[optionsMap.get('B')], options[optionsMap.get('q')]);
                } catch (Exception e) {
                    throw new DiffException(e.getMessage());
                }

            }
        }

        try {
            stdout.write(result.getBytes());
        } catch (IOException e) {
            throw new DiffException(e.getMessage());
        }
    }

	@Override
	public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws Exception {
        if (isTextFile(fileNameA)) {
            ArrayList<String> fileAExtra = new ArrayList<>();
            ArrayList<String> fileBExtra = new ArrayList<>();
            boolean hasPrevLineFileA = false;
            BufferedReader br1 = new BufferedReader(new FileReader(new File(fileNameA)));
            BufferedReader br2 = new BufferedReader(new FileReader(new File(fileNameB)));
            String lineA = br1.readLine();
            String lineB = br2.readLine();

            while (lineA != null && lineB != null) {
                if (isNoBlank) {
                    if (lineA.trim().equals("")) {
                        lineA = br1.readLine();
                        continue;
                    }
                    if (lineB.trim().equals("")) {
                        lineB = br2.readLine();
                        continue;
                    }
                }
                if (!lineA.equals(lineB)) {
                    if (isSimple) {
                        br1.close();
                        br2.close();
                        return parseOutput(fileNameA, fileNameB, DIFFER_KEYWORD, false, isShowSame, false);
                    }
                    String prevLineFileA = lineA;
                    lineA = br1.readLine();
                    if (lineA == null) {
                        fileAExtra.add(prevLineFileA);
                        continue;
                    }
                    else if (lineA.equals(lineB)) {
                        fileAExtra.add(prevLineFileA);
                    } else { // check fileB
                        String prevLineFileB = lineB;
                        lineB = br2.readLine();
                        if (lineB == null) {
                            fileAExtra.add(prevLineFileA);
                            fileBExtra.add(prevLineFileB);
                            continue;
                        }
                        else if (prevLineFileA.equals(lineB)) {
                            fileBExtra.add(prevLineFileB);
                            hasPrevLineFileA = true;
                        } else {
                            fileAExtra.add(prevLineFileA);
                            fileBExtra.add(prevLineFileB);
                            continue;
                        }
                    }
                }
                if (!hasPrevLineFileA) {
                    lineA = br1.readLine();
                }
                hasPrevLineFileA = false;
                lineB = br2.readLine();
            }

            while (lineA != null) {
                if (!(isNoBlank && lineA.trim().equals(""))) {
                    fileAExtra.add(lineA);
                }
                lineA = br1.readLine();
            }
            while (lineB != null) {
                if (!(isNoBlank && lineB.trim().equals(""))) {
                    fileBExtra.add(lineB);
                }
                lineB = br2.readLine();
            }
            br1.close();
            br2.close();

            if (isSimple && (!fileAExtra.isEmpty() || !fileBExtra.isEmpty())) {
                return parseOutput(fileNameA, fileNameB, DIFFER_KEYWORD, false, isShowSame, false);
            }

            if (isShowSame && fileAExtra.isEmpty() && fileBExtra.isEmpty()) {
                return parseOutput(fileNameA, fileNameB,"are identical", false, isShowSame, false);
            }

            return parseDiffFormatOutput(fileAExtra, fileBExtra);
        } else {
            InputStream inputStreamA = new FileInputStream(fileNameA);
            InputStream inputStreamB = new FileInputStream(fileNameB);
            byte[] bufferA = new byte[4096];
            byte[] bufferB = new byte[4096];
            boolean fileDiffers = false;

            while ( inputStreamA.read(bufferA) > 0) {
                int valB = inputStreamB.read(bufferB);
                if (valB == -1) {
                    fileDiffers = true;
                    break;
                }
                if (!Arrays.equals(bufferA, bufferB)) {
                    fileDiffers = true;
                    break;
                }
            }
            inputStreamA.close();

            if (fileDiffers) {
                inputStreamB.close();
                return parseOutput(fileNameA, fileNameB, DIFFER_KEYWORD, true, isShowSame, false);
            }

            if (inputStreamB.read(bufferB) > 0) {
                inputStreamB.close();
                return parseOutput(fileNameA, fileNameB, DIFFER_KEYWORD, true, isShowSame, false);
            }
            inputStreamB.close();
            if (isShowSame) {
                return parseOutput(fileNameA, fileNameB, "are identical", true, isShowSame, false);
            }

            return "";
        }
	}

	@Override
	public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws Exception {
        Set<String> set1 = getFileInDir(folderA);
        Set<String> set2 = getFileInDir(folderB);
        StringBuilder strBuilder = new StringBuilder();
        String outputString;

        for (String filename : set1) {
            if (set2.contains(filename)) {
                File file = new File(folderA + File.separator + filename);
                if (file.isDirectory()) {
                    outputString = parseDiretoryOutput("Common subdirectories:",
                            folderA, folderB, file.getName(), "", true);
                    strBuilder.append(outputString);
                } else {
                    String fileA = folderA + File.separator + file.getName();
                    String fileB = folderB + File.separator + file.getName();
                    String result = diffTwoFiles(fileA, fileB, isShowSame, isNoBlank, isSimple);

                    if (result.isEmpty()) {
                        continue;
                    }
                    char stringChar = result.charAt(0);
                    if (stringChar == '>' || stringChar == '<') {
                        outputString = parseDiretoryOutput("diff",
                                folderA, folderB, file.getName(), "", false);
                        strBuilder.append(outputString);
                    }
                    strBuilder.append(result).append(System.lineSeparator());
                }
            } else {
                strBuilder.append("Only in ")
                        .append(folderA).append(": ")
                        .append(filename).append(System.lineSeparator());
            }
        }

        for (String filename: set2) {
            if (!set1.contains(filename)) {
                strBuilder.append(("Only in "))
                        .append(folderB).append(": ")
						.append(filename) .append(System.lineSeparator());
            }
        }
        return strBuilder.toString().trim();
	}

	@Override
	public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws Exception {
	    File firstFile = new File(fileName);

        if (firstFile.isDirectory()) {
            throw new DiffException("Unable to diff directory with stdin");
        }

        Scanner fileScanner = new Scanner(stdin);
        ArrayList<String> fileExtra = new ArrayList<>();
        ArrayList<String> stdinExtra = new ArrayList<>();
        boolean hasPrevLineFile = false;

        BufferedReader br1 = new BufferedReader(new FileReader(firstFile));
        String line = br1.readLine();
        String stdinLine = getNextStdinLine(fileScanner);

        while (line != null && stdinLine != null) {
            if (isNoBlank) {
                if (line.trim().equals("")) {
                    line = br1.readLine();
                    continue;
                }
                if (stdinLine.trim().equals("")) {
                    stdinLine = getNextStdinLine(fileScanner);
                    continue;
                }
            }
            if (!line.equals(stdinLine)) {
                if (isSimple) {
                    br1.close();
                    fileScanner.close();
                    return parseOutput(fileName, "-", DIFFER_KEYWORD, false, isShowSame, true);
                }
                String prevLineFile = line;
                line = br1.readLine();
                if (line == null) {
                    fileExtra.add(prevLineFile);
                    stdinExtra.add(stdinLine);
                    continue;
                }
                else if (line.equals(stdinLine)) {
                    fileExtra.add(prevLineFile);
                } else { // check stdin
                    String prevLineStdin = stdinLine;
                    stdinLine = getNextStdinLine(fileScanner);

                    if (stdinLine == null) {
                        fileExtra.add(prevLineFile);
                        fileExtra.add(line);
                        stdinExtra.add(prevLineStdin);
                        continue;
                    }
                    else if (prevLineFile.equals(stdinLine)) {
                        stdinExtra.add(prevLineStdin);
                        hasPrevLineFile = true;
                    } else {
                        fileExtra.add(prevLineFile);
                        stdinExtra.add(prevLineStdin);
                        continue;
                    }
                }
            }
            if (!hasPrevLineFile) {
                line = br1.readLine();
            }
            hasPrevLineFile = false;
            stdinLine = getNextStdinLine(fileScanner);
        }

        while (line != null) {
            if (!(isNoBlank && line.trim().equals(""))) {
                fileExtra.add(line);
            }
            line = br1.readLine();
        }
        while (stdinLine != null) {
            if (isNoBlank && stdinLine.trim().equals("")) {
                stdinExtra.add(stdinLine);
            }
            stdinLine = getNextStdinLine(fileScanner);
        }
        br1.close();
		fileScanner.close();
        if (isSimple && (!fileExtra.isEmpty() || !stdinExtra.isEmpty())) {
            return parseOutput(fileName, "-", DIFFER_KEYWORD, false, isShowSame, true);
        }
		if (isShowSame && fileExtra.isEmpty() && stdinExtra.isEmpty()) {
		    return parseOutput(fileName, "-", "are identical", false, isShowSame, true);
        }


		return parseDiffFormatOutput(fileExtra, stdinExtra);
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
    private Set<String> getFileInDir(String folder) throws DiffException {
	    Set<String> set = new HashSet<>();

        try {
            Files.walkFileTree(Paths.get(folder), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File aFile = file.toFile();
                    if (!aFile.isHidden()) {
                        set.add(aFile.getName());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(Paths.get(folder))) {
                        return FileVisitResult.CONTINUE;
                    }
                    File file = dir.toFile();
                    if (!file.isHidden()) {
                        set.add(file.getName());
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            throw new DiffException(e.getMessage());
        }

        return set;
    }

	@SuppressWarnings("PMD.PreserveStackTrace")
    private boolean isTextFile(String filename) throws DiffException {
        File file = new File(filename);
        String filedata;
        String replacedData;

        try {
            FileInputStream inputStream = new FileInputStream(file);

            int size = inputStream.available();
            if(size > 1000) {
                size = 1000;
			}
            byte[] data = new byte[size];
            inputStream.read(data);
            inputStream.close();
            filedata = new String(data, "ISO-8859-1");
            replacedData = filedata.replaceAll(
                    "[a-zA-Z0-9ßöäü\\.\\*!\"§\\$\\%&/()=\\?@~'#:,;\\"+
                            "+><\\|\\[\\]\\{\\}\\^°²³\\\\ \\n\\r\\t_\\-`´âêîô"+
                            "ÂÊÔÎáéíóàèìòÁÉÍÓÀÈÌÒ©‰¢£¥€±¿»«¼½¾™ª]", "");
            // will delete all text signs
        } catch (IOException e) {
            throw new DiffException(e.getMessage());
        }
        double val = (double)(filedata.length() - replacedData.length()) / (double)(filedata.length());
        // percentage of text signs in the text
        return val > 0.95;
    }

    private Boolean[] parseOptions(String... args) throws DiffException {
        Boolean[] optionBool = new Boolean[3];
        Arrays.fill(optionBool, false);

        for (String arg : args) {
            for (int i = 0; i < arg.length(); i++) {
                Character stringChar = arg.charAt(i);

                if (i == 0 && stringChar != '-') {
                    break;
                } else if (i == 0) {
                    continue;
                }
                if (optionsMap.containsKey(stringChar)) {
					optionBool[optionsMap.get(stringChar)] = true;
                } else {
					throw new DiffException("Invalid option");
                }
            }
        }

        return optionBool;
    }

    private String[] parseFile(String... args) throws DiffException {
	    String[] files = new String[2];
	    boolean isStdin = false;
	    int fileCounter = 0;

	    for (String arg : args) {
            if (arg.length() == 1 && arg.charAt(0) == '-') {
                if (isStdin) {
                    throw new DiffException("Unable to handle two stdin");
                }
                isStdin = true;
                files[1] = arg;
                if (fileCounter == 0) {
                    isStdinFirst = true;
                }
                fileCounter += 1;
            } else if (arg.charAt(0) == '-') {
                continue;
            } else if (fileCounter == 2) {
                break;
            } else {
                files[fileCounter] = arg;
                fileCounter += 1;
            }
        }
        if (fileCounter < 2) {
	        throw new DiffException("Insufficient arguments to compare");
        }
        File file = new File(files[0]);
	    if (!file.exists()) {
	        throw new DiffException("Invalid file");
        }
        if (!files[1].equals('-')) {
	        file = new File(files[1]);
	        if (!file.exists()) {
	            throw new DiffException("Invalid file");
            }

        }

	    return files;
    }

    private String parseOutput(String fileA, String fileB, String output, boolean isBinary, boolean isShowSame, boolean hasStdin) {
        StringBuilder strBuilder = new StringBuilder();
	    if (isBinary && !isShowSame) {
	        strBuilder.append("Binary files ");
        } else {
	        strBuilder.append("Files ");
        }

        if (hasStdin && isStdinFirst) {
	        strBuilder.append(fileB);
        } else {
	        strBuilder.append(fileA);
        }

        strBuilder.append(" and ");

        if (hasStdin && isStdinFirst) {
            strBuilder.append(fileA);
        } else {
            strBuilder.append(fileB);
        }

        strBuilder.append(' ')
                .append(output);

	    return strBuilder.toString();
    }

    private String parseDiffFormatOutput(ArrayList<String> array1, ArrayList<String> set2) {
	    StringBuilder strBuilder = new StringBuilder();

        for (String s: array1) {
            strBuilder.append("< ").append(s).append(System.lineSeparator());
        }

        for (String s: set2) {
	        strBuilder.append("> ")
                    .append(s)
                    .append(System.lineSeparator());
        }
	    return strBuilder.toString().trim();
    }

    private String parseDiretoryOutput(String preSentence, String folderA, String folderB, String file, String postSentence, boolean includeAnd) {
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append(preSentence).append(' ').append(folderA)
                .append(File.separator).append(file);

	    if (includeAnd) {
	        strBuilder.append(" and ");
        } else {
            strBuilder.append(' ');
        }
        strBuilder.append(folderB).append(File.separator).append(file);
	    if (!postSentence.isEmpty()) {
	        strBuilder.append(' ').append(postSentence);
        }

        strBuilder.append(System.lineSeparator());
	    return strBuilder.toString();
    }

    private String getNextStdinLine(Scanner scanner) {
	    String output = null;
	    if (scanner.hasNextLine()) {
	        output = scanner.nextLine();
        }
        return output;
    }
}
