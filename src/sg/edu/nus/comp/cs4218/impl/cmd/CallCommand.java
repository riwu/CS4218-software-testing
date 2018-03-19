package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

/**
 * A Call Command is a sub-command consisting of at least one non-keyword and
 * quoted (if any).
 * 
 * <p>
 * <b>Command format:</b> <code>(&lt;non-Keyword&gt; | &lt;quoted&gt;)*</code>
 * </p>
 */

public class CallCommand implements Command {
	public static final String EXP_INVALID_APP = "Invalid app.";
	public static final String EXP_SYNTAX = "Invalid syntax encountered.";
	public static final String EXP_REDIR_PIPE = "File output redirection and pipe "
			+ "operator cannot be used side by side.";
	public static final String EXP_SAME_REDIR = "Input redirection file same as "
			+ "output redirection file.";
	public static final String EXP_STDOUT = "Error writing to stdout.";
	public static final String EXP_NOT_SUPPORTED = " not supported yet";

	String app;
	String cmdline, inputStreamS, outputStreamS;
	String[] argsArray;
	Boolean error;
	String errorMsg;

	public CallCommand(String cmdline) {
		this.cmdline = cmdline.trim();
		app = inputStreamS = outputStreamS = "";
		error = false;
		errorMsg = "";
		argsArray = new String[0];
	}

	public CallCommand() {
		this("");
	}

	/**
	 * Evaluates sub-command using data provided through stdin stream. Writes
	 * result to stdout stream.
	 * 
	 * @param stdin
	 *            InputStream to get data from.
	 * @param stdout
	 *            OutputStream to write resultant data to.
	 * 
	 * @throws AbstractApplicationException
	 *             If an exception happens while evaluating the sub-command.
	 * @throws ShellException
	 *             If an exception happens while evaluating the sub-command.
	 */
	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		if (error) {
			throw new ShellException(errorMsg);
		}

		InputStream inputStream;
		OutputStream outputStream;

		argsArray = ShellImpl.processBQ(argsArray);

		if (("").equals(inputStreamS)) {// empty
			inputStream = stdin;
		} else { // not empty
			inputStream = ShellImpl.openInputRedir(inputStreamS);
		}
		if (("").equals(outputStreamS)) { // empty
			outputStream = stdout;
		} else {
			outputStream = ShellImpl.openOutputRedir(outputStreamS);
		}

		ShellImpl.runApp(app, argsArray, inputStream, outputStream);
		ShellImpl.closeInputStream(inputStream);
		ShellImpl.closeOutputStream(outputStream);
	}

	/**
	 * Parses and splits the sub-command to the call command into its different
	 * components, namely the application name, the arguments (if any), the
	 * input redirection file path (if any) and output redirection file path (if
	 * any).
	 * 
	 * @throws ShellException
	 *             If an exception happens while parsing the sub-command, or if
	 *             the input redirection file path is same as that of the output
	 *             redirection file path.
	 */

	public void parse() throws ShellException {
		Vector<String> cmdVector = new Vector<String>();
		Boolean result = true;
		int endIdx = 0;
		String str = " " + cmdline + " ";

		try {
			endIdx = extractArgs(str, cmdVector);

			cmdVector = parseGlob(cmdVector);
			cmdVector.add(""); // reserved for input redir
			cmdVector.add(""); // reserved for output redir

			endIdx = extractInputRedir(str, cmdVector, endIdx);
			endIdx = extractOutputRedir(str, cmdVector, endIdx);

			//System.out.print("Parse: ");
			//cmdVector.stream().forEach(cmd -> System.out.print( "["+cmd + "]"));

			//System.out.println("cli: "+cmdVector.toString());
		} catch (ShellException e) {
			result = false;
		}

		if (isSpacesOnly(str.substring(endIdx))) {
			result = true;
		} else {
			result = false;
		}

		if (!result) {
			//System.out.println("err"+cmdVector.toString());
			this.app = cmdVector.get(0);
			error = true;
			if (("").equals(errorMsg)) {
				errorMsg = ShellImpl.EXP_SYNTAX;
			}
			throw new ShellException(errorMsg);
		}

		String[] cmdTokensArray = cmdVector
				.toArray(new String[cmdVector.size()]);
		this.app = cmdTokensArray[0];
		int nTokens = cmdTokensArray.length;

		// process inputRedir and/or outputRedir
		if (nTokens >= 3) { // last 2 for inputRedir & >outputRedir
			this.inputStreamS = cmdTokensArray[nTokens - 2].trim();
			this.outputStreamS = cmdTokensArray[nTokens - 1].trim();
			if (!("").equals(inputStreamS)
					&& inputStreamS.equals(outputStreamS)) {
				error = true;
				errorMsg = ShellImpl.EXP_SAME_REDIR;
				throw new ShellException(errorMsg);
			}
			this.argsArray = Arrays.copyOfRange(cmdTokensArray, 1,
					cmdTokensArray.length - 2);
		} else {
			this.argsArray = new String[0];
		}
	}

	/**
	 * Parses the sub-command's arguments to the call command and splits it into
	 * its different components, namely the application name and the arguments
	 * (if any), based on rules:
     * Unquoted: any char except for whitespace characters, quotes, newlines, semicolons ;, |, < and >.
     * Double quoted: any char except \n, ", `
     * Single quoted: any char except \n, '
	 * Back quotes in Double Quote for command substitution: DQ rules for
	 * outside BQ + `anything but \n` in BQ.
	 * 
	 * @param str
	 *            String of command to split.
	 * @param cmdVector
	 *            Vector of String to store the split arguments into.
	 * 
	 * @return endIdx Index of string where the parsing of arguments stopped
	 *         (due to no more matches).
	 * e
	 * @throws ShellException
	 *             If an error in the syntax of the command is detected while
	 *             parsing.
	 */
	int extractArgs(String str, Vector<String> cmdVector) throws ShellException {
		String patternDash = "[\\s]+(-[A-Za-z]*)[\\s]";
		String patternUQ = "[\\s]+([^\\s\"'`\\n;|<>]*)[\\s]";
		String patternDQ = "[\\s]+\"([^\\n\"`]*)\"[\\s]";
		String patternSQ = "[\\s]+\'([^\\n']*)\'[\\s]";
		String patternBQ = "[\\s]+(`[^\\n`]*`)[\\s]"; // <space>`<not new line or backtick>`<space>
		String patternBQinDQ = "[\\s]+\"([^\\n\"`]*`[^\\n]*`[^\\n\"`]*)\"[\\s]";
		String[] patterns = { patternDash, patternUQ, patternDQ, patternSQ,
				patternBQ, patternBQinDQ };
		String substring;
		int newStartIdx = 0, smallestStartIdx, smallestPattIdx, newEndIdx = 0;
		do {
			substring = str.substring(newEndIdx);
			smallestStartIdx = -1;
			smallestPattIdx = -1;
			if (substring.trim().startsWith("<")
					|| substring.trim().startsWith(">")
                    || substring.trim().startsWith("|")) {
				break;
			}
			for (int i = 0; i < patterns.length; i++) {
				Pattern pattern = Pattern.compile(patterns[i]);
				Matcher matcher = pattern.matcher(substring);
				if (matcher.find()
						&& (matcher.start() < smallestStartIdx || smallestStartIdx == -1)) {
					smallestPattIdx = i;
					smallestStartIdx = matcher.start();
				}
			}
			if (smallestPattIdx != -1) { // if a pattern is found
				Pattern pattern = Pattern.compile(patterns[smallestPattIdx]);
				Matcher matcher = pattern.matcher(str.substring(newEndIdx));
				if (matcher.find()) {
					String matchedStr = matcher.group(1);
					newStartIdx = newEndIdx + matcher.start();
					if (newStartIdx != newEndIdx) {
						error = true;
						errorMsg = ShellImpl.EXP_SYNTAX;
						throw new ShellException(errorMsg);
					} // check if there's any invalid token not detected
					cmdVector.add(matchedStr);
					newEndIdx = newEndIdx + matcher.end() - 1;
				}
			}
		} while (smallestPattIdx != -1);
		return newEndIdx;
	}

	/**
	 * Extraction of input redirection from cmdLine with two slots at end of
	 * cmdVector reserved for <inputredir and >outredir. For valid inputs,
	 * assumption that input redir and output redir are always at the end of the
	 * command and input stream first the output stream if both are in the args
	 * 
	 * @param str
	 *            String of command to split.
	 * @param cmdVector
	 *            Vector of String to store the found result into.
	 * @param endIdx
	 *            Index of str to start parsing from.
	 * 
	 * @return endIdx Index of string where the parsing of arguments stopped
	 *         (due to no more matches).
	 * 
	 * @throws ShellException
	 *             When more than one input redirection string is found, or when
	 *             invalid syntax is encountered..
	 */
	int extractInputRedir(String str, Vector<String> cmdVector, int endIdx)
			throws ShellException {
		String substring = str.substring(endIdx);
		String strTrm = substring.trim();
		if (strTrm.startsWith(">") || strTrm.isEmpty()) {
			return endIdx;
		}
		if (!strTrm.startsWith("<")) {
			throw new ShellException(EXP_SYNTAX);
		}

		int newEndIdx = endIdx;
		Pattern inputRedirP = Pattern
				.compile("[\\s]+<[\\s]+(([^\\n\"`'<>]*))[\\s]");
		Matcher inputRedirM;
		String inputRedirS = "";
		int cmdVectorIndex = cmdVector.size() - 2;

		while (!isSpacesOnly(substring)) {
			inputRedirM = inputRedirP.matcher(substring);
			inputRedirS = "";
			if (inputRedirM.find()) {
				if (!cmdVector.get(cmdVectorIndex).isEmpty()) {
					throw new ShellException(EXP_SYNTAX);
				}
				inputRedirS = inputRedirM.group(1);
				cmdVector.set(cmdVectorIndex, inputRedirS);
				newEndIdx = newEndIdx + inputRedirM.end() - 1;
			} else {
				break;
			}
			substring = str.substring(newEndIdx);
		}
		return newEndIdx;
	}

	/**
	 * Extraction of output redirection from cmdLine with two slots at end of
	 * cmdVector reserved for <inputredir and >outredir. For valid inputs,
	 * assumption that input redir and output redir are always at the end of the
	 * command and input stream first the output stream if both are in the args.
	 * 
	 * @param str
	 *            String of command to split.
	 * @param cmdVector
	 *            Vector of String to store the found result into.
	 * @param endIdx
	 *            Index of str to start parsing from.
	 * 
	 * @return endIdx Index of string where the parsing of arguments stopped
	 *         (due to no more matches).
	 * 
	 * @throws ShellException
	 *             When more than one input redirection string is found, or when
	 *             invalid syntax is encountered..
	 */
	int extractOutputRedir(String str, Vector<String> cmdVector, int endIdx)
			throws ShellException {
		String substring = str.substring(endIdx);
		String strTrm = substring.trim();
		if (strTrm.isEmpty()) {
			return endIdx;
		}
		if (!strTrm.startsWith(">")) {
			throw new ShellException(EXP_SYNTAX);
		}

		int newEndIdx = endIdx;
		Pattern inputRedirP = Pattern
				.compile("[\\s]+>[\\s]+(([^\\n\"`'<>]*))[\\s]");
		Matcher inputRedirM;
		String inputRedirS = "";
		int cmdVectorIdx = cmdVector.size() - 1;
		while (!isSpacesOnly(substring)) {

			inputRedirM = inputRedirP.matcher(substring);
			inputRedirS = "";
			if (inputRedirM.find()) {
				if (!cmdVector.get(cmdVectorIdx).isEmpty()) {
					throw new ShellException(EXP_SYNTAX);
				}
				inputRedirS = inputRedirM.group(1);
				cmdVector.set(cmdVectorIdx, inputRedirS);
				newEndIdx = newEndIdx + inputRedirM.end() - 1;
			} else {
				break;
			}
			substring = str.substring(newEndIdx);
		}
		return newEndIdx;
	}

	// TODO Consider putting into utility class
	private boolean isSpacesOnly(String substring) {
		for(int i = 0; i < substring.length(); i++) {
			if(substring.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Terminates current execution of the command (unused for now)
	 */
	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	}

	public Vector<String> parseGlob(Vector<String> cmdVector){
		Vector<String> parsedVector = new Vector<>();

		cmdVector.forEach( cmd -> {
			String[] parsedCmd = globFilesDirectories(cmd);

            parsedVector.addAll(Arrays.asList(parsedCmd));

		});

		return parsedVector;
	}

	public String[] globFilesDirectories(String args) {

		if (args == null) {
			return null;
		}
		if (!args.contains("*")) {
			return new String[] {args};
		}
		// if the command is of form <command> '<single_quote_content>', don't evaluate
		Pattern singleQuote = Pattern.compile("(?:.+)\\s+'(?:.*)'");
		if (singleQuote.matcher(args).matches()) {
			return new String[]{args};
		}
		if (singleQuote.matcher(cmdline).matches()) {
			return new String[] {args};
		}

		// if the command is of form <command> "<double_quote_content>", don't evaluate
		Pattern doubleQuote = Pattern.compile("(?:.+)\\s+\"(?:.*)\"");
		if (doubleQuote.matcher(args).matches()) {
			return new String[]{args};
		}
		if (doubleQuote.matcher(cmdline).matches()) {
			return new String[] {args};
		}

		// separate the base dir and glob
        String[] argsArray;
        String baseDirectory;
        String glob;
        if (args.startsWith(".") || args.startsWith("/")) {

            argsArray = args.split("(?=\\*)", 2);
            baseDirectory = argsArray.length == 1 ? "." : argsArray[0];
            glob = argsArray.length == 1 ? argsArray[0] : argsArray[1];
        }else{
            // assume current directory
            baseDirectory = ".";
            glob = args;
        }

		// resolve ambiguity and combine to single path
		Path globPath = Paths.get(baseDirectory, glob);

		//System.out.println("basedir: "+baseDirectory);
		//System.out.println("glob: "+glob);

		FileSystem fileSystem = FileSystems.getDefault();
		PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + globPath);

		List<String> filesAndDirectory = new ArrayList<>();

		try {
			// define the operation when the file visitor traverse file tree
			Files.walkFileTree(Paths.get(baseDirectory), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {


					if (pathMatcher.matches(directory)){
						filesAndDirectory.add(directory.toString());
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					if (pathMatcher.matches(file)) {
						filesAndDirectory.add(file.toString());
					}

					return FileVisitResult.CONTINUE;
				}


			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return filesAndDirectory.toArray(new String[]{});
	}

}
