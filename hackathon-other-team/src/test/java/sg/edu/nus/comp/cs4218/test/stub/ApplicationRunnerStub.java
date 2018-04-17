package sg.edu.nus.comp.cs4218.test.stub;

import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;

import java.io.*;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings("PMD.DataClass")
public class ApplicationRunnerStub extends ApplicationRunner {
    public static final String STUBBED_OUTPUT = "stubbed output";

    private String app;
    private String[] argsArray;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String inputStreamString;

    @Override
    public void runApp(String app, String[] argsArray, InputStream inputStream,
                       OutputStream outputStream) {
        this.app = app;
        this.argsArray = argsArray;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.inputStreamString = getStringFromInputStream(inputStream);

        if (outputStream != System.out) {
            try {
                outputStream.write(STUBBED_OUTPUT.getBytes());
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    public String getApp() {
        return app;
    }

    public String[] getArgsArray() {
        return argsArray;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getInputStreamString() {
        return inputStreamString;
    }

    public static String getStringFromInputStream(InputStream inputStream) {
        if (inputStream == System.in) {
            return "";
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.lines().collect(Collectors.joining(STRING_NEWLINE));
    }
}
