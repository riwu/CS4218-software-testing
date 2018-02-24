package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoApplicationTest {

    private EchoApplication echoApplication;

    @Before
    public void setUp() throws Exception {
        this.echoApplication = new EchoApplication();
    }

    @Test
    public void Should_PrintEmptyString_When_NoArgGiven() throws EchoException{
        String result = echoApplication.evaluate(new String[]{});

        assertTrue(result.equals(""));
    }

    @Test
    public void When_PrintString_Expect_NoTrailingNewlineCharacter() throws EchoException{
        String singleArg = echoApplication.evaluate(new String[]{"test1"});
        String multipleArgs = echoApplication.evaluate(new String[]{"test1", "test2", "test3"});

        assertTrue(singleArg.endsWith(""));
        assertTrue(multipleArgs.endsWith(""));
    }

    @Test
    public void Should_PrintString_When_EvaluatingOneArg() throws EchoException {
        String result = echoApplication.evaluate(new String[]{"test1"});

        assertTrue(result.equals("test1"));
    }

    @Test
    public void Should_PrintStringWithSpaceBetween_When_EvaluatingMultipleArgs() throws  EchoException {
        String result = echoApplication.evaluate(new String[]{"test1", "test2", "test3"});

        assertTrue(result.equals("test1 test2 test3"));
    }

    @Test
    public void Should_PrintUnicode_When_EvaluatingUnicodeCharacter() throws EchoException {
        String result = echoApplication.evaluate(new String[]{"☺"});

        assertTrue(result.equals("☺"));
    }

}