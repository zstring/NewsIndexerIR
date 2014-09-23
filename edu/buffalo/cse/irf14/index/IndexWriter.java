/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	private static int docId = 0;
	private static int termId = 0;
	private Map<String, Integer> termDict;
	private Map<String, Term> termMap;
	private Map<Integer, String> docDict;
	private Map<Integer, Map<Integer,Posting>> termIndex;

	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		termDict = new HashMap<String, Integer>();
		docDict = new HashMap<Integer, String>();
		termIndex = new HashMap<Integer, Map<Integer,Posting>>();
		termMap = new HashMap<String, Term>();
	}

	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS
		try {
			if (d != null) {
				//increment document id
				docId += 1;
				docDict.put(docId, d.getField(FieldNames.FILEID)[0]);
				String[] str = d.getField(FieldNames.CONTENT);
				if (str.length > 0) {
					Tokenizer tkr = new Tokenizer();
					TokenStream tstream = tkr.consume(str[0]);
					while (tstream.hasNext()) {
						Token tk = tstream.next();
						if (tk != null && tk.toString() != null &&
								!"".equals(tk.toString())) {
							String tkString = tk.toString();
							// If new Term
							Integer tkInt = termDict.get(tkString);
							if (!termIndex.containsKey(tkInt)) {
								//increment term id
								termId += 1;
								Term term = new Term();
								term.setTermText(tkString);
								term.setTermId(termId);
								term.incColFreq();
								term.incdocFreq();
								this.termDict.put(tkString, termId);
								this.termMap.put(tkString, term);
								Posting posting = new Posting();
								posting.setDocId(docId);
								posting.incTermFreq();
								Map<Integer, Posting> postingsList = new HashMap<Integer, Posting>();
								postingsList.put(docId, posting);
								termIndex.put(termId,postingsList);
							}
							// If term already exist in index
							else {
								Term term = termMap.get(tkString);
								term.incColFreq();
								Map<Integer, Posting> postingsList = termIndex.get(tkInt);
								Posting posting = postingsList.get(docId);
								// If docId already exist in postingList
								if ( posting != null) {
									posting.incTermFreq();
								}
								// If docId does not exist in postingList
								else {
									Posting newPosting = new Posting();
									newPosting.setDocId(docId);
									newPosting.incTermFreq();
									postingsList.put(docId, posting);
								}
							}
						}
					}
				}
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		docId += 1;

	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
	}
}
