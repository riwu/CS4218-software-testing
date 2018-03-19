package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.*;
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
    static HashMap<Character, Integer> optionsMap = new HashMap<>();
    static {
        optionsMap.put('s', 0);
        optionsMap.put('B', 1);
        optionsMap.put('q', 2);
    }

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
        boolean hasStdIn = inputFiles[1].equals("-");
        String result = "";

        if (hasStdIn && stdin == null) {
            throw new DiffException("Stdin is null");
        }
        else if (hasStdIn) {
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
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        Set<String> onlySet2 = new HashSet<>();
        BufferedReader br1 = new BufferedReader(new FileReader(new File(fileNameA)));
        BufferedReader br2 = new BufferedReader(new FileReader(new File(fileNameB)));
        String line;

        if (isTextFile(fileNameA)) {
            while ((line = br1.readLine()) != null) {
                if (isNoBlank && line.trim().equals("")) {
                    continue;
                }
                set1.add(line);
            }
            while ((line = br2.readLine()) != null) {
                if (isNoBlank && line.trim().equals("")) {
                    continue;
                }
                else if (isSimple && !set1.contains(line)) {
					br1.close(); br2.close();
                    return parseOutput(fileNameA, fileNameB, "differ", false);
                } else if (!set1.contains(line)) {
                    onlySet2.add(line);
                }
                set2.add(line);
            }
			br1.close(); br2.close();
        } else {
            int val1;
            int val2;

            while ((val1 = br1.read()) > 0) {
                val2 = br2.read();
                if (val1 != val2) {
					br1.close(); br2.close();
                    return parseOutput(fileNameA, fileNameB, "differ", true);
                }
            }
            if (isShowSame) {
				br1.close(); br2.close();
                return parseOutput(fileNameA, fileNameB, "are identical", true);
            }
        }

        set1.removeAll(set2);
        if (isShowSame && onlySet2.isEmpty() && set1.isEmpty()) {
            return parseOutput(fileNameA, fileNameB, "are identical", false);
        }
        if (isSimple && !set1.isEmpty()) {
            return parseOutput(fileNameA, fileNameB, "differ", false);
        }

        return parseDiffFormatOutput(set1, onlySet2);
	}

	@Override
	public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws Exception {
        Set<String> set1 = getFileInDir(folderA);
        Set<String> set2 = getFileInDir(folderB);
        StringBuilder strBuilder = new StringBuilder();
        String outputString = "";

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
                    char c = result.charAt(0);
                    if (c == '>' || c == '<') {
                        outputString = parseDiretoryOutput("diff",
                                folderA, folderB, file.getName(), "", false);
                        strBuilder.append(outputString);
                    }
                    strBuilder.append(result).append(System.lineSeparator());;
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
		Scanner fileScanner = new Scanner(stdin);
	    String stdinFileName = fileScanner.nextLine();
        File stdinFile = new File(stdinFileName);
		String output = "";

        if (!stdinFile.exists()) {
			fileScanner.close();
            throw new DiffException("Invalid stdin file");
        }
        if (firstFile.isFile() && stdinFile.isFile()) {
			output = diffTwoFiles(fileName, stdinFileName, isShowSame, isNoBlank, isSimple);
        }
        else if (firstFile.isDirectory() && stdinFile.isDirectory()) {
			output = diffTwoDir(fileName, stdinFileName, isShowSame, isNoBlank, isSimple);
        } else {
			fileScanner.close();
            throw new DiffException("Unable to diff directory and file");
        }
		fileScanner.close();
		return output;
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
    private Set<String> getFileInDir(String folder) throws DiffException {
	    Set<String> set = new HashSet<String>();

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
        String s;
        String s2;

        try {
            FileInputStream inputStream = new FileInputStream(file);

            int size = inputStream.available();
            if(size > 1000) {
                size = 1000;
			}
            byte[] data = new byte[size];
            inputStream.read(data);
            inputStream.close();
            s = new String(data, "ISO-8859-1");
            s2 = s.replaceAll(
                    "[a-zA-Z0-9ßöäü\\.\\*!\"§\\$\\%&/()=\\?@~'#:,;\\"+
                            "+><\\|\\[\\]\\{\\}\\^°²³\\\\ \\n\\r\\t_\\-`´âêîô"+
                            "ÂÊÔÎáéíóàèìòÁÉÍÓÀÈÌÒ©‰¢£¥€±¿»«¼½¾™ª]", "");
            // will delete all text signs
        } catch (IOException e) {
            throw new DiffException(e.getMessage());
        }
        double val = (double)(s.length() - s2.length()) / (double)(s.length());
        // percentage of text signs in the text
        return val > 0.95;
    }

    private Boolean[] parseOptions(String... args) throws DiffException {
        Boolean[] optionBool = new Boolean[3];
        Arrays.fill(optionBool, false);

        for (String arg : args) {
            for (int i = 0; i < arg.length(); i++) {
                Character c = arg.charAt(i);

                if (i == 0 && c != '-') {
                    break;
                } else if (i == 0) {
                    continue;
                }
                if (optionsMap.containsKey(c)) {
					optionBool[optionsMap.get(c)] = true;
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

    private String parseOutput(String fileA, String fileB, String output, boolean isBinary) {
        StringBuilder strBuilder = new StringBuilder();
	    if (isBinary) {
	        strBuilder.append("Binary files ");
        } else {
	        strBuilder.append("Files ");
        }
	    strBuilder.append(fileA);
	    if (isBinary) {
	        strBuilder.append(" and ");
        } else {
	        strBuilder.append(' ');
        }
        strBuilder.append(fileB).append(' ').append(output);

	    return strBuilder.toString();
    }

    private String parseDiffFormatOutput(Set<String> set1, Set<String> set2) {
	    StringBuilder strBuilder = new StringBuilder();

        for (String s: set1) {
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
}
