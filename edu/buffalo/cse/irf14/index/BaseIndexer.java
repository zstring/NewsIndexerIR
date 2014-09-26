package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerTerm;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class BaseIndexer implements Serializable {

	private static final long serialVersionUID = 42L;
	private static int docId = 0;
	private static int termId = 0;
	private IndexType indexerType;
	public Map<String, Integer> termDict;
//	public Map<Integer, String> docDict;
	public Map<Integer, Term> termMap;	
//	public Map<Integer, Map<Integer, Posting>> termIndex;

	public BaseIndexer(IndexType indexerType) {
		this.indexerType = indexerType;
		termDict = new HashMap<String, Integer>();
//		docDict = new HashMap<Integer, String>();
//		termIndex = new HashMap<Integer, Map<Integer,Posting>>();
		termMap = new HashMap<Integer, Term>();
	}

	public Set<Integer> getTermKeys() {
		return termMap.keySet();
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
				String docTerm = "";
				if (d.getField(FieldNames.FILEID).length > 0) {
					docTerm = d.getField(FieldNames.FILEID)[0];
				}
//				docDict.put(docId, d.getField(FieldNames.FILEID)[0]);
				String strContent = "";
				if (d.getField(FieldNames.CONTENT).length > 0) {
					strContent = d.getField(FieldNames.CONTENT)[0];
				}
				if (!strContent.trim().isEmpty()) {
					Tokenizer tkr = new Tokenizer();
					TokenStream tStream = tkr.consume(strContent);
					AnalyzerFactory aFactory = AnalyzerFactory.getInstance();
					Analyzer aContent = aFactory.getAnalyzerForField(FieldNames.CONTENT, tStream);
					while (aContent.increment()) {
					}
					tStream.reset();
					while (tStream.hasNext()) {
						Token tk = tStream.next();
						if (tk != null) {
							String tkString = tk.toString();
							if (tkString != null && !tkString.isEmpty()) {
								// If new Term
								Integer tkInt = termDict.get(tkString);
								if (!termMap.containsKey(tkInt)) {
//								if (!termIndex.containsKey(tkInt)) {
									//increment term id
									termId += 1;
									Term term = new Term(tkString, termId, docTerm);
									this.termDict.put(tkString, termId);
									this.termMap.put(termId, term);
//									Posting posting = new Posting();
//									posting.setDocId(docId);
//									posting.incTermFreq();
//									Map<Integer, Posting> postingsList =
											new HashMap<Integer, Posting>();
//									postingsList.put(docId, posting);
//									termIndex.put(termId, postingsList);
								}
								// If term already exist in index
								else {
									Term term = termMap.get(tkInt);
									term.addOrUpdateDoc(docTerm);
//									term.incColFreq();
//									Map<Integer, Posting> postingsList =
//											termIndex.get(tkInt);
//									Posting posting = postingsList.get(docId);
									// If docId already exist in postingList
//									if ( posting != null) {
//										posting.incTermFreq();
//									}
									// If docId does not exist in postingList
//									else {
//										Posting newPosting = new Posting(docId);
//										term.incdocFreq();
//										postingsList.put(docId, newPosting);
//									}
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
	}

	public class SortByFreq implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			Term term1 = termMap.get(o1);
			Term term2 = termMap.get(o2);
			return term1.getColFreq() - term2.getColFreq();
		}
	}
}