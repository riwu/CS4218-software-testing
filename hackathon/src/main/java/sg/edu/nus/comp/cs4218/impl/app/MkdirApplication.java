package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MkdirInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MkdirException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class MkdirApplication implements MkdirInterface {
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            createFolder(args);
        } catch (Exception e) {
            throw new MkdirException(e.getMessage(), e);
        }
    }

    @Override
    public void createFolder(String... folderName) throws Exception {
        List<String> failedPaths = new LinkedList<>();

        for (String pathRequested: folderName) {
            Path path = Paths.get(pathRequested);
            boolean mkdirFailed = !path.toFile().mkdir();

            if (mkdirFailed) {
                failedPaths.add(pathRequested);
            }

        }

        if (!failedPaths.isEmpty()) {
            String[] failedPathsArr = failedPaths.toArray(new String[]{});
            String errMessage = makeErrorMessage(failedPathsArr);

            throw new MkdirException(errMessage);
        }
    }

    private String makeErrorMessage(String... paths) {
        StringBuilder builder = new StringBuilder();

        for (String path: paths) {
            builder.append(path);
            builder.append(" failed");
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
