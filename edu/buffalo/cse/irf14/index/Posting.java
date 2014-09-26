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
	private int termFreq;

	public int getTermFreq() {
		return termFreq;
	}

	/**
	 * Instantiate class.
	 */
	public Posting() {
		this.termFreq = 1;
	}

	/**
	 * Increment term frequency of this posting
	 */
	public void incTermFreq() {
		termFreq += 1;
	}
	
	@Override
	public String toString() {
		return "[Term Freq :" + termFreq + " ]";
	}
}
