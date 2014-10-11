/**
 * 
 */
package edu.buffalo.cse.irf14.query;

/**
 * @author avinav and himanshu
 *
 */
public class QBracket implements Expression {
	private Expression encapsulate;
	public QBracket(Expression encapsulate) {
		this.encapsulate = encapsulate;
	}

	@Override
	public int interpret(String s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return "[ " + encapsulate.toString() + " ]";
	}

	@Override
	public String toString() {
		return toSudoString();
	}
}
