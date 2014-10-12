/**
 * 
 */
package edu.buffalo.cse.irf14.query;
import java.util.Map;
import edu.buffalo.cse.irf14.index.Posting;

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
	public Map<String, Posting> interpret() {
		// TODO Auto-generated method stub
		return null;
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