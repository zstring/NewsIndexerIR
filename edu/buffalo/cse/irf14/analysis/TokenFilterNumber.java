/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Avinav Sharan
 *
 */
public class TokenFilterNumber extends TokenFilter{
//	private static Pattern pattIsNum, pattDate, pattMonth, pattNumRem;
	private static Matcher matIsNum, matDate, matMonth, matNumRem;
	static {
//		pattIsNum = Pattern.compile("[^a-zA-z]*[0-9][^a-zA-Z]*");
//		pattDate = Pattern.compile("[0-9 ]?[AapP]\\.?[mM]\\.?(\\W|$)|"
//				+ "[0-9 ]?[bB]\\.?[cC]\\.?(\\W|$)|[0-9 ]?[Aa]\\.?[dD]\\.?(\\W|$)");
//		//		pattYear = Pattern.compile("\\W[12]\\d{3}\\W");
//		pattNumRem = Pattern.compile("(,|\\.)?[0-9]");
//		pattMonth = Pattern.compile("january|february|march|april|may|june|"
//				+ "july|august|september|october|november|december|jan|feb|mar|apr"
//				+ "|jun|jul|aug|sep|nov|dec");
		matIsNum = Pattern.compile("[^a-zA-z]*[0-9][^a-zA-Z]*").matcher("");
		matDate = Pattern.compile("[0-9 ]?[AapP]\\.?[mM]\\.?(\\W|$)|"
				+ "[0-9 ]?[bB]\\.?[cC]\\.?(\\W|$)|[0-9 ]?[Aa]\\.?[dD]\\.?(\\W|$)").matcher("");
		//		pattYear = Pattern.compile("\\W[12]\\d{3}\\W");
		matNumRem = Pattern.compile("(,|\\.)?[0-9]").matcher("");
		matMonth = Pattern.compile("(?:jan|january)|(?:feb|february)|"
				+ "(?:mar|march)|(?:apr|april)|(?:may)|(?:jun|june)|"
				+ "(?:jul|july)|(?:aug|august)|(?:sep|september)|"
				+ "(?:oct|october)|(?:nov|november)|(?:dec|december)")
				.matcher("");

	}
	public TokenFilterNumber(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		/* 0. If a token contains a number literal. Proceed else quit.
		 * 1. Does token contains AM/PM/BC./AD or month name
		 * 2. Does next token contains AM/P.M./B.C./AD or month name
		 * 3. Does previous token contains month name
		 * 4. if 1,2,3 fails. isDate = false. Is number 4 digit and between 1000 and 3000
		 * 5. if 1,2,3,4 fails: remove number digits only. if empty string remains, remove token. 
		 */
		if (stream.hasNext()) {
			Token token;
			token = stream.next();
			if (token != null && !token.isDate() && !token.isTime()) {
				boolean isDate = false;
				String tkString = token.toString();
				matIsNum.reset(tkString);
				if (matIsNum.matches()) {
//				if (pattIsNum.matcher(tkString).matches()) {
					Token[] prevTokens = stream.getPrevTokens(2);
					StringBuilder prevTokenString = new StringBuilder("");
					for (int i = 0; i < prevTokens.length; i++) {
						if (prevTokens[i] != null) {
							prevTokenString.append(prevTokens[i].toString().toLowerCase()+" ");
						}
					}
					
					if (matDate.reset(prevTokenString).find()) {
//					if (pattDate.matcher(prevTokenString).find()) {
						isDate = true;
					}
					else if (matMonth.reset(prevTokenString).find()) {
//					else if (pattMonth.matcher(prevTokenString).find()) {
						isDate = true;
					}
					else {
//						tkString = pattNumRem.matcher(tkString).replaceAll("");
						tkString = matNumRem.reset(tkString).replaceAll("");
					}
				}
				if ("".equals(tkString.trim())) {
					stream.remove();
				}
				else {
					token.setTermText(tkString);
				}
			}
		}
		return stream.hasNext();
	}
}
