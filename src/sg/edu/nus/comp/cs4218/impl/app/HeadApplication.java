package sg.edu.nus.comp.cs4218.impl.app;
import java.io.*;
import java.nio.Buffer;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class HeadApplication implements Application {

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin));

            String line = bufferedReader.readLine();
            boolean hasLine = line != null;
            boolean hasArgs = args.length != 0;

            if(hasArgs){ // if it has argument, ignore the piped input then evaluate the args only.
                stdout.write(args[0].getBytes(), 0, args[0].length());
            }else { // no args
                while (hasLine) { // then we check if it has any the piped inputs
                    stdout.write(line.getBytes());

                    line = bufferedReader.readLine();
                    hasLine = line != null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
