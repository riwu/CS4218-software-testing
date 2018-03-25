package sg.edu.nus.comp.cs4218.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class GrepGrepIntegrationTest {
	private final ShellImpl shell = new ShellImpl();
    private static final String TEST_TEXT = "The departure point of the paper is the skip-gram model";
	private static final String SENTENCES1_PATH = "testresource/sentences1.txt";
    private static final String SENTENCES2_PATH = "testresource/sentences2.txt";

    @Test
    public void shouldPrintMatchedSentencesWhenRepeatedPatternMatches() throws Exception{
    	String cmdline = "grep departure " + SENTENCES1_PATH + "|" + "grep departure ";
        String expected = TEST_TEXT + System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintNewLineWhenNoMatches() throws Exception{
    	String cmdline = "grep departure " + SENTENCES1_PATH + "|" + "grep aaaaa ";
    	String expected = System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintInverselyMatchedSentencesWhenPatternInverselyMatches() throws Exception{
        String cmdline = "grep model " + SENTENCES1_PATH + "|" + "grep -v the ";
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        String expected = "Recently introduced continuous Skip-gram model is an efficient method for learning high-quality distributed vector representations that capture a large number of precise syntactic and semantic word relationships" + System.lineSeparator();
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintNewLineWhenGrepEmptyString() throws Exception{
        String cmdline = "grep aaaaa " + SENTENCES1_PATH + "|" + "grep nothing";
        String expected = System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingRegex() throws Exception{
    	String cmdline = "grep \\w+-\\w+-\\w+-\\w+ " + SENTENCES1_PATH + "|" + "grep the ";
    	// matches: state-of-the-art
        String expected = "The word2vec software of Tomas Mikolov and colleagues has gained a lot of traction lately and provides state-of-the-art word embeddings"  + System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintMatchedSentencesWhenEvaluatingMultipleFiles() throws Exception{
    	String cmdline = "grep context " + SENTENCES1_PATH + " " + SENTENCES2_PATH + "|" + "grep words ";
        String expected = "In this model we are given a corpus of words w and their contexts c"  + System.lineSeparator() +
                          "We introduce a new dataset with human judgments on pairs of words in sentential context and evaluate our model on it showing that our model outperforms competitive baselines and other neural language models" + System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintRemainingMatchedSentencesWhenEvaluatingMultipleFiles() throws Exception{
    	String cmdline = "grep context " + SENTENCES1_PATH + " " + SENTENCES2_PATH + "|" + "grep contexts ";
        String expected = "In this model we are given a corpus of words w and their contexts c"  + System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }
    
    @Test
    public void shouldPrintInverselyMatchedSentencesWhenEvaluatingMultipleFiles() throws Exception{
    	String cmdline = "grep context " + SENTENCES1_PATH + " " + SENTENCES2_PATH + "|" + "grep -v contexts ";
        String expected = "We introduce a new dataset with human judgments on pairs of words in sentential context and evaluate our model on it showing that our model outperforms competitive baselines and other neural language models" + System.lineSeparator();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
    }

}
