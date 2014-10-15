package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

public class QTerm extends QIndexType implements Expression {
	private String term;
	private IndexType index;
	private HashMap<Integer, Double> qVector;

	public QTerm(String term, IndexType index) {
		this.term = term;
		this.index = index;
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		IndexReader ir = reader.get(index);
		edu.buffalo.cse.irf14.index.Term termOb = ir.getTerm(this.term);
		double termIdf = termOb.getIdf();
		int termId = termOb.getTermId();
		if (qVector.get(termId) != null) {
			qVector.put(termId,termIdf);
		}
		else {
			double oldIdf = qVector.get(termId);
			qVector.put(termId, termIdf + oldIdf);
		}
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
	
	public HashMap<Integer, Double> getQueryVector() {
		return qVector;
	}
	
}
