package sg.edu.nus.comp.cs4218.test.integration.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeTestParams {
    private final List<String> apps;
    private final List<List<String>> args = new ArrayList<>();
    private String expected;

    public PipeTestParams(String ...apps) {
        this.apps = Arrays.asList(apps);

        for (int i = 0; i < apps.length; i++) {
            args.add(new ArrayList<>());
        }
    }

    public List<String> getApps() {
        return apps;
    }

    public String getApp(int app) {
        return apps.get(app);
    }

    public List<String> getArgs(int app) {
        return args.get(app);
    }

    public void setArgs(int app, List<String> args) {
        this.args.set(app, args);
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }
}
