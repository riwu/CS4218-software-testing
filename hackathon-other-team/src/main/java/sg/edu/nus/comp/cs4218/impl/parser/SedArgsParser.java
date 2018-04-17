package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.SedException;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SedArgsParser extends ArgsParser {
    private static final String START = "s";

    private String regex;
    private String replacement;
    private int matchIndex;

    @Override
    public void parse(String... args) throws InvalidArgsException {
        super.parse(args);

        parseSedCommand(nonFlagArgs.get(0));
    }

    @Override
    public void validateArgs() throws InvalidArgsException {
        super.validateArgs();

        if (nonFlagArgs.size() > 2) {
            throw new InvalidArgsException("currently only supports one file");
        }
    }

    public int getMatchIndex() {
        return matchIndex;
    }

    public String getRegex() {
        return regex;
    }

    public String getReplacement() {
        return replacement;
    }

    /**
     * Parses the sed command into its separate components.
     *
     * @param arg
     *
     * @throws SedException
     */
    private void parseSedCommand(String arg) throws InvalidArgsException {
        if (arg.length() <= 1) {
            throw new InvalidArgsException("invalid length of command string");
        }

        evaluateStartCharacter(arg);

        // Get the delimiter used
        String delimiter = Character.toString(arg.charAt(1));

        String[] tokens = arg.split(delimiter, 4);

        // Guard against invalid number of tokens/end character
        if (tokens.length < 4) {
            throw new InvalidArgsException("invalid expression - end character");
        }

        evaluateRegex(tokens[1]);
        evaluateReplacement(tokens[2]);
        evaluateMatchIndex(tokens[3]);
    }

    /**
     * Throws an exception if the start character is invalid.
     *
     * @param start
     *
     * @throws SedException
     */
    private void evaluateStartCharacter(String arg) throws InvalidArgsException {
        if (!Character.toString(arg.charAt(0)).equals(START)) {
            throw new InvalidArgsException(
                    String.format("invalid expression - start character %s", arg.charAt(0)));
        }
    }

    /**
     * Throws an exception if the regex string is invalid.
     *
     * @param regex
     *
     * @throws SedException
     */
    private void evaluateRegex(String regex) throws InvalidArgsException {
        if (regex.isEmpty()) {
            throw new InvalidArgsException("char 0: no previous regular expression");
        }

        try {
            Pattern.compile(regex);
            this.regex = regex;
        } catch (PatternSyntaxException e) {
            throw new InvalidArgsException("invalid regex expression", e);
        }
    }

    /**
     * Sets the replacement string.
     *
     * @param replacement
     */
    private void evaluateReplacement(String replacement) {
        this.replacement = replacement;
    }

    /**
     * Throws an exception if the match index is invalid.
     *
     * @param index
     *
     * @throws SedException
     */
    private void evaluateMatchIndex(String index) throws InvalidArgsException {
        if (index.isEmpty()) {
            matchIndex = 1;
            return;
        }

        try {
            matchIndex = Integer.parseInt(index);
        } catch (Exception e) {
            throw new InvalidArgsException(String.format("match index not an integer %s", index),
                                           e);
        }

        if (matchIndex == 0) {
            throw new InvalidArgsException("match index may not be zero");
        }
    }
}
