/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Posting;

/**
 * @author avinav and himanshu
 *
 */
public class ExpressionParser implements Expression {
	private Expression expression;
	
	public Expression getExpression() {
		return expression;
	}
	
	public void expressionParser(String userQuery, String defaultOperator) throws QueryParserException {
		if (userQuery == null || userQuery.isEmpty()) {
			throw new QueryParserException("Query Null");
		}
		
		Matcher matcher = Pattern.compile("(Author|Category|Term|Place)(:)",
				Pattern.CASE_INSENSITIVE).matcher(userQuery);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1)+" ");
		}
		matcher.appendTail(sb);
		userQuery = sb.toString();

		Matcher matcher1 = Pattern.compile("(\\()|(\\))|(\")").matcher(userQuery);
		StringBuffer sb1 = new StringBuffer();
		while(matcher1.find()) {
			matcher1.appendReplacement(sb1, " "+matcher1.group(0)+" ");
		}
		matcher1.appendTail(sb1);
		userQuery = sb1.toString();
		
		Tokenizer tkr = new Tokenizer(" ");
		TokenStream tokenStream = null;
		try {
			tokenStream = tkr.consume(userQuery);
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tokenStream == null) {
			throw new QueryParserException("Query Parser Exception: tokenStream empty");
		}
		
		Stack<Expression> operator = new Stack<Expression>();
		Stack<Expression> operand = new Stack<Expression>();
		boolean dQuotesOn = false,dQuotesOff = false, defaultIndex = true, 
				superDefaultIndex = false, isDefaultOperator = false, notOperator = false;
		String index = "";
		StringBuilder quotedString = new StringBuilder("\"");
		while (tokenStream.hasNext()) {
			String token = tokenStream.next().toString();
			if (defaultIndex) {
				index = "Term";
			}
			if ("\"".equals(token)) {
				dQuotesOn = !dQuotesOn;
				if (dQuotesOff) {
					Expression qTerm = new QTerm(quotedString.toString().trim()+"\"", index);
					if(notOperator) {
						operand.push(new NotOperator(qTerm));
						notOperator = false;
					}
					else {
						operand.push(qTerm);
					}
					quotedString = new StringBuilder("\"");
					if(!superDefaultIndex) {
						defaultIndex = true;
					}
					if (isDefaultOperator) {
						operator.push(new Term("OR"));
					}
				}
				dQuotesOff = !dQuotesOff;
			}
			else if(dQuotesOn) {
				quotedString.append(token+" ");
			}
			else if ("AUTHOR".equalsIgnoreCase(token) || "TERM".equalsIgnoreCase(token) ||
					"CATEGORY".equalsIgnoreCase(token) || "PLACE".equalsIgnoreCase(token)) {
				index = token;
				defaultIndex = false;
				isDefaultOperator = false;
			}
			else if(")".equals(token)) {
				operator.push(new Term(token));
				ObjectifyBracket(operator, operand);
				superDefaultIndex = false;
				defaultIndex = true;
				
				isDefaultOperator = false;
			}
			else if ("AND".equalsIgnoreCase(token)||"OR".equalsIgnoreCase(token)||
					"NOT".equalsIgnoreCase(token)||"(".equals(token)) {
				if ("(".equals(token)) {
					superDefaultIndex = true;
				}
				if("NOT".equalsIgnoreCase(token)) {
					operator.push(new Term("AND"));
					notOperator = true;
				}
				else {
					Expression term = new Term(token);
					operator.push(term);
				}
				isDefaultOperator = false;
			}
			else {
				Expression qterm = new QTerm(token,index);
				
				if(notOperator) {
					operand.push(new NotOperator(qterm));
					notOperator = false;
				}
				else {
					operand.push(qterm);
				}
				/* if defaultIndex is true, default index, "term" is used
				 * superDefaultIndex is true when it sees and (
				 * defaultIndex should not be made true
				 */
				if(!superDefaultIndex) {
					defaultIndex = true;
				}
				// if two continuous terms occur isDefaultOperator index is true
				
				if (isDefaultOperator) {
					operator.push(new Term("OR"));
				}
				isDefaultOperator = true;
			}
		}
		ObjectifyEnd(operator, operand);
		this.expression = operand.pop();
	}
	
	public void ObjectifyBracket(Stack<Expression> operator,Stack<Expression> operand) throws QueryParserException {
		// Removing the ) that was pushed for calling this method
		Expression popped = operator.pop();
		/* It can only encounter AND,NOT,OR or ( 
		 * It cannot encounter ) because it is the first ) itself.
		 */
		while (true) {
			popped = operator.pop();
			Objectify(operator, operand, popped);
			if ("(".equals(popped.toString())) {
				break;
			}
		}
	}
	
	public void ObjectifyEnd(Stack<Expression> operator,Stack<Expression> operand) throws QueryParserException {
//		if (operator.isEmpty()) {
//			throw new Exception("Operator Stack underflow while ending");
//		}
		Expression popped;
		while (!operator.isEmpty()) {
			popped = operator.pop();
			Objectify(operator, operand, popped);
		}
	}
	/* It can only encounter AND,NOT,OR or ( 
	 * It cannot encounter ) because it is the first ) itself.
	 */
	public void Objectify(Stack<Expression> operator,Stack<Expression> operand, Expression popped) throws QueryParserException {
		String poppedText = popped.toString();
		if ("AND".equalsIgnoreCase(poppedText)) {
			if (operand.size() >= 2) {
				Expression rightOperand = operand.pop();
				Expression leftOperand = operand.pop();
				Expression andOp = new AndOperator(leftOperand,rightOperand);
				operand.push(andOp);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for AND operator");
			}
		}
		else if ("OR".equalsIgnoreCase(poppedText)) {
			if (operand.size() >=2) {
				Expression rightOperand = operand.pop();
				Expression leftOperand = operand.pop();
				Expression orOp = new OrOperator(leftOperand,rightOperand);
				operand.push(orOp);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for OR operator");
			}
		}
		else if("NOT".equalsIgnoreCase(poppedText)) {
			if (operand.size() >= 1) {
				Expression op = operand.pop();
				Expression notOp = new NotOperator(op);
				operand.push(notOp);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for NOT operator");
			}
		}
		else if("(".equals(poppedText)) {
			if (operand.size() >= 1) {
				Expression encaps = operand.pop();
				Expression opBrack = new QBracket(encaps);
				operand.push(opBrack);
			}
			else {
				throw new QueryParserException("Operand Stack Underflow for Brackets");
			}
		}
	}

	@Override
	public Map<String, Posting> interpret(HashMap<IndexType, IndexReader> reader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSudoString() {
		// TODO Auto-generated method stub
		return "{ " + expression.toString() + " }";
	}
	
	@Override
	public String toString() {
		return toSudoString();
	}

}
