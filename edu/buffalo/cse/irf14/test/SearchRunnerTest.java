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
		String[] query = {"(black OR blue) OR bruises"};
		query[0] = "Adobe";
//		query[0] = "tapioca";
		try {
			String indexDir = "/home/avinav/Dropbox/workspace/avinav_newsindexer/data";
			indexDir = "/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/news_training";
			String fileOutput = "/home/avinav/Dropbox/workspace/avinav_newsindexer/data/output.txt";
			fileOutput = "/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/news_training/output.txt";
			String corpusDir = "";
			char mode = 'E';
			FileOutputStream file = new FileOutputStream(fileOutput);
			PrintStream stream = new PrintStream(file);
			sr = new SearchRunner(indexDir, corpusDir, mode, stream);
			for (int i = 0; i < query.length; i++) {
				sr.query(query[i], ScoringModel.OKAPI);
			}
			stream.close();
		} catch (Exception ex) {
			fail("Error no file found");
		}
	}

}
