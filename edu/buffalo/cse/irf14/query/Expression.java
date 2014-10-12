package edu.buffalo.cse.irf14.query;

import java.util.Map;
import edu.buffalo.cse.irf14.index.Posting;

public interface Expression {

	/**
	 *
	 * @param s
	 * @return
	 */
	public Map<String, Posting> interpret(String s);
	/**
	 *
	 * @return
	 */
	public String toSudoString();
}
