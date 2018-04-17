package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.SplitException;
import sg.edu.nus.comp.cs4218.impl.parser.SplitArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.SplitFileNameGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Paths;

public class SplitApplication implements SplitInterface {
    private static final String STD_IN = "-";
    private static final String NOT_IMPLEMENTED = "Not implemented";

    @Override
    public void splitFileByLines(String fileName, String prefix, int linesPerFile) throws Exception {
        File resolvedPath = Paths.get(Environment.currentDirectory).resolve(fileName).toFile();
        splitByLines(new FileReader(resolvedPath), prefix, linesPerFile);
    }

    @Override
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {
        File resolvedPath = Paths.get(Environment.currentDirectory).resolve(fileName).toFile();
        splitByByte(new FileInputStream(resolvedPath), prefix, SplitArgsParser.getByteSize(bytesPerFile));
    }

    @Override
    public void splitStdinByLines(InputStream stdin, String prefix, int linesPerFile) throws Exception {
        splitByLines(new InputStreamReader(stdin), prefix, linesPerFile);
    }

    @Override
    public void splitStdinByBytes(InputStream stdin, String prefix, String bytesPerFile) throws Exception {
        splitByByte(stdin, prefix, SplitArgsParser.getByteSize(bytesPerFile));
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        SplitArgsParser parser = new SplitArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new SplitException(e.getMessage(), e);
        }

        try {
            if (parser.getFileName().equals(STD_IN)) {
                if (parser.isLineCount()) {
                    splitStdinByLines(stdin, parser.getPrefix(), parser.getChunkSize());
                } else {
                    splitStdinByBytes(stdin, parser.getPrefix(), String.valueOf(parser.getChunkSize()));
                }
            } else {
                if (parser.isLineCount()) {
                    splitFileByLines(parser.getFileName(), parser.getPrefix(), parser.getChunkSize());
                } else {
                    splitFileByBytes(parser.getFileName(), parser.getPrefix(), String.valueOf(parser.getChunkSize()));
                }
            }
        } catch (Exception e) {
            throw new SplitException(e.getMessage(), e);
        }
    }

    /**
     * Writes content into file specified by {@code fileName}
     * @param path
     * @param content
     * @throws IOException
     */
    private void writeToFile(String path, String content) throws IOException {
        File file = Paths.get(Environment.currentDirectory).resolve(path).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file)); // overwrite existing file?
        writer.write(content);
        writer.newLine();
    }

    /**
     * Writes content into file specified by {@code fileName}
     * @param path
     * @param content
     * @throws IOException
     */
    private void writeToFile(String path, byte[] content) throws IOException {
        File file = Paths.get(Environment.currentDirectory).resolve(path).toFile();
        OutputStream outputStream = new FileOutputStream(file);

        outputStream.write(content);
    }

    /**
     * Provide an abstraction over the type of input for splitting its content
     * @param inputReader
     * @param prefix
     * @param linesPerFile
     * @throws IOException
     */
    private void splitByLines(Reader inputReader, String prefix, int linesPerFile) throws IOException {
        BufferedReader reader = new BufferedReader(inputReader);
        SplitFileNameGenerator nameGenerator = new SplitFileNameGenerator(prefix);

        String line;
        String targetFileName = nameGenerator.next();
        File file = Paths.get(Environment.currentDirectory).resolve(targetFileName).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file)); // overwrite existing file?
        int linesRead = 0;
        while ((line = reader.readLine()) != null) {
            if (linesRead == 0) {
                // open new files for writing
                file = Paths.get(Environment.currentDirectory).resolve(targetFileName).toFile();
                writer = new BufferedWriter(new FileWriter(file)); // overwrite existing file?
            }
            writer.write(line);
            writer.newLine();

            linesRead++;

            if (linesRead == linesPerFile) {
                targetFileName = nameGenerator.next();
                writer.close();

                linesRead = 0;
            }
        }
        writer.close();
    }

    private void splitByByte(InputStream inputStream, String prefix, int bytesPerFile) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(inputStream);
        SplitFileNameGenerator nameGenerator = new SplitFileNameGenerator(prefix);

        byte[] content = new byte[bytesPerFile];
        while(stream.read(content, 0, bytesPerFile) > 0) {
            writeToFile(nameGenerator.next(), content);
        }
    }
}
