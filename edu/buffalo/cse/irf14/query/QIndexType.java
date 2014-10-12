package edu.buffalo.cse.irf14.query;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;


public abstract class QIndexType implements Expression {

	@Override
	public abstract Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader);
}
