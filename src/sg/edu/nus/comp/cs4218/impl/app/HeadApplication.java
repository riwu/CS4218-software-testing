package sg.edu.nus.comp.cs4218.impl.app;
import java.io.*;
import java.nio.Buffer;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class HeadApplication implements Application {

    private int HEAD_READ_COUNT = 10;

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin));

            String line = bufferedReader.readLine();
            boolean hasLine = line != null;
            boolean hasArgs = args.length != 0;

            line += System.lineSeparator();
            if(hasArgs){ // if it has argument, ignore the piped input then evaluate the args only.
                stdout.write(args[0].getBytes());
            }else { // no args
                int lineRead = 1;

                while (hasLine && lineRead < HEAD_READ_COUNT) { // then we check if it has any the piped inputs
                    stdout.write(line.getBytes());

                    line = bufferedReader.readLine();
                    hasLine = line != null;
                    line += System.lineSeparator();

                    lineRead++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
