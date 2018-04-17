package sg.edu.nus.comp.cs4218.test.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.impl.app.ExitApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.app.EchoApplication.ERR_NO_STREAM;

public class ExitApplicationTest {


    private ExitApplication exitApp;

    @BeforeEach
    public void setUp(){
        exitApp = new ExitApplication();
    }

    /** Tests for terminateExecution() **/

    @Test
    public void testTerminateExecution() {
        Throwable exception = assertThrows(ExitException.class, () -> {
            exitApp.terminateExecution();
        });
        assertEquals("exit: 0", exception.getMessage());
    }

    /** Tests for run() **/

    @Test
    public void testRunNullParams() {
        Throwable exception = assertThrows(ExitException.class, () -> {
            exitApp.run(null, null, null);
        });
        assertEquals("exit: 0", exception.getMessage());
    }

    @Test
    public void testRunNonNullParams() {
        Throwable exception = assertThrows(ExitException.class, () -> {
            exitApp.run(new String[0], System.in, System.out);
        });
        assertEquals("exit: 0", exception.getMessage());
    }
}
