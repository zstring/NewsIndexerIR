/**
 * 
 */
package edu.buffalo.cse.irf14.index;

/**
 * @author avinav and himanshu
 *
 */
public class Term {
	
	private int colFreq;
	private int docFreq;
	private String termText;
	private int termId;
	
	public Term() {
		colFreq = 0;
		docFreq = 0;
		termId = 0;
		termText = "";
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
	
	@Override
	public String toString() {
		return "T Id :" + termId + ", T Text :" + termText + ", Totel freq :" + colFreq + ", doc freq :" + docFreq;
	}
}

