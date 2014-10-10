/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

/**
 * @author avinav and himanshu
 *
 */
public class ExpressionParser implements Expression {
	private Expression expression;
	
	public Expression expressionParser(String userQuery, String defaultOperator) throws Exception {
		if (userQuery == null || !userQuery.isEmpty()) {
			return null;
		}
		
		Matcher matcher = Pattern.compile("(Author|Category|Term|Place)(:)",
				Pattern.CASE_INSENSITIVE).matcher(userQuery);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1)+" ");
		}
		matcher.appendTail(sb);
		userQuery = sb.toString();

		Matcher matcher1 = Pattern.compile("(\\()|(\\))").matcher(userQuery);
		StringBuffer sb1 = new StringBuffer();
		while(matcher1.find()) {
			matcher1.appendReplacement(sb1, " "+matcher1.group(0)+" ");
		}
		matcher1.appendTail(sb1);
		userQuery = sb1.toString();
		
		Tokenizer tkr = new Tokenizer(" ");
		TokenStream tokenStream = tkr.consume(userQuery);
		if (tokenStream == null) {
			throw new Exception("Query Parser Exception: tokenStream empty");
		}
		
		Stack<Expression> operator = new Stack<Expression>();
		Stack<Expression> operand = new Stack<Expression>();
		boolean dQuotesOn = false,dQuotesOff = false;
		StringBuilder quotedString = new StringBuilder("");
		while (tokenStream.hasNext()) {
			String token = tokenStream.next().toString();
			String index = "TERM";
			
			if ("\"".equals(token)) {
				dQuotesOn = !dQuotesOn;
				if (dQuotesOff) {
					Expression term = new Term(quotedString.toString());
					operand.push(term);
					quotedString = new StringBuilder("");
				}
				dQuotesOff = !dQuotesOff;
			}
			else if(dQuotesOn) {
				quotedString.append(token);
			}
			else if ("AUTHOR".equalsIgnoreCase(token) || "TERM".equalsIgnoreCase(token) ||
					"CATEGORY".equalsIgnoreCase(token) || "PLACE".equalsIgnoreCase(token)) {
				index = token;
			}
			else if(")".equals(token)) {
				Expression popped = operator.pop();
				Objectify(operator,operand,popped);
			}
			else if ("AND".equalsIgnoreCase(token)||"OR".equalsIgnoreCase(token)||
					"NOT".equalsIgnoreCase(token)||"(".equalsIgnoreCase(token)) {
				Expression term = new Term(token);
				operator.push(term);
			}
			else {
				Expression qterm = new QTerm(token,index);
				operand.push(qterm);
			}
			
		}
		
		return null;
	}
	
	public void Objectify(Stack<Expression> operator,Stack<Expression> operand, Expression popped) {
		if ("AND".equalsIgnoreCase(popped.toString())) {
			
		}
		Expression qterm = operator.pop();
		
	}

	@Override
	public int interpret(String s) {
		// TODO Auto-generated method stub
		return 0;
	}

}
