package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoApplicationTest {

    private EchoApplication echoApplication;
    private static final String TEST_ONE = "test1";
    private static final String TEST_TWO = "test2";
    private static final String TEST_THREE = "test3";
    private static final String TEST_UNICODE = "☺";

    @Before
    public void setUp() throws Exception {
        this.echoApplication = new EchoApplication();
    }

    @Test
    public void shouldPrintNewlineWhenNoArgGiven() throws EchoException{
        String result = echoApplication.constructResult(new String[]{});

        assertTrue(result.equals(System.lineSeparator()));
    }

    @Test
    public void whenPrintStringExpectNewlineCharacter() throws EchoException{
        String singleArg = echoApplication.constructResult(new String[]{TEST_ONE});
        String multipleArgs = echoApplication.constructResult(new String[]{TEST_ONE, TEST_TWO, TEST_THREE});

        assertTrue(singleArg.endsWith(""));
        assertTrue(multipleArgs.endsWith(""));
    }

    @Test
    public void shouldPrintStringWhenEvaluatingOneArg() throws EchoException {
        String result = echoApplication.constructResult(new String[]{TEST_ONE});

        assertTrue(result.equals(TEST_ONE + System.lineSeparator()));
    }

    @Test
    public void shouldPrintStringWithSpaceBetweenWhenEvaluatingMultipleArgs() throws  EchoException {
        String result = echoApplication.constructResult(new String[]{"test1", "test2", "test3"});

        assertTrue(result.equals("test1 test2 test3" + System.lineSeparator()));
    }

    @Test
    public void shouldPrintUnicodeWhenEvaluatingUnicodeCharacter() throws EchoException {
        String result = echoApplication.constructResult(new String[]{TEST_UNICODE});

        assertTrue(result.equals(TEST_UNICODE + System.lineSeparator()));
    }
    
    @Test(expected=EchoException.class)
    public void shouldThrowExceptionWhenArgIsNull() throws EchoException{
        echoApplication.run(null, System.in, System.out);
    }
    
    @Test(expected=EchoException.class)
    public void shouldThrowExceptionWhenStdoutIsNull() throws EchoException{
        echoApplication.run(new String[]{TEST_ONE}, System.in, null);
    }

}