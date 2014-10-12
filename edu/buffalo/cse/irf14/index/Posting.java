package edu.buffalo.cse.irf14.index;
import java.util.ArrayList;
import java.util.List;

public class Posting {

	private int termFreq;
	private List<Integer> posIndex;

	public Posting() {
		posIndex = new ArrayList<Integer>();
		this.termFreq = 1;
	}
	
	public Posting(int index) {
		this.termFreq = 1;
		posIndex = new ArrayList<Integer>();
		posIndex.add(index);
	}

	public void setTermFreq() {
		this.termFreq = 1;
	}

	public void incTermFreq() {
		this.termFreq = this.termFreq + 1;
	}

	public int getTermFreq() {
		return this.termFreq;
	}

	public void addPosIndex(int index) {
		// TODO Auto-generated method stub
		this.posIndex.add(index);
	}
}
