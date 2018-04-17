package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static sg.edu.nus.comp.cs4218.impl.ShellImpl.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_OUTPUT;

public class IORedirectionHandler {
    private final List<String> argsList;
    private List<String> noRedirArgsList;
    private InputStream inputStream;
    private OutputStream outputStream;

    // TODO ArgumentResolver should be a dependency so that we can unit test IORedirectionHandler
    public IORedirectionHandler(List<String> argsList, InputStream origInputStream,
                                OutputStream origOutputStream) {
        this.argsList = argsList;
        this.inputStream = origInputStream;
        this.outputStream = origOutputStream;
    }

    public void extractRedirOptions() throws ShellException {
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        noRedirArgsList = new LinkedList<>();

        // extract redirection operators (with their corresponding files) from argsList
        ListIterator<String> argsIterator = argsList.listIterator();
        while (argsIterator.hasNext()) {
            String arg = argsIterator.next();

            // leave the other args untouched
            if (!isRedirOperator(arg)) {
                noRedirArgsList.add(arg);
                continue;
            }

            // if current arg is < or >, fast-forward to the next arg to extract the specified file
            String file;
            try {
                file = argsIterator.next();
            } catch (NoSuchElementException e) {
                // redirection operator located at the end of argsList
                throw new ShellException(ERR_SYNTAX, e);
            }

            if (isRedirOperator(file)) {
                // consecutive redirection operators specified
                throw new ShellException(ERR_SYNTAX);
            }

            // handle quoting + globing + command substitution in file arg
            List<String> fileSegment = ArgumentResolver.resolveOneArgument(file);
            if (fileSegment.size() > 1) {
                // ambiguous redirect if file resolves to more than one parsed arg
                throw new ShellException(ERR_SYNTAX);
            }
            file = fileSegment.get(0);

            // replace existing inputStream / outputStream
            if (arg.equals(String.valueOf(CHAR_REDIR_INPUT))) {
                IOUtils.closeInputStream(inputStream);
                inputStream = IOUtils.openInputStream(file);
            } else if (arg.equals(String.valueOf(CHAR_REDIR_OUTPUT))) {
                IOUtils.closeOutputStream(outputStream);
                outputStream = IOUtils.openOutputStream(file);
            }
        }
    }

    public List<String> getNoRedirArgsList() {
        return noRedirArgsList;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    private boolean isRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_INPUT)) ||
               str.equals(String.valueOf(CHAR_REDIR_OUTPUT));
    }
}
