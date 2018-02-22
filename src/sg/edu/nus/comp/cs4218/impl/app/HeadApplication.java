package sg.edu.nus.comp.cs4218.impl.app;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class HeadApplication implements Application {

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

        String in = convertStreamToString(stdin) + args[0];

        try {
            stdout.write(in.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
