package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import edu.buffalo.cse.irf14.Scorer.ScorerClass;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
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
	private int noOfResult;
	private Map<String, Double> finalResult;
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
		noOfResult = 0;
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
			long startTime = System.currentTimeMillis();
			Query query = QueryParser.parse(userQuery, defaultOperator);
			Map<String, Posting> unRankedResult = query.execute(reader);
			Map<Integer, Double> queryVector = query.getVector(reader);
			Map<Integer, Double> queryTermFreq = query.getQueryTermFreq(reader);
			Map<String, Double> rankedResult =  RankedResultWithModel(unRankedResult, queryVector, queryTermFreq, this.avgLen, model);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println("Query: " + userQuery);
			System.out.println("Query time: " + elapsedTime);
			int rank = 0;
			for (String docId : rankedResult.keySet()) {
				rank++;
				if (rank > 10) break;
				System.out.println("");
				System.out.println("Result Rank: " + rank);
				Document d = Parser.parse(corpusDir + File.separator + docId);
				System.out.println("Result Title: ");
				if (d.getField(FieldNames.TITLE) != null) {
					System.out.println(d.getField(FieldNames.TITLE)[0].trim());
				}
				else {
					System.out.println("No Title!");
				}
				Posting posting = unRankedResult.get(docId);
				List<Integer> positions = posting.getPosIndex();
				TokenStream stream = null, streamTitle = null;
				HashMap<FieldNames, TokenStream> streamMap = getStreamMap(docId, d, unRankedResult);
				if (!streamMap.isEmpty()) {
					stream = streamMap.get(posting.getIndexType().toFieldNames());
					if(IndexType.TERM.equals(posting.getIndexType())) {
						streamTitle = streamMap.get(FieldNames.TITLE);
					}
				}
				if (stream != null) {
					for (int i = 0; i < positions.size(); i++) {
						Token[] tokenList = stream.getPrevTokens(7);
						if (tokenList != null) {
							StringBuilder sb = new StringBuilder("....");
							for (int j = 0; j < tokenList.length; j++ ) {
								if (tokenList[j] != null)
								sb.append(tokenList[j].getTermText());
							}
							sb.append("....");
							System.out.println(sb.toString());
						}
						if(i > 2) {
							break;
						}
					}
				}
				if (streamTitle != null) {
					for (int i = 0; i < positions.size(); i++) {
						Token[] tokenList = streamTitle.getPrevTokens(7);
						if (tokenList != null) {
							StringBuilder sb = new StringBuilder(".... ");
							for (int j = 0; j < tokenList.length; j++ ) {
								if (tokenList[j] != null)
								sb.append(tokenList[j].getTermText() + " ");
							}
							sb.append(" ....");
							System.out.println(sb.toString());
						}
						if(i > 2) {
							break;
						}
					}
				}
				System.out.println("Result Relevancy: " + rankedResult.get(docId));
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		//TODO: IMPLEMENT THIS METHOD
	}

	public HashMap<FieldNames, TokenStream> getStreamMap(String docId, Document d,
			Map<String, Posting> unRankedResult) throws TokenizerException {
		Tokenizer tkr = new Tokenizer();
		HashMap<FieldNames, TokenStream> streamMap = new HashMap<FieldNames, TokenStream>();
		if (d.getField(FieldNames.AUTHOR) != null) 
			streamMap.put(FieldNames.AUTHOR, tkr.consume(d.getField(FieldNames.AUTHOR)[0].trim()));
		if (d.getField(FieldNames.AUTHORORG) != null)
			streamMap.put(FieldNames.AUTHORORG, tkr.consume(d.getField(FieldNames.AUTHORORG)[0].trim()));
		if (d.getField(FieldNames.TITLE) != null)
			streamMap.put(FieldNames.TITLE, tkr.consume(d.getField(FieldNames.TITLE)[0].trim()));
		if (d.getField(FieldNames.CONTENT)  != null)
			streamMap.put(FieldNames.CONTENT, tkr.consume(d.getField(FieldNames.CONTENT)[0].trim()));
		return streamMap;
	}

	/**
	 * 
	 * @param unRankedResult
	 * @param model
	 */
	private Map<String, Double> RankedResultWithModel(Map<String, Posting> unRankedResult,
			Map<Integer, Double> queryVector, Map<Integer, Double> queryTermFreq, double avgLen, ScoringModel model) {
		// TODO Auto-generated method stub
		ScorerClass sc = new ScorerClass();
		TreeMap<String, Double> rankedResult = null;
		if (unRankedResult != null && unRankedResult.keySet().size() > 1) {
			rankedResult = sc.rankResult(unRankedResult, docVector, queryVector, queryTermFreq, avgLen, model);
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
		try {
			if (queryFile == null)
				return;
			Scanner sc = new Scanner(queryFile);
			String regex = "(.*)(?:\\: *\\{ *)(.*)(?: *\\} *)";
			Matcher mat = Pattern.compile(regex).matcher("");
			String firstLine = "";
			if (sc.hasNext()) {
				firstLine = sc.nextLine();
			}
			String[] noOfQ = firstLine.split(".*=");
			int numQuery = 0;
			for (int i = 0; i < noOfQ.length; i++) {
				if (!noOfQ[i].isEmpty()) {
					numQuery = Integer.parseInt(noOfQ[i]);
					break;
				}
			}
			String[] queryId = new String[numQuery];
			String[] queries = new String[numQuery];
			int index = 0;
			for (int i = 0; i < numQuery; i++) {
				String q = sc.nextLine();
				mat.reset(q);
				if (mat.matches()) {
					queryId[index] = mat.group(1);
					queries[index++] = mat.group(2);
				}
			}
			String[] queryResults = new String[index];
			for (int i = 0; i < index; i++) {
				queryForE(queries[i], ScoringModel.TFIDF);
				setResultsInQuerySet(queryResults, finalResult, queryId, i);
			}
			stream.append("numResults=" + noOfResult);
			stream.append("\n");
			for (int i = 0; i < index; i++) {
				if (queryResults[i] != null &&
						!queryResults[i].isEmpty()) {
					stream.append(queryResults[i]);
					stream.append("\n");
				}
			}
			noOfResult = 0;

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}
		public void queryForE(String userQuery, ScoringModel model) {
			String defaultOperator = "OR";
			try {
				Query query = QueryParser.parse(userQuery, defaultOperator);
				Map<String, Posting> unRankedResult = query.execute(reader);
				Map<Integer, Double> queryVector = query.getVector(reader);
				Map<Integer, Double> queryTermFreq = query.getQueryTermFreq(reader);
				Map<String, Double> rankedResult =  RankedResultWithModel(unRankedResult, queryVector, queryTermFreq, this.avgLen, model);
				finalResult = rankedResult;
			} catch (Exception ex) {
				
			}
	}

	private void setResultsInQuerySet(String[] queryResults,
			Map<String, Double> fR, String[] queryId, int i) {
		String res = queryId[i] + ":{";
		if (fR != null) {
			StringBuilder sb = new StringBuilder(res);
			String hashString = "#";
			String appendRes = ", ";
			boolean flag = false;
			int counter = 0;
			for (String docId : fR.keySet()) {
				if (counter > 9) break;
				Double d = fR.get(docId);
				DecimalFormat df = new DecimalFormat("#.####");
				d = Double.parseDouble(df.format(d));
				sb.append(docId + hashString + d);
				sb.append(appendRes);
				flag = true;
				counter++;
			}
			if (flag) {
				sb.replace(sb.length() - 2, sb.length(), "}");
				queryResults[i] = sb.toString();
				noOfResult++;
			}
		}
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
