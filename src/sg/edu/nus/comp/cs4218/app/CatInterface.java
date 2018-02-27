package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.nio.file.Path;

public interface CatInterface extends Application {

    /**
     * Returns the content of the given file
     */
    public byte[] getContent(Path file) throws CatException;
}
