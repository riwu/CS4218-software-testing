package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

@SuppressWarnings("PMD.ExcessiveMethodLength")
public final class ArgumentResolver {
    private ArgumentResolver() {
    }

    /**
     * Handle quoting + globing + command substitution for a list of arguments.
     *
     * @param argsList The original list of arguments.
     *
     * @return The list of parsed arguments.
     *
     * @throws ShellException If any of the arguments have an invalid syntax.
     */
    public static List<String> parseArguments(List<String> argsList) throws ShellException {
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        List<String> parsedArgsList = new LinkedList<>();

        for (String arg : argsList) {
            List<String> parsedArgsSegment = resolveOneArgument(arg);
            parsedArgsList.addAll(parsedArgsSegment);
        }

        return parsedArgsList;
    }

    /**
     * Unwraps single and double quotes from one argument.
     * Performs globing when there are unquoted asterisks.
     * Performs command substitution.
     *
     * Single quotes disable the interpretation of all special characters.
     * Double quotes disable the interpretation of all special characters, except for back quotes.
     *
     * @param arg String containing one argument.
     *
     * @return A list containing one or more parsed args, depending on the outcome of the parsing.
     *
     * @throws ShellException If there are any mismatched quotes.
     */
    public static List<String> resolveOneArgument(String arg) throws ShellException {
        Stack<Character> unmatchedQuotes = new Stack<>();
        LinkedList<RegexArgument> parsedArgsSegment = new LinkedList<>();
        RegexArgument parsedArg = new RegexArgument();
        StringBuilder subCommand = new StringBuilder();

        for (int i = 0; i < arg.length(); i++) {
            char chr = arg.charAt(i);

            if (chr == CHAR_BACK_QUOTE) {
                if (unmatchedQuotes.isEmpty() || unmatchedQuotes.peek() == CHAR_DOUBLE_QUOTE) {
                    // start of command substitution
                    unmatchedQuotes.push(chr);

                    // append current parsedArg to the previous parsedArg
                    // if parsedArgsSegment is empty, then just add current parsedArg
                    // see e.g. below for further explanation

                    // TODO should we be checking this?
                    // echo a '' b --should give--> "a  b"
                    // echo a `` b --should give--> "a b"
                    if (!parsedArg.isEmpty()) {
                        if (parsedArgsSegment.isEmpty()) {
                            parsedArgsSegment.add(parsedArg);
                        } else {
                            RegexArgument lastParsedArg = parsedArgsSegment.removeLast();
                            lastParsedArg.merge(parsedArg);
                            parsedArgsSegment.add(lastParsedArg);
                        }
                        parsedArg = new RegexArgument();
                    }
                } else if (unmatchedQuotes.peek() == chr) {
                    // end of command substitution
                    unmatchedQuotes.pop();

                    // TODO clean up this section
                    // evaluate subCommand and get the output
                    String subCommandOutput = evaluateSubCommand(subCommand.toString());
                    subCommand = new StringBuilder();

                    if (unmatchedQuotes.isEmpty()) {
                        List<String> subOutputSegment = new LinkedList<>(
                                Arrays.asList(StringUtils.tokenize(subCommandOutput)));

                        // append the first token to the previous parsedArg
                        // e.g. arg: abc`1 2 3`xyz`4 5 6` (contents in `` is after command sub)
                        // expected: [abc1, 2, 3xyz4, 5, 6]
                        if (!parsedArgsSegment.isEmpty() && !subOutputSegment.isEmpty()) {
                            RegexArgument lastParsedArg = parsedArgsSegment.removeLast();
                            String firstOutputArg = subOutputSegment.remove(0);
                            lastParsedArg.merge(firstOutputArg);
                            parsedArgsSegment.add(lastParsedArg);
                        }

                        // add remaining tokens to parsedArgsSegment
                        parsedArgsSegment.addAll(
                                subOutputSegment.stream()
                                                .map(str -> new RegexArgument(str))
                                                .collect(Collectors.toList()));
                    } else {
                        if (parsedArgsSegment.isEmpty()) {
                            parsedArgsSegment.add(new RegexArgument(subCommandOutput));
                        } else {
                            RegexArgument lastParsedArg = parsedArgsSegment.removeLast();
                            lastParsedArg.merge(subCommandOutput);
                            parsedArgsSegment.add(lastParsedArg);
                        }
                    }
                } else {
                    // ongoing single quote
                    parsedArg.append(chr);
                }
            } else if (chr == CHAR_SINGLE_QUOTE || chr == CHAR_DOUBLE_QUOTE) {
                if (unmatchedQuotes.isEmpty()) {
                    // start of quote
                    unmatchedQuotes.push(chr);
                } else if (unmatchedQuotes.peek() == chr) {
                    // end of quote
                    unmatchedQuotes.pop();
                } else if (unmatchedQuotes.peek() == CHAR_BACK_QUOTE) {
                    // ongoing back quote: add chr to subCommand
                    subCommand.append(chr);
                } else {
                    // ongoing single/double quote
                    parsedArg.append(chr);
                }
            } else if (chr == CHAR_ASTERISK) {
                if (unmatchedQuotes.isEmpty()) {
                    // each unquoted * matches a (possibly empty) sequence of non-slash chars
                    parsedArg.appendAsterisk();
                } else if (unmatchedQuotes.peek() == CHAR_BACK_QUOTE) {
                    // ongoing back quote: add chr to subCommand
                    subCommand.append(chr);
                } else {
                    // ongoing single/double quote
                    parsedArg.append(chr);
                }
            } else {
                if (unmatchedQuotes.isEmpty()) {
                    // not a special character
                    parsedArg.append(chr);
                } else if (unmatchedQuotes.peek() == CHAR_BACK_QUOTE) {
                    // ongoing back quote: add chr to subCommand
                    subCommand.append(chr);
                } else {
                    // ongoing single/double quote
                    parsedArg.append(chr);
                }
            }
        }

        if (!unmatchedQuotes.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        // TODO this is duplicate code from above. maybe DRY this up?
        if (!parsedArg.isEmpty()) {
            if (parsedArgsSegment.isEmpty()) {
                parsedArgsSegment.add(parsedArg);
            } else {
                RegexArgument lastParsedArg = parsedArgsSegment.removeLast();
                lastParsedArg.merge(parsedArg);
                parsedArgsSegment.add(lastParsedArg);
            }
        }

        // perform globing
        return parsedArgsSegment.stream()
                                .flatMap(regexArgument -> regexArgument.globFiles().stream())
                                .collect(Collectors.toList());
    }

    private static String evaluateSubCommand(String commandString) {
        if (StringUtils.isBlank(commandString)) {
            return "";
        }

        OutputStream outputStream = new ByteArrayOutputStream();
        String output;

        try {
            Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
            command.evaluate(System.in, outputStream);
            output = outputStream.toString();
        } catch (AbstractApplicationException | ShellException e) {
            output = e.getMessage();
        }

        // replace newlines with spaces
        return output.replace(STRING_NEWLINE, String.valueOf(CHAR_SPACE));
    }
}
