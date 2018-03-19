package sg.edu.nus.comp.cs4218.impl.app;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SedException;

public class SedApplication implements SedInterface {
	public static String REPLACE_ALL_INDEX = "-1";

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		// TODO Auto-generated method stub
        if (stdout == null) {
            throw new SedException("null stdout");
        }
        ArrayList<String> parsedArg = parseArgs(args);
        System.out.println(Arrays.toString(parsedArg.toArray()));
        String result = "";

        // no input file, get from stdin
        if (parsedArg.size() == 3) {
            System.out.println("stdin" + stdin);
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
		// TODO Auto-generated method stub
        File file = new File(fileName);
        if (!file.exists()) {
            throw new SedException("file does not exist");
        } else if (file.isDirectory()) {
            throw new SedException("file is a directory");
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            String replaced = replaceLine(pattern, replacement, replacementIndex, line);
            sb.append(replaced).append(System.lineSeparator());
        }
        return sb.toString();
	}

	@Override
	public String replaceSubstringInStdin(String pattern, String replacement, int replacementIndex, InputStream stdin)
			throws Exception {
        Scanner fileScanner = new Scanner(stdin);
        String filename = fileScanner.nextLine();
        File file = new File(filename);
        if (!file.exists()) {
            throw new SedException("Invalid file");
        } else if (file.isDirectory()) {
            throw new SedException("stdin is directory");
        }
        return replaceSubstringInFile(pattern, replacement, replacementIndex, filename);
	}

	private ArrayList<String> parseArgs(String[] args) throws SedException {
		String firstArg = args[0];
		if (firstArg.length() < 2) {
			throw new SedException("Invalid sed syntax");
		}
		String delimiter = Character.toString(firstArg.charAt(1));
		String sedArgString = String.join(" ", args);
		String[] sedArgs = sedArgString.split(delimiter);
        ArrayList<String> parsedArg;

		if (!validateInput(sedArgs, delimiter)) {
		    throw new SedException("sed Argument should not contain delimiter char");
        }
        parsedArg = new ArrayList<>(Arrays.asList(sedArgs[1], sedArgs[2]));

        // no filename and replacementIndex
		if (sedArgs.length == 3) {
		    parsedArg.add(REPLACE_ALL_INDEX);
            return parsedArg;
        }
		else if (sedArgs.length != 4) {
			throw new SedException("Invalid sed syntax");
		}
        ArrayList<String> indexAndFilename = parseFileArg(sedArgs[3]);
        parsedArg.add(indexAndFilename.get(0));

		// only have replacementIndex no filename
		if (indexAndFilename.size() == 1) {
            return parsedArg;
        } else { // have filename
		    parsedArg.add(indexAndFilename.get(1));
        }

        // check for valid file
        String filename = indexAndFilename.get(1);
        File f = new File(filename);
        if (!f.exists()) {
            throw new SedException("Invalid input file");
        }
        else if (f.isDirectory()) {
            throw new SedException("Input file is a directory");
        }

        // no errors return
		// regex, replacement, index, filename
		return parsedArg;
	}

	private ArrayList<String> parseFileArg(String fileArg) throws SedException {
		ArrayList<String> parsedArgs = new ArrayList<>();

		int index = fileArg.indexOf(' ');
		if (index <= 0) {
		    parsedArgs.add(fileArg);
			return parsedArgs;
		}

		String replaceDigit = fileArg.substring(0, index);
        String filename = fileArg.substring(index + 1);
        for (int i=0; i<replaceDigit.length(); i++) {
		    if (i == 0 && replaceDigit.charAt(i) == '-') {
		        throw new SedException("Invalid replacementIndex");
            }
		    if (!Character.isDigit(replaceDigit.charAt(i))) {
                parsedArgs.add(fileArg.substring(1));
                return parsedArgs;
            }
        }

        if (replaceDigit.equals("")) {
		    parsedArgs.add(REPLACE_ALL_INDEX);
        } else {
            parsedArgs.add(replaceDigit);
        }
		parsedArgs.add(filename);
		return parsedArgs;
	}

	private boolean validateInput(String[] args, String delimiter) {
        for (int i=0; i < 3; i++) {
	        if (i==0) {
                if (!args[i].equals("s")) {
                    return false;
                }
            } else {
	            if (args[i].contains(delimiter)) {
                    return false;
                }
            }
        }
        return true;
    }

	private String replaceLine(String regex, String replacement, int index, String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer(line.length());

        if (index == 0) {
            return line;
        }
        for (int i=0; i<index - 1; i++) {
            if (!matcher.find()) {
                break;
            }
        }

        if (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

}
