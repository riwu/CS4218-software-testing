package sg.edu.nus.comp.cs4218.impl.cmd;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class PipeCommand implements Command {

    private Queue<CallCommand> commandQ;
    private OutputStream resultStream;

    public PipeCommand(String cmdline) {
        this.commandQ = new LinkedList<>();

        try {
            String[] pipedCommands = extract(cmdline);

            if ( pipedCommands == null) {
                commandQ.offer(new CallCommand(cmdline));
            }else{
                //Arrays.stream(pipedCommands).forEach(System.out::println);
                Arrays.stream(pipedCommands).forEach(command -> commandQ.offer(new CallCommand(command)));
            }

        } catch (ShellException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout) throws AbstractApplicationException, ShellException {


        while (!commandQ.isEmpty()) {
            CallCommand command = commandQ.poll();
            command.parse();
            command.evaluate(stdin, stdout);

            resultStream = stdout; // cache the current result of pipeline
            stdin = ShellImpl.outputStreamToInputStream(stdout); // transfer current result to next pipe

            ShellImpl.closeOutputStream(stdout); // close the current pipe
            stdout = new ByteArrayOutputStream(); // create new output stream (so the previous result doesn't remain inside)
        }

    }

    public OutputStream getResultStream(){
        return resultStream;
    }

    @Override
    public void terminate() {

    }


    private String[] extract(String str)
            throws ShellException {

        str = str.trim();

        // if there's no pipe, no need to evaluate
        if (!str.contains("|")) return null;

        // if the command is of form <command> '<single_quote_content>', don't evaluate
        Pattern singleQuote = Pattern.compile("(?:.+)\\s+'(?:.*)'");
        if (singleQuote.matcher(str).matches()) return null;

        // if the command is of form <command> "<double_quote_content>", don't evaluate
        Pattern doubleQuote = Pattern.compile("(?:.+)\\s+\"(?:.*)\"");
        if (doubleQuote.matcher(str).matches()) return null;

        List<String> commands = new ArrayList<>();
        Queue<Character> queue = new LinkedList<>();

        boolean isClosingBacktick = false; // unlike parenthesis, we need a boolean to keep track if it is a opening or closing backtick
        int count = 0;

        while(!str.isEmpty()){
            Character c = str.charAt(0); // processing char from front of string
            str = str.substring(1); // reduce the string by one

            if(c != '|' && c != '`'){

                queue.offer(c);
                count++;
                continue;
            }

            if(c == '`') {

                queue.offer(c);
                count++;

                // toggle backtick type
                isClosingBacktick = !isClosingBacktick;
            }


            if (isClosingBacktick){
                if(c == '|') {
                    queue.offer(c);
                    count++;
                }
            }else{ // not within backtick
                if(c== '|'){
                    commands.add(pollMutiple(queue, count));
                    count = 0;
                }
            }

        }

        if(!queue.isEmpty()) commands.add(pollAll(queue));

        return commands.toArray(new String[]{});
    }

    private String pollAll(Queue<Character> queue){
        StringBuilder result = new StringBuilder();

        while(!queue.isEmpty()) result.append(queue.poll());

        return result.toString();
    }

    private String pollMutiple(Queue<Character> queue, int count){
        StringBuilder result = new StringBuilder();

        while(!queue.isEmpty() && count > 0) {
            if (queue.isEmpty()) break;

            result.append(queue.poll());

            count--;
        }

        return result.toString();
    }

}
