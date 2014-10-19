package edu.buffalo.cse.irf14.query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		System.out.println("a");
		List<String> aTerms = getAnalyzedTerm(this.term);
		Map<String, Posting> posting = new HashMap<String, Posting>();
		for (int i = 0; i < aTerms.size(); i++) {
			Map<String, Posting> ret = ir.getPostingList(aTerms.get(i));
			if (ret != null)
				posting.putAll(ret);
		}
		return posting;
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
		List<String> aTerms = getAnalyzedTerm(this.term);
		for (int i = 0; i < aTerms.size(); i++) {
			edu.buffalo.cse.irf14.index.Term termOb = ir.getTerm(aTerms.get(i));
			//this weight represent the weight for the given term
			// if its in another format than make it less e.g. 0.3
			double wt = 1;
			if (i != 0) {
				wt = 0.7;
			}
			if (termOb != null) {
				double termIdf = termOb.getIdf();
				int termId = termOb.getTermId();
				qVector.put(termId, termIdf * wt);
				System.out.println(termId + " " + this.term);
			}
		}
		return qVector;
	}

	private List<String> getAnalyzedTerm(String string) {
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		// Number of Permuation of a given String
		// 1. Camel Case, 2. all lower, 3. given format
		// 1. Ios 2. ios 3. iOS
		String lowerCase = string.toLowerCase();
		String camelCase = string.toUpperCase();
		if (string.length() > 1) {
			camelCase = string.substring(0, 1).toUpperCase() 
					+ string.substring(1).toLowerCase();
		}
		List<String> terms = new ArrayList<String>();
		terms.add(string);
		if (!string.equals(lowerCase)) {
			terms.add(lowerCase);
		}
		if (!string.equals(camelCase)) {
			terms.add(camelCase);
		}
		try {
			for (int i = 0; i < terms.size(); i++) {
				TokenStream stream = new TokenStream(terms.get(i));
				Analyzer analyzer = fact.getAnalyzerForField(index.toFieldNames(), stream);
				while (analyzer.increment()) {
				}
				stream = analyzer.getStream();
				stream.reset();
				terms.set(i, stream.next().toString());
			}
			return terms;
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}