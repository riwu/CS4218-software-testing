package sg.edu.nus.comp.cs4218.test.integration.structural;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.test.stub.ApplicationRunnerStub;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

/**
 * This integration test suite ensures that CommandBuilder correctly parses a command string.
 * This involves recognizing and tokenizing arguments that may contain special characters.
 *
 * At the command level, CommandBuilder:
 * - Extracts call / pipe / sequence commands and chains them together.
 * - Throws an exception when the command syntax is invalid.
 *
 * At the call command level, CommandBuilder:
 * - Isolates redirection operators into separate tokens, for further parsing later on. It does
 * not validate the number and position of redirection operators in the command.
 * - Tokenizes well-formed quotes and throws an exception when there are mismatched quotes.
 */
class CommandParsingIntegrationTest {
    private static final String APP = "app";
    private static final String[] ARGS = {"abc", "xyz", "123", "999"};
    private static final String MSG_SYNTAX = "shell: " + ERR_SYNTAX;
    private final ApplicationRunnerStub appRunnerStub = new ApplicationRunnerStub();

    @Test
    void testBlankCommand() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("    ", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    @Test
    void testCommandWithNoValidArgs() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("| < > ;", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    @Test
    void testCommandWithNewline() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app arg1" + STRING_NEWLINE + "arg2", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Quoting
     */
    @Test
    void testCommandWithMismatchedSingleQuotes() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app arg1 'arg2", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Quoting
     */
    @Test
    void testCommandWithMismatchedDoubleQuotes() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app arg1 \"arg2", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Quoting
     */
    @Test
    void testCommandWithMismatchedBackQuotes() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app arg1 `arg2", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * IO Redirection
     */
    @Test
    void testCallCommandWithRedirArgs() throws Exception {
        Command command = CommandBuilder
                .parseCommand("app -ABC arg1/* <file1 > file2", appRunnerStub);
        assertTrue(command instanceof CallCommand);

        List<String> expectedArgsList = Arrays
                .asList(APP, "-ABC", "arg1/*", "<", "file1", ">", "file2");
        List<String> argsList = ((CallCommand) command).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    @Test
    void testCallCommandWithNoExtraArgs() throws Exception {
        Command command = CommandBuilder.parseCommand(APP, appRunnerStub);
        assertTrue(command instanceof CallCommand);

        List<String> expectedArgsList = Arrays.asList(APP);
        List<String> argsList = ((CallCommand) command).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    @Test
    void testCallCommandWithExtraWhitespace() throws Exception {
        Command command = CommandBuilder.parseCommand("  app  -ABC arg1   arg2 ", appRunnerStub);
        assertTrue(command instanceof CallCommand);

        List<String> expectedArgsList = Arrays.asList(APP, "-ABC", "arg1", "arg2");
        List<String> argsList = ((CallCommand) command).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    /**
     * Quoting
     */
    @Test
    void testCallCommandWithQuotes() throws Exception {
        Command command = CommandBuilder
                .parseCommand("app 'a\"rg'1 arg2\" \" `arg 3`", appRunnerStub);
        assertTrue(command instanceof CallCommand);

        List<String> expectedArgsList = Arrays.asList(APP, "'a\"rg'1", "arg2\" \"", "`arg 3`");
        List<String> argsList = ((CallCommand) command).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    /**
     * Quoting
     */
    @Test
    void testNoPipeCommandIfQuoted() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc '| xyz'", appRunnerStub);
        assertTrue(command instanceof CallCommand);

        List<String> expectedArgsList = Arrays.asList(APP, ARGS[0], "'| xyz'");
        List<String> argsList = ((CallCommand) command).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    /**
     * Quoting
     */
    @Test
    void testNoSequenceCommandIfQuoted() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc \";\" xyz", appRunnerStub);
        assertTrue(command instanceof CallCommand);

        List<String> expectedArgsList = Arrays.asList(APP, ARGS[0], "\";\"", ARGS[1]);
        List<String> argsList = ((CallCommand) command).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    /**
     * Pipe
     */
    @Test
    void testPipeCommandWithTwoCallCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc | app xyz", appRunnerStub);
        assertTrue(command instanceof PipeCommand);

        List<CallCommand> callCommands = ((PipeCommand) command).getCallCommands();
        assertEquals(2, callCommands.size());

        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[0]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[1]);
        List<String> argsList1 = callCommands.get(0).getArgsList();
        List<String> argsList2 = callCommands.get(1).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));
    }

    /**
     * Pipe
     */
    @Test
    void testPipeCommandWithMultipleCallCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc|app xyz|app 123", appRunnerStub);
        assertTrue(command instanceof PipeCommand);

        List<CallCommand> callCommands = ((PipeCommand) command).getCallCommands();
        assertEquals(3, callCommands.size());

        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[0]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[1]);
        List<String> expectedArgsList3 = Arrays.asList(APP, ARGS[2]);
        List<String> argsList1 = callCommands.get(0).getArgsList();
        List<String> argsList2 = callCommands.get(1).getArgsList();
        List<String> argsList3 = callCommands.get(2).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));
        assertTrue(expectedArgsList3.equals(argsList3));
    }

    /**
     * Pipe
     */
    @Test
    void testPipeCommandWithoutFirstHalf() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("| app xyz", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Pipe
     */
    @Test
    void testPipeCommandWithoutSecondHalf() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app abc |", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Pipe
     */
    @Test
    void testPipeCommandWithTwoPipeOperators() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app abc | | app xyz", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Pipe + Quoting + IO Redirection
     */
    @Test
    void testPipeCommandWithCallCommandRedirection() throws Exception {
        // syntax for the first half is valid. nothing will be written to the pipe.
        // syntax for the second half is valid, but that's left to CallCommand evaluation.
        Command command = CommandBuilder.parseCommand("app '123 456'>abc | app xyz<>999",
                                                      appRunnerStub);
        assertTrue(command instanceof PipeCommand);

        List<CallCommand> callCommands = ((PipeCommand) command).getCallCommands();
        assertEquals(2, callCommands.size());

        List<String> expectedArgsList1 = Arrays.asList(APP, "'123 456'", ">", "abc");
        List<String> expectedArgsList2 = Arrays.asList(APP, "xyz", "<", ">", "999");
        List<String> argsList1 = callCommands.get(0).getArgsList();
        List<String> argsList2 = callCommands.get(1).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));
    }

    /**
     * Sequence
     */
    @Test
    void testSequenceCommandWithTwoCallCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc ; app xyz", appRunnerStub);
        assertTrue(command instanceof SequenceCommand);

        List<Command> commands = ((SequenceCommand) command).getCommands();
        assertEquals(2, commands.size());

        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[0]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[1]);
        List<String> argsList1 = ((CallCommand) commands.get(0)).getArgsList();
        List<String> argsList2 = ((CallCommand) commands.get(1)).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));
    }

    /**
     * Sequence
     */
    @Test
    void testSequenceCommandWithMultipleCallCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc;app xyz;app 123", appRunnerStub);
        assertTrue(command instanceof SequenceCommand);

        List<Command> commands = ((SequenceCommand) command).getCommands();
        assertEquals(3, commands.size());

        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[0]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[1]);
        List<String> expectedArgsList3 = Arrays.asList(APP, ARGS[2]);
        List<String> argsList1 = ((CallCommand) commands.get(0)).getArgsList();
        List<String> argsList2 = ((CallCommand) commands.get(1)).getArgsList();
        List<String> argsList3 = ((CallCommand) commands.get(2)).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));
        assertTrue(expectedArgsList3.equals(argsList3));
    }

    /**
     * Sequence
     */
    @Test
    void testSequenceCommandWithoutFirstHalf() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("; app xyz", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Sequence
     */
    @Test
    void testSequenceCommandWithoutSecondHalf() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app abc ;", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Sequence
     */
    @Test
    void testSequenceCommandWithTwoSequenceOperators() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app abc;;app xyz", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Sequence + Pipe
     */
    @Test
    void testSequenceCommandWithoutMalformedFirstHalf() {
        Throwable exception = assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("app abc | ; app xyz", appRunnerStub);
        });

        assertEquals(MSG_SYNTAX, exception.getMessage());
    }

    /**
     * Sequence + Pipe
     */
    @Test
    void testSequenceCommandWithPipeAndCallCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc | app xyz ; app 123", appRunnerStub);
        assertTrue(command instanceof SequenceCommand);

        List<Command> commands = ((SequenceCommand) command).getCommands();
        assertEquals(2, commands.size());

        Command command1 = commands.get(0);
        assertTrue(command1 instanceof PipeCommand);
        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[0]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[1]);
        List<String> argsList1 = ((PipeCommand) command1).getCallCommands().get(0).getArgsList();
        List<String> argsList2 = ((PipeCommand) command1).getCallCommands().get(1).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));

        Command command2 = commands.get(1);
        assertTrue(command2 instanceof CallCommand);
        List<String> expectedArgsList = Arrays.asList(APP, ARGS[2]);
        List<String> argsList = ((CallCommand) command2).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));
    }

    /**
     * Sequence + Pipe
     */
    @Test
    void testSequenceCommandWithCallAndPipeCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc ; app xyz | app 123", appRunnerStub);
        assertTrue(command instanceof SequenceCommand);

        List<Command> commands = ((SequenceCommand) command).getCommands();
        assertEquals(2, commands.size());

        Command command1 = commands.get(0);
        assertTrue(command1 instanceof CallCommand);
        List<String> expectedArgsList = Arrays.asList(APP, ARGS[0]);
        List<String> argsList = ((CallCommand) command1).getArgsList();
        assertTrue(expectedArgsList.equals(argsList));

        Command command2 = commands.get(1);
        assertTrue(command2 instanceof PipeCommand);
        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[1]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[2]);
        List<String> argsList1 = ((PipeCommand) command2).getCallCommands().get(0).getArgsList();
        List<String> argsList2 = ((PipeCommand) command2).getCallCommands().get(1).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));
    }

    /**
     * Sequence + Pipe
     */
    @Test
    void testSequenceCommandWithPipeAndPipeCommands() throws Exception {
        Command command = CommandBuilder.parseCommand("app abc | app xyz ; app 123 | app 999",
                                                      appRunnerStub);
        assertTrue(command instanceof SequenceCommand);

        List<Command> commands = ((SequenceCommand) command).getCommands();
        assertEquals(2, commands.size());

        Command command1 = commands.get(0);
        assertTrue(command1 instanceof PipeCommand);
        List<String> expectedArgsList1 = Arrays.asList(APP, ARGS[0]);
        List<String> expectedArgsList2 = Arrays.asList(APP, ARGS[1]);
        List<String> argsList1 = ((PipeCommand) command1).getCallCommands().get(0).getArgsList();
        List<String> argsList2 = ((PipeCommand) command1).getCallCommands().get(1).getArgsList();
        assertTrue(expectedArgsList1.equals(argsList1));
        assertTrue(expectedArgsList2.equals(argsList2));

        Command command2 = commands.get(1);
        assertTrue(command2 instanceof PipeCommand);
        List<String> expectedArgsList3 = Arrays.asList(APP, ARGS[2]);
        List<String> expectedArgsList4 = Arrays.asList(APP, ARGS[3]);
        List<String> argsList3 = ((PipeCommand) command2).getCallCommands().get(0).getArgsList();
        List<String> argsList4 = ((PipeCommand) command2).getCallCommands().get(1).getArgsList();
        assertTrue(expectedArgsList3.equals(argsList3));
        assertTrue(expectedArgsList4.equals(argsList4));
    }
}
