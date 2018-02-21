package sg.edu.nus.comp.cs4218.app;

import java.io.File;

import sg.edu.nus.comp.cs4218.Application;

public interface Cat extends Application {

    /**
     * Returns the content of the given file
     */
    public String getContent(File file)
            throws Exception;

}
