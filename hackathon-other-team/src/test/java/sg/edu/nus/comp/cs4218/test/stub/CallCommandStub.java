package sg.edu.nus.comp.cs4218.test.stub;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CallCommandStub extends CallCommand {
    public enum InducedException {
        FAIL,
        EXIT;
    }

    public static int numTerminated = 0;

    private String injectedData;
    private InducedException inducedException;

    public CallCommandStub(List<String> argsList, ApplicationRunner appRunner) {
        super(argsList, appRunner);
    }

    public CallCommandStub(List<String> argsList, ApplicationRunner appRunner,
                           String injectedData) {
        super(argsList, appRunner);
        this.injectedData = injectedData;
    }

    public CallCommandStub(List<String> argsList, ApplicationRunner appRunner,
                           InducedException inducedException) {
        super(argsList, appRunner);
        this.inducedException = inducedException;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException {
        if (inducedException == InducedException.EXIT) {
            throw new ExitException("Induced exit");
        }
        if (inducedException == InducedException.FAIL) {
            throw new ShellException("Induced failure");
        }

        try {
            if (injectedData == null) {
                int data;
                while ((data = stdin.read()) != -1) {
                    stdout.write(data);
                }
            } else {
                stdout.write(injectedData.getBytes());
            }

            IOUtils.closeInputStream(stdin);
            IOUtils.closeOutputStream(stdout);
        } catch (IOException e) {
            throw new ShellException(e.getMessage(), e);
        }
    }

    @Override
    public void terminate() {
        numTerminated++;
    }
}
