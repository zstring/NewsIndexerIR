package edu.buffalo.cse.irf14.query;

public interface Expression {

	/**
	 *
	 * @param s
	 * @return
	 */
	public int interpret(String s);
	/**
	 *
	 * @return
	 */
	public String toSudoString();
}
