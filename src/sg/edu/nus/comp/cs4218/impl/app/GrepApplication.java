package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GrepApplication implements GrepInterface {

    @Override
    public String grepFromStdin(String pattern, Boolean isInvert, InputStream stdin) throws Exception {
        Pattern grepPattern = Pattern.compile(pattern);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin));

        StringBuilder result = new StringBuilder();
        String line;
        boolean hasLine;
        do {
            line = bufferedReader.readLine();
            hasLine = line != null;

            if(hasLine) {

                boolean hasFoundPattern = grepPattern.matcher(line).find();

                /*
                We construct the truth table:

                hasFoundPattern | isInvert | return result?
                -------------------------------------------
                false           | false    | false
                false           | true     | true
                true            | false    | true
                true            | true     | false

                We can see this is this is a XOR truth table,
                therefore we do: hasFoundPattern XOR isInvert
                 */

                if (hasFoundPattern ^ isInvert) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
            }

        }while (hasLine);

        return result.toString().trim();
    }

    @Override
    public String grepFromMultipleFiles(String pattern, final Boolean isInvert, String... fileNames) throws Exception {
        final Pattern grepPattern = Pattern.compile(pattern);

        // transform each filenames into Stream of Path
        final Stream<Path> paths = Arrays.stream(fileNames).map(fileName -> Paths.get(fileName));

        final StringBuilder result = new StringBuilder();

        // by default Stream runs in parallel. We want serial in this case.
        paths.forEachOrdered(path -> {
            try {
                Files.lines(path).forEachOrdered( line -> {
                    boolean hasFoundPattern = grepPattern.matcher(line).find();

                    if (hasFoundPattern ^ isInvert) {
                        result.append(line);
                        result.append(System.lineSeparator());
                    }
                });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        return result.toString().trim();
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

        boolean isInvert = Arrays.stream(args).anyMatch(arg -> arg.equals("-v"));
        int invertOptionIndex = Arrays.binarySearch(args, "-v");

        // grep [-v] PATTERN [FILE]...
        // if -v then PATTERN is one index more than the index of -v,
        // otherwise the PATTERN index is the 0 (first)
        String patternString = isInvert? args[invertOptionIndex + 1] : args[0];
        String[] fileNames = isInvert? Arrays.copyOfRange(args, invertOptionIndex + 2, args.length): Arrays.copyOfRange(args, 1, args.length);

        try {
            if(fileNames.length == 0) {
                stdout.write(grepFromStdin(patternString, isInvert, stdin).getBytes());
            }else{
                stdout.write(grepFromMultipleFiles(patternString, isInvert, fileNames).getBytes());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
