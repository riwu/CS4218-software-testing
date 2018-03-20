package sg.edu.nus.comp.cs4218.impl.app;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SedException;

@SuppressWarnings("PMD.GodClass")
public class SedApplication implements SedInterface {
	public static final String NO_INDEX = "-1";

	@Override
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (stdout == null) {
            throw new SedException("null stdout");
        }
        ArrayList<String> parsedArg = parseArgs(args);
        String result = "";

        // no input file, get from stdin
        if (parsedArg.size() == 3) {
            if (stdin == null) {
                throw new SedException("Input arg missing");
            }
            try {
                replaceSubstringInStdin(parsedArg.get(0), parsedArg.get(1), Integer.parseInt(parsedArg.get(2)), stdin);
            } catch (Exception e) {
                throw new SedException(e.getMessage());
            }
        } else {
            try {
                result = replaceSubstringInFile(parsedArg.get(0), parsedArg.get(1), Integer.parseInt(parsedArg.get(2)), parsedArg.get(3));
            } catch (Exception e) {
                throw new SedException(e.getMessage());
            }
        }


        try {
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new SedException(e.getMessage());
        }
    }

	@Override
	public String replaceSubstringInFile(String pattern, String replacement, int replacementIndex, String fileName)
			throws Exception {
		Path currentDir = Paths.get(Environment.currentDirectory);
        File file = currentDir.resolve(fileName).toFile();
        if (!file.exists()) {
            throw new Exception("file does not exist");
        }
        if (file.isDirectory()) {
            throw new Exception("file is a directory");
        }
        BufferedReader bReader = new BufferedReader(new FileReader(file));
        StringBuilder strBuilder = new StringBuilder();
        String line;

        while ((line = bReader.readLine()) != null) {
            if (pattern.isEmpty()) {
                strBuilder.append(line).append(System.lineSeparator());
                continue;
            }
            String replaced = replaceLine(pattern, replacement, replacementIndex, line);
            strBuilder.append(replaced).append(System.lineSeparator());
        }
        bReader.close();
        return strBuilder.toString();
	}

	@Override
	public String replaceSubstringInStdin(String pattern, String replacement, int replacementIndex, InputStream stdin)
			throws Exception {
        Scanner fileScanner = new Scanner(stdin);
        String filename = fileScanner.nextLine();
        Path currentDir = Paths.get(Environment.currentDirectory);
        File file = currentDir.resolve(filename).toFile();
        fileScanner.close();
        if (!file.exists()) {
            throw new Exception("Invalid file");
        }
        if (file.isDirectory()) {
            throw new Exception("stdin is directory");
        }
        return replaceSubstringInFile(pattern, replacement, replacementIndex, filename);
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private ArrayList<String> parseArgs(String... args) throws SedException {
        if (args.length == 0) {
	        throw new SedException("Missing sed argument");
        }
		String firstArg = args[0];
		if (firstArg.length() < 2) {
			throw new SedException("Invalid sed syntax: s/ is mssing");
		}
		String delimiter = Character.toString(firstArg.charAt(1));
		String sedArgString = String.join(" ", args);
		// min s///
		if (sedArgString.length() < 4) {
		    throw new SedException("Invalid sed syntax: Missing arguments");
        }
        ArrayList<String> sedArgs = splitArg(sedArgString, delimiter);
        ArrayList<String> parsedArg;

        if (!validateInput(sedArgs, delimiter)) {
		    throw new SedException("Invalid sed argument");
        }
        parsedArg = new ArrayList<>(Arrays.asList(sedArgs.get(1), sedArgs.get(2)));


        // no filename and replacementIndex
		if (sedArgs.size() == 3) {
            parsedArg.add(NO_INDEX);
            return parsedArg;
        }
		else if (sedArgs.size() != 4) {
			throw new SedException("Invalid sed syntax");
		}
        ArrayList<String> indexAndFilename = parseFileArg(sedArgs.get(3));
        parsedArg.add(indexAndFilename.get(0));

		// only have replacementIndex no filename
		if (indexAndFilename.size() == 1) {
            return parsedArg;
        } else { // have filename
		    parsedArg.add(indexAndFilename.get(1));
        }

        // check for valid file
        String filename = indexAndFilename.get(1);
        File file = new File(filename);

        if (!file.exists()) {
            throw new SedException("Invalid input file");
        }
        if (file.isDirectory()) {
            throw new SedException("Input file is a directory");
        }

        // no errors return
		// regex, replacement, index, filename
		return parsedArg;
	}

	private ArrayList<String> parseFileArg(String fileArg) throws SedException {
		ArrayList<String> parsedArgs = new ArrayList<>();
        int index = fileArg.indexOf(' ');

        if (index < 0) {
		    if (isValidReplacementIndex(fileArg)) {
		        parsedArgs.add(fileArg);
		        return parsedArgs;
            } else {
		        throw new SedException("Invalid Sed Syntax");
            }
        } else if (index == 0) {
		    parsedArgs.add(NO_INDEX);
		    parsedArgs.add(fileArg.substring(1));
			return parsedArgs;
		}

        // check if the spacing is because of filename
		String replaceDigit = fileArg.substring(0, index);
        String filename = fileArg.substring(index + 1);

        if (isValidReplacementIndex(replaceDigit)) {
            parsedArgs.add(replaceDigit);
            parsedArgs.add(filename);
        } else {
            parsedArgs.add(NO_INDEX);
            parsedArgs.add(fileArg);
        }
		return parsedArgs;
	}

	private boolean validateInput(ArrayList<String> args, String delimiter) {
        if (!(args.size() == 3 || args.size() == 4)) {
            return false;
        }
        for (int i=0; i < 3; i++) {
	        if (i==0) {
                if (!args.get(i).equals("s")) {
                    return false;
                }
            } else {
	            if (args.get(i).contains(delimiter)) {
                    return false;
                }
            }
        }
        return true;
    }

	private String replaceLine(String regex, String replacement, int index, String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        StringBuffer strBuilder = new StringBuffer(line.length());

        if (index == 0) {
            return line;
        }
        for (int i=0; i<index - 1; i++) {
            if (!matcher.find()) {
                break;
            }
        }


        if (matcher.find()) {
            matcher.appendReplacement(strBuilder, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(strBuilder);
        return strBuilder.toString();
    }

    private boolean isValidReplacementIndex(String digit) throws SedException {
        for (int i=0; i<digit.length(); i++) {
            if (i == 0 && digit.charAt(i) == '-') {
                throw new SedException("Invalid replacement index");
            }
            if (!Character.isDigit(digit.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> splitArg(String input, String delimiter) {
	    ArrayList<String> splitInput = new ArrayList<>();
	    int startIndex = 0;
	    int count = 3;

	    for (int i=0; i<input.length(); i++) {
	        if (input.charAt(i) == delimiter.charAt(0)) {
	            splitInput.add(input.substring(startIndex, i));
	            startIndex = i + 1;
	            count -= 1;
	            if (count == 0) {
	                break;
                }
            }
        }
        String lastSlice = input.substring(startIndex);
	    if (!lastSlice.isEmpty()) {
	        splitInput.add(lastSlice);
        }

        return splitInput;
    }

}
