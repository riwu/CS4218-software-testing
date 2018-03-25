package sg.edu.nus.comp.cs4218.integration;

import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class EchoSedIntegrationTest {
    private static ShellImpl shell;
    private OutputStream stdout;

    @Before
    public void setUp() throws Exception {
        shell = new ShellImpl();
        stdout = new ByteArrayOutputStream();
    }

    @Test
    public void whenReplaceEntireWordExpectChange() throws Exception {
        String expected = "world";
        String argument = "echo hello | sed s/hello/world/";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }

    @Test
    public void whenReplaceSecondWordExpectOnlySecondChange() throws Exception {
        String expected = "helco";
        String argument = "echo hello | sed s/l/c/2";
        shell.parseAndEvaluate(argument, stdout);

        assertEquals(expected, stdout.toString());
    }
}
