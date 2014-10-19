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
	public Map<Integer, Term> getTermMap() {
		return termMap;
	}

	public transient Set<String> documentSet;


	public BaseIndexer(IndexType indexerType) {
		this.indexerType = indexerType;
		this.isDocCounted = false;
		aFactory = AnalyzerFactory.getInstance();
		fieldName = new ArrayList<FieldNames>();
		if (IndexType.AUTHOR.equals(indexerType)) {
			fieldName.add(FieldNames.AUTHOR);
			fieldName.add(FieldNames.AUTHORORG);
			tkr = new Tokenizer(", ");
		}
		else if (IndexType.CATEGORY.equals(indexerType)) {
			fieldName.add(FieldNames.CATEGORY);
			tkr = new Tokenizer();
		}
		else if (IndexType.PLACE.equals(indexerType)) {
			fieldName.add(FieldNames.PLACE);
			tkr = new Tokenizer();
		}
		else if (IndexType.TERM.equals(indexerType)) {
			fieldName.add(FieldNames.TITLE);
			fieldName.add(FieldNames.CONTENT);
			tkr = new Tokenizer();
		}
		termDict = new HashMap<String, Integer>();
		termMap = new HashMap<Integer, Term>();
		documentSet = new HashSet<String>();
	}

	public Set<Integer> getTermKeys() {
		if (termMap != null) {
			return termMap.keySet();
		}
		return null;
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
	public void addDocument(Document d, 
			HashMap<String, HashMap<Integer, Double>> docVector)
					throws IndexerException {
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
						createIndex(strContent, fn, docTerm, docVector);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createIndex (String strContent, FieldNames fn, String docTerm,
			HashMap<String, HashMap<Integer, Double>> docVector) {
		if (!strContent.isEmpty()) {
			double termWeight = 1.0;
			boolean fieldType = false;
			if (fn.equals(FieldNames.TITLE)) {
				termWeight = 3.0;
				fieldType = true;
			}
			else if (fn.equals(FieldNames.AUTHOR)) {
				termWeight = 10.0;
			}
			else if (fn.equals(FieldNames.CATEGORY)) {
				termWeight = 10.0;
			}
			else if (fn.equals(FieldNames.PLACE)) {
				termWeight = 10.0;
			}
			TokenStream tStream;
			try {
				tStream = tkr.consume(strContent);
				Analyzer aContent = aFactory.getAnalyzerForField
						(fn, tStream);
				while (aContent.increment()) {
				}
				tStream.reset();
				if(tStream.hasNext() && !isDocCounted) {
					this.documentSet.add(docTerm);
					isDocCounted = true;
					if (!docVector.containsKey(docTerm)) {
						docVector.put(docTerm, new HashMap<Integer, Double>());	
					}
				}

				HashMap<Integer, Double> termSpace = docVector.get(docTerm);
				while (tStream.hasNext()) {
					Token tk = tStream.next();
					if (tk != null) {
						String tkString = tk.toString();
						if (tkString != null && !tkString.isEmpty()) {
							// If new Term
							Integer tkInt = termDict.get(tkString);
							if (tkInt == null) {
								//increment term id
								termId += 1;
								Term term = new Term(tkString, termId, docTerm,
										tk.getPosIndex(), fieldType);
								this.termDict.put(tkString, termId);
								this.termMap.put(termId, term);
								termSpace.put(termId, termWeight);
							}
							// If term already exist in index
							else {
								Term term = termMap.get(tkInt);
								term.addOrUpdateDoc(docTerm, tk.getPosIndex(), fieldType);
								Double tFreq = termSpace.get(tkInt);
								if (tFreq != null) {
									termSpace.put(tkInt, tFreq + termWeight);
								}
								else {
									termSpace.put(tkInt, termWeight);
								}
							}
							addOrUpdateSplitTermsInDict(tk, docTerm, termSpace, termWeight, fn);
						}
					}
				}
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method for adding or Updating tokens having multiple 
	 * strings with whitespace delimited, Add them separately 
	 * into dictionary with less weight
	 * @param tk
	 * @param docTerm
	 * @param termSpace
	 * @param termWeight
	 * @param fn
	 */
	private void addOrUpdateSplitTermsInDict(Token tk,
			String docTerm, HashMap<Integer, Double> termSpace, double termWeight, FieldNames fn) {
		String tkString = tk.toString();
		if (!tkString.isEmpty()) {
			String[] splitTk = tkString.split(" ");
			boolean fieldType = false;
			if (fn.equals(FieldNames.TITLE)) {
				fieldType = true;
			}
			int len = splitTk.length;
			if (len > 1) {
				//Reducing the termWeight as it is sub String
				// of the actual token
				termWeight = termWeight * 0.9;
				for (int i = 0; i < len; i++) {
					Integer tkInt = termDict.get(splitTk[i]);
					if (tkInt == null) {
						//increment term id
						termId += 1;
						Term term = new Term(splitTk[i], termId,
								docTerm, tk.getPosIndex(), fieldType);
						this.termDict.put(splitTk[i], termId);
						this.termMap.put(termId, term);
						termSpace.put(termId, termWeight);
					}
					else {
						Term term = termMap.get(tkInt);
						term.addOrUpdateDoc(docTerm, tk.getPosIndex(), fieldType);
						Double tFreq = termSpace.get(tkInt);
						if (tFreq != null) {
							termSpace.put(tkInt, tFreq + termWeight);
						}
						else {
							termSpace.put(tkInt, termWeight);
						}
					}
				}
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
			if (diff < 0) return 1;
			else if (diff > 0) return -1;
			else 
				return (term1.getTermText().compareTo(term2.getTermText()));
		}
	}
}