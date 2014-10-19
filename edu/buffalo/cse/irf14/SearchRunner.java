package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.GZIPInputStream;

import edu.buffalo.cse.irf14.Scorer.ScorerClass;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	public enum ScoringModel {TFIDF, OKAPI};
	private HashMap<IndexType, IndexReader> reader;
	HashMap<String, HashMap<Integer, Double>> docVector;
	private String indexDir;
	private String corpusDir;
	private char mode;
	private PrintStream stream;
	private double avgLen;
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		this.indexDir = indexDir;
		this.corpusDir = corpusDir;
		this.mode = mode;
		this.stream = stream;
		setUpIndexWriter();
		setUpIndexReader();
		//TODO: IMPLEMENT THIS METHOD
	}

	private void setUpIndexWriter() {
		// TODO Auto-generated method stub


	}

	private void setUpIndexReader() {
		// TODO Auto-generated method stub
		reader = new HashMap<IndexType, IndexReader>();
		reader.put(IndexType.AUTHOR, new IndexReader(indexDir, IndexType.AUTHOR));
		reader.put(IndexType.CATEGORY, new IndexReader(indexDir, IndexType.CATEGORY));
		reader.put(IndexType.PLACE, new IndexReader(indexDir, IndexType.PLACE));
		reader.put(IndexType.TERM, new IndexReader(indexDir, IndexType.TERM));
		docVector = readDocVector();
	}

	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		String defaultOperator = "OR";
		try {
			Query query = QueryParser.parse(userQuery, defaultOperator);
			Map<String, Posting> unRankedResult = query.execute(reader);
			Map<Integer, Double> queryVector = query.getVector(reader);
			Map<String, Double> rankedResult =  RankedResultWithModel(unRankedResult, queryVector, this.avgLen, model);
			
			for (String docId : rankedResult.keySet()) {
				stream.print("Doc: " + docId + " Score: " + rankedResult.get(docId));
				stream.println();
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		//TODO: IMPLEMENT THIS METHOD
	}

	/**
	 * 
	 * @param unRankedResult
	 * @param model
	 */
	private Map<String, Double> RankedResultWithModel(Map<String, Posting> unRankedResult,
			Map<Integer, Double> queryVector, double avgLen, ScoringModel model) {
		// TODO Auto-generated method stub
		ScorerClass sc = new ScorerClass();
		TreeMap<String, Double> rankedResult = null;
		if (unRankedResult != null && unRankedResult.keySet().size() > 1) {
			rankedResult = sc.rankResult(unRankedResult, docVector, queryVector, avgLen, model);
		}
		for (String docId : rankedResult.keySet()) {
			System.out.println("Doc: " + docId + " Score: " + rankedResult.get(docId));
		}
		return rankedResult;	
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, HashMap<Integer, Double>> readDocVector() {
		// TODO Auto-generated method stub
		HashMap<String, HashMap<Integer, Double>> docVector = null;
		try {
			FileInputStream fo = new FileInputStream(indexDir + java.io.File.separator  + "VECTOR");
			GZIPInputStream gzip = new GZIPInputStream(fo);
			ObjectInputStream ois = new ObjectInputStream(gzip);
			docVector = (HashMap<String, HashMap<Integer, Double>>)ois.readObject();
			this.avgLen = (Double) ois.readObject();
			ois.close();
			gzip.close();
			fo.close();
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		} catch (ClassNotFoundException ex) {
		}
		return docVector;
	}

	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
	}

	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;

	}

	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
