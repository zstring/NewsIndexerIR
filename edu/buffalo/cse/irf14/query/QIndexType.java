package edu.buffalo.cse.irf14.query;
import java.util.Map;
import edu.buffalo.cse.irf14.index.Posting;


public abstract class QIndexType implements Expression {

	@Override
	public abstract Map<String, Posting> interpret(String s);
}
