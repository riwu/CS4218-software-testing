package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.Test;
import sg.edu.nus.comp.cs4218.impl.CommandTestUtil;

import static org.junit.Assert.assertEquals;

public class GrepApplicationTest {

    private static final String TEST_TEXT = "The departure point of the paper is the skip-gram model";
    private static final String SENTENCES1_PATH = "testresource/sentences1.txt";
    private static final String SENTENCES2_PATH = "testresource/sentences2.txt";

    @Test
    public void shouldPrintMatchedSentencesWhenPatternMatches() throws Exception {
        String expected = TEST_TEXT + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep departure " + SENTENCES1_PATH));
    }

    @Test
    public void shouldPrintNewlineWhenNoMatches() throws Exception {
        String expected = System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep nomatches " + SENTENCES1_PATH));
    }

    @Test
    public void shouldPrintInverselyMatchedSentencesWhenPatternInverselyMatches() throws Exception {
        String expected = "Recently introduced continuous Skip-gram model is an efficient method for learning " +
                "high-quality distributed vector representations that capture a large number of precise syntactic and " +
                "semantic word relationships" + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep -v the " + SENTENCES1_PATH));
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingRegex() throws Exception {
        // matches: state-of-the-art
        String expected = "The word2vec software of Tomas Mikolov and colleagues has gained a lot of traction lately " +
                "and provides state-of-the-art word embeddings" + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep \\w+-\\w+-\\w+-\\w+ " + SENTENCES1_PATH));
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingInputFromPipe() throws Exception {
        // matches: state-of-the-art
        String expected = TEST_TEXT + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("cat " + SENTENCES1_PATH + " | grep departure"));
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingMultipleFiles() throws Exception {
        String expected = "In this model we are given a corpus of words w and their contexts c" + System.lineSeparator() +
                "We introduce a new dataset with human judgments on pairs of words in sentential context and evaluate" +
                " our model on it showing that our model outperforms competitive baselines and other neural language" +
                " models" + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep context " + SENTENCES1_PATH + " " + SENTENCES2_PATH));
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingSingleQuotes() throws Exception {
        String expected = TEST_TEXT + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep 'departure point' " + SENTENCES1_PATH));
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingDoubleQuotes() throws Exception {
        String expected = TEST_TEXT + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep \"departure point\" " + SENTENCES1_PATH));
    }

    @Test
    public void shouldPrintMatchesSentencesWhenEvaluatingBacktick() throws Exception {
        String expected = TEST_TEXT + System.lineSeparator();
        assertEquals(expected, CommandTestUtil.getCommandOutput("grep `echo departure point` " + SENTENCES1_PATH));
    }
}
