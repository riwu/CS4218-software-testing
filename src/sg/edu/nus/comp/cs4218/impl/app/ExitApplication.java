package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.ExitInterface;

import java.io.InputStream;
import java.io.OutputStream;

public class ExitApplication implements ExitInterface {

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) {
        terminateExecution();
    }

    @Override
    public void terminateExecution() {
        System.exit(0);
    }
}
