package edu.buffalo.cse.irf14.index;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

public class Posting implements Serializable {

	private static final long serialVersionUID = 43L;
	private int termFreq;
	private List<Integer> posIndex;
	private boolean type;
	private int termId;
	private IndexType indexType;
	public IndexType getIndexType() {
		return indexType;
	}
	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;
	}

	public Posting() {
		posIndex = new ArrayList<Integer>();
		this.termFreq = 1;
	}
	public List<Integer> getPosIndex() {
		return posIndex;
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

	@Override
	public String toString() {
		return "Term Freq " + termFreq;
	}

	public void setType(boolean type, int termId) {
		if (type) {
			this.type = type;
			this.termId = termId;
		}
	}

	public boolean getType() {
		return this.type;
	}

	public int getTermId() {
		return this.termId;
	}
}
