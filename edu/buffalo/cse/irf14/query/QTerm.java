package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
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
		String aTerm = getAnalyzedTerm(this.term);
		return ir.getPostingList(aTerm);
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
		String aTerm = getAnalyzedTerm(this.term);
		edu.buffalo.cse.irf14.index.Term termOb = ir.getTerm(aTerm);
		if (termOb != null) {
			double termIdf = termOb.getIdf();
			int termId = termOb.getTermId();
			qVector.put(termId,termIdf);
			System.out.println(termId + " " + this.term);
			return qVector;
		}
		return null;
	}

	private String getAnalyzedTerm(String string) {
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		try {
			TokenStream stream = new TokenStream(string);
			Analyzer analyzer = fact.getAnalyzerForField(index.toFieldNames(), stream);
			while (analyzer.increment()) {
			}
			stream = analyzer.getStream();
			stream.reset();
			return stream.next().toString();
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
