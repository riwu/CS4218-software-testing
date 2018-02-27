package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

public class GrepApplicationTest {

    private static final String TEST_TEXT = "The departure point of the paper is the skip-gram model";
	private static final String SENTENCES1_PATH = "testresource/sentences1.txt";
    private static final String SENTENCES2_PATH = "testresource/sentences2.txt";

    private InputStream inputStream;
    private OutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        this.inputStream = new ByteArrayInputStream("".getBytes());
        this.outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        this.inputStream.close();
        this.outputStream.close();
    }


    @Test
    public void shouldPrintMatchedSentencesWhenPatternMatches() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("grep departure " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = TEST_TEXT + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintNewlineWhenNoMatches() throws Exception {
        PipeCommand pipeCommand = new PipeCommand("grep nomatches " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected =  System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintInverselyMatchedSentencesWhenPatternInverselyMatches() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("grep -v the " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "Recently introduced continuous Skip-gram model is an efficient method for learning high-quality distributed vector representations that capture a large number of precise syntactic and semantic word relationships" + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingRegex() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("grep \\w+-\\w+-\\w+-\\w+ " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        // matches: state-of-the-art
        String expected = "The word2vec software of Tomas Mikolov and colleagues has gained a lot of traction lately and provides state-of-the-art word embeddings"  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingInputFromPipe() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("cat " + SENTENCES1_PATH + " | grep departure");
        System.out.println("cat " + SENTENCES1_PATH + " | grep departure");
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        // matches: state-of-the-art
        String expected = TEST_TEXT  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingMultipleFiles() throws Exception{
        PipeCommand pipeCommand = new PipeCommand( "grep context "+SENTENCES1_PATH + " "+ SENTENCES2_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = "In this model we are given a corpus of words w and their contexts c"  + System.lineSeparator() +
                          "We introduce a new dataset with human judgments on pairs of words in sentential context and evaluate our model on it showing that our model outperforms competitive baselines and other neural language models" + System.lineSeparator();

        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingSingleQuotes() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("grep 'departure point' " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = TEST_TEXT  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingDoubleQuotes() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("grep \"departure point\" " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = TEST_TEXT  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingBacktick() throws Exception{
        PipeCommand pipeCommand = new PipeCommand("grep `echo departure point` " + SENTENCES1_PATH);
        pipeCommand.parse();
        pipeCommand.evaluate(inputStream, outputStream);

        String expected = TEST_TEXT  + System.lineSeparator();
        String evaluated = pipeCommand.getResultStream().toString();

        assertEquals(expected, evaluated);
    }
}
