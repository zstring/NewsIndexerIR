/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author avinav and himanshu
 *
 */
public class Term implements Serializable {

	private static final long serialVersionUID = 41L;
	private int colFreq;
	private int docFreq;
	private HashMap<String, Integer> postingMap;
	private HashMap<String, Posting> postingList;
	private String termText;
	private int termId;
	public int getTermId() {
		return termId;
	}

	private double idf;

	public int getColFreq() {
		return colFreq;
	}

	public int getDocFreq() {
		return docFreq;
	}

	public void setIdf(int totalDoc) {
		double df = postingMap.keySet().size();
		double val = (double)(totalDoc) / df;
		idf =  Math.log10(val);
	}

	public HashMap<String, Integer> getPosting() {
		return postingMap;
	}

	/**
	 * returns the PostingList with <String, Posting>
	 * same functionality as getPosting() but returing 
	 * the other object
	 * @return
	 */
	public HashMap<String, Posting> getPostingList() {
		return postingList;
	}

	public double getIdf() {
		return idf;
	}

	public Term() {
		this.colFreq = 0;
		this.docFreq = 0;
		this.termId = 0;
		this.termText = "";
		postingMap = new HashMap<String, Integer>();
		postingList = new HashMap<String, Posting>();
	}

	public Term(String termText, int termId, String docTerm, int posIndex, boolean title,
			IndexType indexType) {
		this.termId = termId;
		this.termText = termText;
		this.colFreq = 1;
		this.docFreq = 1;
		this.postingMap = new HashMap<String, Integer>();
		this.postingMap.put(docTerm, 1);
		this.postingList = new HashMap<String, Posting>();
		Posting post = new Posting(posIndex);
		post.setType(title, termId);
		post.setIndexType(indexType);
		this.postingList.put(docTerm, post);
	}

	/**
	 * returns term string
	 * @return
	 */
	public String getTermText() {
		if (termText !=null) {
			return termText;
		}
		return "";
	}

	/**
	 * set term string as text
	 * @param text
	 */
	public void setTermText(String text) {
		if (text != null) {
			termText = text;
		}
	}

	/**
	 * set termID as id
	 * @param id
	 */
	public void setTermId(int id) {
		termId = id;
	}

	/**
	 * increment collection frequency by 1
	 */
	public void incColFreq() {
		colFreq += 1; 
	}

	/**
	 * increment document frequency by 1
	 */
	public void incdocFreq() {
		docFreq += 1;
	}

	public void addOrUpdateDoc(String docTerm, int index, boolean title,
			IndexType indexType) {
		if (this.postingMap.containsKey(docTerm)) {
			int termFreq = this.postingMap.get(docTerm) + 1;
			this.postingMap.put(docTerm, termFreq);
			Posting val = this.postingList.get(docTerm);
			val.setType(title, termId);
			val.setIndexType(indexType);
			val.addPosIndex(index);
			val.incTermFreq();
		}
		else {
			//Adding new document to the term posting
			this.postingMap.put(docTerm, 1);
			Posting post = new Posting(index);
			post.setType(title, termId);
			post.setIndexType(indexType);
			this.postingList.put(docTerm, post);
			this.incdocFreq();
		}
		this.incColFreq();
	}

	@Override
	public String toString() {
		return "T.Id :" + termId + ", T.Text :" + termText + ", "
				+ "T.Collection_freq :" + colFreq + ", T.doc_freq :" + docFreq 
				+ "Posting List : " + postingMap.toString() +"\n";
	}
}

