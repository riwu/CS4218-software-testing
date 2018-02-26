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
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws DiffException {
        Boolean[] options = parseOptions(args);
        String[] inputFiles = parseFile(args);

        if (stdout == null) {
            throw new DiffException("null stdout");
        }
        boolean hasStdIn = inputFiles[1] == "-";
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
                    return parseOutput(fileNameA, fileNameB, "differ", false);
                } else if (!set1.contains(line)) {
                    onlySet2.add(line);
                }
                set2.add(line);
            }
        } else {
            int val1;
            int val2;

            while ((val1 = br1.read()) > 0) {
                val2 = br2.read();
                if (val1 != val2) {
                    return parseOutput(fileNameA, fileNameB, "differ", true);
                }
            }
            if (isShowSame) {
                return parseOutput(fileNameA, fileNameB, "are identical", true);
            }
        }

        set1.removeAll(set2);
        if (isShowSame && onlySet2.isEmpty() && set1.isEmpty()) {
            return parseOutput(fileNameA, fileNameB, "are identical", false);
        }
        if (isSimple && set1.size() > 0) {
            return parseOutput(fileNameA, fileNameB, "differ", false);
        }

        return parseDiffFormatOutput(set1, onlySet2);
	}

	@Override
	public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws Exception {
        Set<String> s1 = getFileInDir(folderA);
        Set<String> s2 = getFileInDir(folderB);
        StringBuilder sb = new StringBuilder();
        String outputString = "";

        for (String filename : s1) {
            if (s2.contains(filename)) {
                File f = new File(folderA + File.separator + filename);
                if (f.isDirectory()) {
                    outputString = parseDiretoryOutput("Common subdirectories:",
                            folderA,
                            folderB,
                            f.getName(),
                            "",
                            true);
                    sb.append(outputString);
                } else {
                    String fileA = folderA + File.separator + f.getName();
                    String fileB = folderB + File.separator + f.getName();
                    String result = diffTwoFiles(fileA, fileB, isShowSame, isNoBlank, isSimple);

                    if (result.equals("")) {
                        continue;
                    }
                    char c = result.charAt(0);
                    if (c == '>' || c == '<') {
                        outputString = parseDiretoryOutput("diff",
                                folderA,
                                folderB,
                                f.getName(),
                                "",
                                false);
                        sb.append(outputString);
                    }
                    sb.append(result)
                            .append(System.lineSeparator());;
                }
            } else {
                sb.append("Only in ")
                        .append(folderA)
                        .append(": ")
                        .append(filename)
                        .append(System.lineSeparator());
            }
        }

        for (String filename: s2) {
            if (!s1.contains(filename)) {
                sb.append(("Only in "))
                        .append(folderB)
                        .append(": ")
                        .append(filename)
                        .append(System.lineSeparator());
            }
        }
        return sb.toString().trim();
	}

	@Override
	public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws Exception {
	    File firstFile = new File(fileName);
	    String stdinFileName = new Scanner(stdin).nextLine();
        File stdinFile = new File(stdinFileName);

        if (!stdinFile.exists()) {
            throw new DiffException("Invalid stdin file");
        }
        if (firstFile.isFile() && stdinFile.isFile()) {
            return diffTwoFiles(fileName, stdinFileName, isShowSame, isNoBlank, isSimple);
        }
        else if (firstFile.isDirectory() && stdinFile.isDirectory()) {
            return diffTwoDir(fileName, stdinFileName, isShowSame, isNoBlank, isSimple);
        } else {
            throw new DiffException("Unable to diff directory and file");
        }
	}

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private Set<String> getFileInDir(String folder) throws DiffException {
	    Set<String> set = new HashSet<String>();

        try {
            Files.walkFileTree(Paths.get(folder), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File f = file.toFile();
                    if (!f.isHidden()) {
                        set.add(f.getName());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(Paths.get(folder))) {
                        return FileVisitResult.CONTINUE;
                    }
                    File f = dir.toFile();
                    if (!f.isHidden()) {
                        set.add(f.getName());
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            throw new DiffException(e.getMessage());
        }

        return set;
    }

    private boolean isTextFile(String filename) throws DiffException {
        File f = new File(filename);
        String s;
        String s2;

        try {
            FileInputStream in = new FileInputStream(f);

            int size = in.available();
            if(size > 1000)
                size = 1000;
            byte[] data = new byte[size];
            in.read(data);
            in.close();
            s = new String(data, "ISO-8859-1");
            s2 = s.replaceAll(
                    "[a-zA-Z0-9ßöäü\\.\\*!\"§\\$\\%&/()=\\?@~'#:,;\\"+
                            "+><\\|\\[\\]\\{\\}\\^°²³\\\\ \\n\\r\\t_\\-`´âêîô"+
                            "ÂÊÔÎáéíóàèìòÁÉÍÓÀÈÌÒ©‰¢£¥€±¿»«¼½¾™ª]", "");
            // will delete all text signs
        } catch (IOException e) {
            throw new DiffException(e.getMessage());
        }
        double d = (double)(s.length() - s2.length()) / (double)(s.length());
        // percentage of text signs in the text
        return d > 0.95;
    }

    private Boolean[] parseOptions(String[] args) throws DiffException {
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
                if (!optionsMap.containsKey(c)) {
                    throw new DiffException("Invalid option");
                } else {
                    optionBool[optionsMap.get(c)] = true;
                }
            }
        }

        return optionBool;
    }

    private String[] parseFile(String[] args) throws DiffException {
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
        File f = new File(files[0]);
	    if (!f.exists()) {
	        throw new DiffException("Invalid file");
        }
        if (!files[1].equals('-')) {
	        f = new File(files[1]);
	        if (!f.exists()) {
	            throw new DiffException("Invalid file");
            }

        }

	    return files;
    }

    private String parseOutput(String fileA, String fileB, String output, boolean isBinary) {
        StringBuilder sb = new StringBuilder();
	    if (isBinary) {
	        sb.append("Binary files ");
        } else {
	        sb.append("Files ");
        }
	    sb.append(fileA);
	    if (isBinary) {
	        sb.append(" and ");
        } else {
	        sb.append(" ");
        }
        sb.append(fileB)
                .append(" ")
                .append(output);

	    return sb.toString();
    }

    private String parseDiffFormatOutput(Set<String> s1, Set<String> s2) {
	    StringBuilder sb = new StringBuilder();

        for (String s: s1) {
            sb.append("< ")
                    .append(s)
                    .append(System.lineSeparator());
        }

        for (String s: s2) {
	        sb.append("> ")
                    .append(s)
                    .append(System.lineSeparator());
        }
	    return sb.toString().trim();
    }

    private String parseDiretoryOutput(String preSentence, String folderA, String folderB, String file, String postSentence, boolean includeAnd) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(preSentence)
                .append(" ")
                .append(folderA)
                .append(File.separator)
                .append(file);

	    if (includeAnd) {
	        sb.append(" and ");
        } else {
            sb.append(" ");
        }
        sb.append(folderB)
                .append(File.separator)
                .append(file);
	    if (!postSentence.equals("")) {
	        sb.append(" ")
                    .append(postSentence);
        }

        sb.append(System.lineSeparator());
	    return sb.toString();
    }
}
