/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.Serializable;

/**
 * @author avinav and himanshu
 *
 */
public class Posting implements Serializable {
	
	private static final long serialVersionUID = 40L;
	private int docId;
	private int termFreq;
	
	/**
	 * Instantiate class with documentID = 0.
	 */
	public Posting() {
		this.docId = 0;
		this.termFreq = 0;
	}
	
	public Posting(int documentId) {
		this.docId = documentId;
		this.termFreq = 1;
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
	
	@Override
	public String toString() {
		return "[ Doc Id :" + docId + ", Term Freq :" + termFreq + " ]";
	}
}
