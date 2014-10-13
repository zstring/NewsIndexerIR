package edu.buffalo.cse.irf14.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

import java.io.FileOutputStream;
import java.io.PrintStream;
public class SearchRunnerTest {

	private SearchRunner sr;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		String[] query = {"Author:(Peter AND Bale )"};
		try {
		String indexDir = "/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/news_training";
		String corpusDir = "";
		char mode = 'E';
		FileOutputStream file = new FileOutputStream("/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/output.txt");
		PrintStream stream = new PrintStream(file);
		sr = new SearchRunner(indexDir, corpusDir, mode, stream);
		for (int i = 0; i < query.length; i++) {
			sr.query(query[i], ScoringModel.OKAPI);
		}
		} catch (Exception ex) {
			fail("Error no file found");
		}
	}

}
