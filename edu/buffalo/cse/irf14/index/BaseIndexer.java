package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	private int docId = 0;
	private int termId = 0;
	private boolean isDocCounted;
	private IndexType indexerType;
	private List<FieldNames> fieldName;
	private transient Tokenizer tkr;
	private transient AnalyzerFactory aFactory;
	public Map<String, Integer> termDict;
	public Map<Integer, Term> termMap;
	public transient Set<String> documentSet;


	public BaseIndexer(IndexType indexerType) {
		this.indexerType = indexerType;
		this.isDocCounted = false;
		tkr = new Tokenizer();
		aFactory = AnalyzerFactory.getInstance();
		fieldName = new ArrayList<FieldNames>();
		if (IndexType.AUTHOR.equals(indexerType)) {
			fieldName.add(FieldNames.AUTHOR);
		}
		else if (IndexType.CATEGORY.equals(indexerType)) {
			fieldName.add(FieldNames.CATEGORY);
		}
		else if (IndexType.PLACE.equals(indexerType)) {
			fieldName.add(FieldNames.PLACE);
		}
		else if (IndexType.TERM.equals(indexerType)) {
			fieldName.add(FieldNames.TITLE);
			fieldName.add(FieldNames.CONTENT);
		}
		termDict = new HashMap<String, Integer>();
		termMap = new HashMap<Integer, Term>();
		documentSet = new HashSet<String>();
	}

	public Set<Integer> getTermKeys() {
		return termMap.keySet();
	}
	
	public int getDocNum() {
		return docId;
	}
	public void setDocNum() {
		if (this.documentSet != null) {
			this.docId = this.documentSet.size();
		}
		else {
			this.docId = 0;
		}
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
				isDocCounted = false;
				String docTerm = "";
				if (d.getField(FieldNames.FILEID).length > 0) {
					docTerm = d.getField(FieldNames.FILEID)[0];
				}
				String strContent = "";
				for (int i = 0; i < fieldName.size(); i++) {
					FieldNames fn = fieldName.get(i);
					if (d.getField(fn) != null && d.getField(fn).length > 0) {
						strContent = d.getField(fn)[0].trim();
						createIndex(strContent, fn, docTerm);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createIndex (String strContent, FieldNames fn, String docTerm) {
		if (!strContent.isEmpty()) {
			TokenStream tStream;
			try {
				tStream = tkr.consume(strContent);
				Analyzer aContent = aFactory.getAnalyzerForField
						(fn, tStream);
				while (aContent.increment()) {
				}
				tStream.reset();
				while (tStream.hasNext()) {
					Token tk = tStream.next();
					if (tk != null) {
						String tkString = tk.toString();
						if (tkString != null && !tkString.isEmpty()) {
							if (!isDocCounted) {
//								docId += 1;
								this.documentSet.add(docTerm);
								isDocCounted = true;
							}
							// If new Term
							Integer tkInt = termDict.get(tkString);
							if (tkInt == null) {
								//increment term id
								termId += 1;
								Term term = new Term(tkString, termId, docTerm);
								this.termDict.put(tkString, termId);
								this.termMap.put(termId, term);
							}
							// If term already exist in index
							else {
								Term term = termMap.get(tkInt);
								term.addOrUpdateDoc(docTerm);
							}
						}
					}
				}
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class SortByFreq implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			Term term1 = termMap.get(o1);
			Term term2 = termMap.get(o2);
			int diff = term1.getColFreq() - term2.getColFreq();
			if (diff < 0) return -1;
			else if (diff > 0) return 1;
			else 
				return term1.getTermText().compareTo(term2.getTermText());
		}
	}
}