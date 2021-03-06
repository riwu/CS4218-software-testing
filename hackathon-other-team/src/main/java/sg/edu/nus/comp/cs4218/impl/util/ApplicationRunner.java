package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.*;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_INVALID_APP;

public class ApplicationRunner {
    public final static String APP_LS = "ls";
    public final static String APP_CAT = "cat";
    public final static String APP_ECHO = "echo";
    public final static String APP_EXIT = "exit";
    public final static String APP_MKDIR = "mkdir";
    public final static String APP_GREP = "grep";
    public final static String APP_PASTE = "paste";
    public final static String APP_DIFF = "diff";
    public final static String APP_CD = "cd";
    public final static String APP_SED = "sed";
    public final static String APP_SPLIT = "split";
    public final static String APP_CMP = "cmp";

    /**
     * Run the application as specified by the application command keyword and arguments.
     *
     * @param app          String containing the keyword that specifies what application to run.
     * @param argsArray    String array containing the arguments to pass to the applications for
     *                     running.
     * @param inputStream  InputStream for the application to get input from, if needed.
     * @param outputStream OutputStream for the application to write its output to.
     *
     * @throws AbstractApplicationException If an exception happens while running an application.
     * @throws ShellException               If an unsupported or invalid application command is
     *                                      detected.
     */
    public void runApp(String app, String[] argsArray, InputStream inputStream,
                       OutputStream outputStream)
            throws AbstractApplicationException, ShellException {
        Application application;

        switch (app) {
            case APP_LS:
                application = new LsApplication();
                break;
            case APP_CAT:
                application = new CatApplication();
                break;
            case APP_ECHO:
                application = new EchoApplication();
                break;
            case APP_EXIT:
                application = new ExitApplication();
                break;
            case APP_MKDIR:
                application = new MkdirApplication();
                break;
            case APP_GREP:
                application = new GrepApplication();
                break;
            case APP_PASTE:
                application = new PasteApplication();
                break;
            case APP_DIFF:
                application = new DiffApplication();
                break;
            case APP_CD:
                application = new CdApplication();
                break;
            case APP_SED:
                application = new SedApplication();
                break;
            case APP_SPLIT:
                application = new SplitApplication();
                break;
            case APP_CMP:
                application = new CmpApplication();
                break;
            default:
                throw new ShellException(app + ": " + ERR_INVALID_APP);
        }

        application.run(argsArray, inputStream, outputStream);
    }
}
