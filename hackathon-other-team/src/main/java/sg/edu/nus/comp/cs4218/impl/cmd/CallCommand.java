package sg.edu.nus.comp.cs4218.impl.cmd;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.IORedirectionHandler;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_SYNTAX;

/**
 * A Call Command is a sub-command consisting of at least one non-keyword or quoted.
 *
 * <p>
 * <b>Command format:</b> <code>(&lt;non-keyword&gt; or &lt;quoted&gt;) *</code>
 * </p>
 */
public class CallCommand implements Command {
    private final List<String> argsList;
    private final ApplicationRunner appRunner;

    // TODO ArgumentResolver should be a dependency so that we can unit test CallCommand
    public CallCommand(List<String> argsList, ApplicationRunner appRunner) {
        this.argsList = argsList;
        this.appRunner = appRunner;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException {
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        // Handle IO redirection
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, stdin, stdout);
        redirHandler.extractRedirOptions();
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        InputStream inputStream = redirHandler.getInputStream();
        OutputStream outputStream = redirHandler.getOutputStream();

        // Handle quoting + globing + command substitution
        List<String> parsedArgsList = ArgumentResolver.parseArguments(noRedirArgsList);
        String app;
        if (parsedArgsList.isEmpty()) {
            app = "";
        } else {
            app = parsedArgsList.remove(0);
        }

        appRunner.runApp(app, parsedArgsList.toArray(new String[0]), inputStream, outputStream);
        IOUtils.closeInputStream(inputStream);
        IOUtils.closeOutputStream(outputStream);
    }

    @Override
    public void terminate() {
        // Unused for now
    }

    public List<String> getArgsList() {
        return argsList;
    }
}
