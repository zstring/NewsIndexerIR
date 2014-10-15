package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class QTerm extends QIndexType implements Expression {
	private String term;
	private IndexType index;

	public QTerm(String term, IndexType index) {
		this.term = term;
		this.index = index;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		IndexReader ir = reader.get(index);
		return ir.getPostingList(this.term);
	}
	

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return index.toString() + ":" + term;
	}

	@Override
	public String toString() {
		return toSudoString();
	}
	

	@Override
	public Map<Integer, Double> getQVector(
			HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		IndexReader ir = reader.get(index);
		HashMap<Integer, Double> qVector = new HashMap<Integer, Double>();
		edu.buffalo.cse.irf14.index.Term termOb = ir.getTerm(this.term);
		if (termOb != null) {
			double termIdf = termOb.getIdf();
			int termId = termOb.getTermId();
			qVector.put(termId,termIdf);
			return qVector;
		}
		return null;
	}
	
}
