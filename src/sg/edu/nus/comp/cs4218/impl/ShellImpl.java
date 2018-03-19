package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.*;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Shell is a command interpreter and forms the backbone of the entire
 * program. Its responsibility is to interpret commands that the user type and
 * to run programs that the user specify in her command lines.
 * <p>
 * <p>
 * <b>Command format:</b>
 * <code>&lt;Pipe&gt; | &lt;Sequence&gt; | &lt;Call&gt;</code>
 * </p>
 */

public class ShellImpl implements Shell {

    public static final String EXP_INVALID_APP = "Invalid app.";
    public static final String EXP_SYNTAX = "Invalid syntax encountered.";
    public static final String EXP_REDIR_PIPE = "File output redirection and "
            + "pipe operator cannot be used side by side.";
    public static final String EXP_SAME_REDIR = "Input redirection file same "
            + "as output redirection file.";
    public static final String EXP_STDOUT = "Error writing to stdout.";
    public static final String EXP_NOT_SUPPORTED = " not supported yet";

    /**
     * Searches for and processes the commands enclosed by back quotes for
     * command substitution.If no back quotes are found, the argsArray from the
     * input is returned unchanged. If back quotes are found, the back quotes
     * and its enclosed commands substituted with the output from processing the
     * commands enclosed in the back quotes.
     *
     * @param argsArray String array of the individual commands.
     * @return String array with the back quotes command processed.
     * @throws AbstractApplicationException If an exception happens while processing the content in the
     *                                      back quotes.
     * @throws ShellException               If an exception happens while processing the content in the
     *                                      back quotes.
     */
    public static String[] processBQ(String... argsArray)
            throws AbstractApplicationException, ShellException {
        // echo "this is space `echo "nbsp"`"
        // echo "this is space `echo "nbsp"` and `echo "2nd space"`"
        // Back quoted: any char except \n,`
        String[] resultArr = new String[argsArray.length];
        System.arraycopy(argsArray, 0, resultArr, 0, argsArray.length);
        String patternBQ = "`([^\\n`]*)`";
        Pattern patternBQp = Pattern.compile(patternBQ);

        for (int i = 0; i < argsArray.length; i++) {
            Matcher matcherBQ = patternBQp.matcher(argsArray[i]);
            if (matcherBQ.find()) {// found backquoted
                String bqStr = matcherBQ.group(1);
                // cmdVector.add(bqStr.trim());
                // process back quote
                // System.out.println("backquote" + bqStr);
                OutputStream bqOutputStream = new ByteArrayOutputStream();
                ShellImpl shell = new ShellImpl();
                shell.parseAndEvaluate(bqStr, bqOutputStream);

                ByteArrayOutputStream outByte = (ByteArrayOutputStream) bqOutputStream;
                byte[] byteArray = outByte.toByteArray();
                String bqResult = new String(byteArray).replace("\n", "")
                        .replace("\r", "");

                // replace substring of back quote with result
                String replacedStr = argsArray[i].replace("`" + bqStr + "`",
                        bqResult);
                resultArr[i] = replacedStr;
            }
        }
        return resultArr;
    }

    /**
     * Static method to run the application as specified by the application
     * command keyword and arguments.
     *
     * @param app          String containing the keyword that specifies what application
     *                     to run.
     * @param argsArray    String array containing the arguments to pass to the
     *                     applications for running.
     * @param inputStream  InputputStream for the application to get arguments from, if
     *                     needed.
     * @param outputStream OutputStream for the application to print its output to.
     * @throws AbstractApplicationException If an exception happens while running any of the
     *                                      application(s).
     * @throws ShellException               If an unsupported or invalid application command is detected.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
	public static void runApp(String app, String[] argsArray,
                              InputStream inputStream, OutputStream outputStream)
            throws AbstractApplicationException, ShellException {
        Application absApp = null;

        switch (app) {
            case "cat": // cat [FILE]...
                absApp = new CatApplication();
                break;
            case "echo": // echo [args]...
                absApp = new EchoApplication();
                break;
            case "head": // head [OPTIONS] [FILE]
                absApp = new HeadApplication();
                break;
            case "tail": // tail [OPTIONS] [FILE]
                absApp = new TailApplication();
                break;
            case "ls":
                absApp = new LsApplication();
                break;
            case "mkdir":
                absApp = new MkdirApplication();
                break;
            case "grep":
                absApp = new GrepApplication();
                break;
            case "paste":
            	absApp = new PasteApplication();
            	break;
            case "diff":
            	absApp = new DiffApplication();
            	break;
            case "sed":
                absApp = new SedApplication();
                break;
            case "cmp":
            	absApp = new CmpApplication();
            	break;
            case "cd":
            	absApp = new CdApplication();
            	break;
            case "exit":
            	absApp = new ExitApplication();
            	break;
            case "split":
                absApp = new SplitApplication();
                break;
            default:
                throw new ShellException(app + ": " + EXP_INVALID_APP);
        }

        absApp.run(argsArray, inputStream, outputStream);
    }

    /**
     * Static method to creates an inputStream based on the file name or file
     * path.
     *
     * @param inputStreamS String of file name or file path
     * @return InputStream of file opened
     * @throws ShellException If file is not found.
     */
    @SuppressWarnings({ "PMD.PreserveStackTrace", "PMD.AvoidDuplicateLiterals" })
	public static InputStream openInputRedir(String inputStreamS)
            throws ShellException {
        File inputFile = new File(inputStreamS);
        FileInputStream fInputStream = null;
        try {
            fInputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            throw new ShellException(e.getMessage());
        }
        return fInputStream;
    }

    /**
     * Static method to creates an outputStream based on the file name or file
     * path.
     *
     * @param outputStreamS String of file name or file path.
     * @return OutputStream of file opened.
     * @throws ShellException If file destination cannot be opened or inaccessible.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
	public static OutputStream openOutputRedir(String outputStreamS) throws ShellException {
        File outputFile = new File(outputStreamS);
        FileOutputStream fOutputStream = null;
        try {
            fOutputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new ShellException(e.getMessage());
        }
        return fOutputStream;
    }

    /**
     * Static method to close an inputStream.
     *
     * @param inputStream InputStream to be closed.
     * @throws ShellException If inputStream cannot be closed successfully.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
	public static void closeInputStream(InputStream inputStream)
            throws ShellException {
        if (inputStream != System.in) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new ShellException(e.getMessage());
            }
        }
    }

    /**
     * Static method to close an outputStream. If outputStream provided is
     * System.out, it will be ignored.
     *
     * @param outputStream OutputStream to be closed.
     * @throws ShellException If outputStream cannot be closed successfully.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
	public static void closeOutputStream(OutputStream outputStream)
            throws ShellException {
        if (outputStream != System.out) {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new ShellException(e.getMessage());
            }
        }
    }

    /**
     * Static method to write output of an outputStream to another outputStream,
     * usually System.out.
     *
     * @param outputStream Source outputStream to get stream from.
     * @param stdout       Destination outputStream to write stream to.
     * @throws ShellException If exception is thrown during writing.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
	public static void writeToStdout(OutputStream outputStream,
                                     OutputStream stdout) throws ShellException {
        if (outputStream instanceof FileOutputStream) {
            return;
        }
        try {
            stdout.write(((ByteArrayOutputStream) outputStream).toByteArray());
        } catch (IOException e) {
            throw new ShellException(EXP_STDOUT);
        }
    }

    /**
     * Static method to pipe data from an outputStream to an inputStream, for
     * the evaluation of the Pipe Commands.
     *
     * @param outputStream Source outputStream to get stream from.
     * @return InputStream with data piped from the outputStream.
     * @throws ShellException If exception is thrown during piping.
     */
    public static InputStream outputStreamToInputStream(
            OutputStream outputStream) throws ShellException {
        return new ByteArrayInputStream(
                ((ByteArrayOutputStream) outputStream).toByteArray());
    }

    /**
     * Main method for the Shell Interpreter program.
     *
     * @param args List of strings arguments, unused.
     */

    public static void main(String... args) {
        ShellImpl shell = new ShellImpl();

        BufferedReader bReader = new BufferedReader(new InputStreamReader(
                System.in));
        String readLine = null;
        String currentDir;

        while (true) {
            try {
                currentDir = Environment.currentDirectory;
                System.out.print(currentDir + ">");
                readLine = bReader.readLine();
                if (readLine == null) {
                    break;
                }
                if (("").equals(readLine)) {
                    continue;
                }

                shell.parseAndEvaluate(readLine, System.out);

            } catch (Exception e) {
                // TODO: need to change to e.printMessage on production
                e.printStackTrace();
            }
        }
    }

    @Override
    public void parseAndEvaluate(String cmdline, final OutputStream stdout)
            throws AbstractApplicationException, ShellException {

        //Arrays.stream(globFilesDirectories("test/**/*.java").split(" ")).forEach(System.out::println);

        InputStream inputPipe = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        OutputStream outputPipe = new ByteArrayOutputStream();

        String[] sequencedCommands = extractSemicolon(cmdline);

        PipeCommand pipeCommand = null;
        for (String cmd: sequencedCommands) {
            pipeCommand = new PipeCommand(cmd);
            pipeCommand.parse();
            pipeCommand.evaluate(inputPipe, outputPipe);
        }
        writeToStdout(pipeCommand.getResultStream(), stdout);
    }

    // TODO: Can consider extract to different class
    @SuppressWarnings("PMD.ExcessiveMethodLength")
	public String[] extractSemicolon(String cmdline){
        // GUARDS:
        // does not contain semicolon, don't evaluate
        if (!cmdline.contains(";")) {
        	return new String[]{cmdline};
        }

        // if the command is of form <command> '<single_quote_content>', don't evaluate
        Pattern singleQuote = Pattern.compile("(?:.+)\\s+'(?:.*)'");
        if (singleQuote.matcher(cmdline).matches()) {
        	return new String[]{cmdline};
        }

        // if the command is of form <command> "<double_quote_content>", don't evaluate
        Pattern doubleQuote = Pattern.compile("(?:.+)\\s+\"(?:.*)\"");
        if (doubleQuote.matcher(cmdline).matches()) {
        	return new String[]{cmdline};
        }
        // END GUARDS

        // cmdline matches sequenced command pattern
        List<String> validCommands = new ArrayList<>();

        // this regex will match all the semicolons NOT within backtick
		@SuppressWarnings("PMD.LongVariable")
		Pattern semicolonNotInsideBQPattern = Pattern.compile("(?!\\B`[^`]*);(?![^`]*`\\B)");
        Matcher matcher = semicolonNotInsideBQPattern.matcher(cmdline);

        // for each semicolons that matches, we extract the substring up to that semicolon position
		@SuppressWarnings("PMD.LongVariable")
		int lastValidCommandStartIndex = 0;
        while (matcher.find()) {
            String validCommand = cmdline.substring(lastValidCommandStartIndex, matcher.start());
            validCommands.add(validCommand);

            lastValidCommandStartIndex = matcher.end();
        }

        // evaluate last portion that maybe have no semicolon
        // eg: echo hello; echo world
        // as such the word has to be added into valid command even if there's no semicolon precedes it
        String validCommand = cmdline.substring(lastValidCommandStartIndex);

        if (!validCommand.isEmpty()) {
            // this happens when valid command such as:
            // echo hello;
            // as such we will not add the empty string into valid command
            validCommands.add(validCommand);
        }


        return validCommands.toArray(new String[]{});
    }
}
