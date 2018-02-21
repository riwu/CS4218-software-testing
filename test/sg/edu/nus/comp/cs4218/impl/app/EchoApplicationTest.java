package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.Test;
import static org.junit.Assert.*;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoApplicationTest {

    EchoApplication echoApplication = new EchoApplication();

    @Test
    public void Should_PrintSingleEmptyLine_When_NoArgGiven() throws EchoException{
        String result = echoApplication.evaluate(new String[]{});

        assertTrue("Result should contain a single new line character.", result.equals("\n"));
    }

    @Test
    public void When_PrintString_Expect_TrailingNewlineCharacter() throws EchoException{
        String singleArg = echoApplication.evaluate(new String[]{"test1"});
        String multipleArgs = echoApplication.evaluate(new String[]{"test1", "test2", "test3"});

        assertTrue(singleArg.endsWith("\n"));
        assertTrue(multipleArgs.endsWith("\n"));
    }

    @Test
    public void Should_PrintString_When_EvaluatingOneArg() throws EchoException {
        String result = echoApplication.evaluate(new String[]{"test1"});

        assertTrue(result.equals("test1\n"));
    }

    @Test
    public void Should_PrintStringWithSpace_When_EvaluatingMultipleArgs() throws  EchoException {
        String result = echoApplication.evaluate(new String[]{"test1", "test2", "test3"});

        assertTrue(result.equals("test1 test2 test3\n"));
    }

    @Test
    public void Should_PrintString_When_EvaluatingUnicodeCharacter() throws EchoException {
        String result = echoApplication.evaluate(new String[]{"☺"});

        assertTrue(result.equals("☺\n"));
    }

}