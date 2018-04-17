package sg.edu.nus.comp.cs4218.test.integration.params;

import java.util.List;

/**
 * Abstracts the test parameters used for testing two piped commands.
 */
@SuppressWarnings("PMD.DataClass")
public class PipeTwoTestParam {
    // Names of applications
    private final String app1;
    private final String app2;

    // Arguments of applications
    private List<String> args1;
    private List<String> args2;

    // Expected output string to be seen in STDOUT
    private String expected;

    public PipeTwoTestParam(String app1, String app2) {
        this.app1 = app1;
        this.app2 = app2;
    }

    public void setArgs1(List<String> args1) {
        this.args1 = args1;
    }

    public void setArgs2(List<String> args2) {
        this.args2 = args2;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getApp1() {
        return app1;
    }

    public String getApp2() {
        return app2;
    }

    public List<String> getArgs1() {
        return args1;
    }

    public List<String> getArgs2() {
        return args2;
    }

    public String getExpected() {
        return expected;
    }
}
