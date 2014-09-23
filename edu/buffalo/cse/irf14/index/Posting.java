/**
 * 
 */
package edu.buffalo.cse.irf14.index;

/**
 * @author avinav and himanshu
 *
 */
public class Posting {
	
	private int docId;
	private int termFreq;
	
	/**
	 * Instantiate class with documentID = 0.
	 */
	public Posting() {
		docId = 0;
		termFreq = 0;
	}
	/**
	 * set docId as documentId
	 * @param documentId
	 */
	public void setDocId(int documentId) {
		this.docId = documentId;
	}
	/**
	 * Increment term frequency of this posting
	 */
	public void incTermFreq() {
		termFreq += 1;
	}

}
