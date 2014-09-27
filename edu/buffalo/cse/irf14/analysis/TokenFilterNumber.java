/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Avinav Sharan
 *
 */
public class TokenFilterNumber extends TokenFilter{
	private static Pattern pattIsNum, pattDate, pattYear, pattMonth, pattNumRem,pattTimeYear;
	static {
		pattIsNum = Pattern.compile("[^a-zA-z]*[0-9][^a-zA-Z]*");
		pattDate = Pattern.compile("[0-9 ]?[AapP]\\.?[mM]\\.?(\\W|$)|"
				+ "[0-9 ]?[bB]\\.?[cC]\\.?(\\W|$)|[0-9 ]?[Aa]\\.?[dD]\\.?(\\W|$)");
		//		pattYear = Pattern.compile("\\W[12]\\d{3}\\W");
		pattNumRem = Pattern.compile("(,|\\.)?[0-9]");
		pattMonth = Pattern.compile("january|february|march|april|may|june|"
				+ "july|august|september|october|november|december|jan|feb|mar|apr"
				+ "|jun|jul|aug|sep|nov|dec");

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
			if (token != null) {
				boolean isDate = false;
				String tkString = token.toString();
				int currIndex = stream.getCurrentIndex();
				if (!token.isDate() && pattIsNum.matcher(tkString).matches()) {
					Token[] prevTokens = stream.getPrevTokens(2);
					StringBuilder prevTokenString = new StringBuilder("");
					for (int i = 0; i < prevTokens.length; i++) {
						if (prevTokens[i] != null) {
							prevTokenString.append(prevTokens[i].toString().toLowerCase()+" ");
						}
					}
					if (pattDate.matcher(prevTokenString).find()) {
						isDate = true;
					}
					else if (pattMonth.matcher(prevTokenString).find()) {
						isDate = true;
					}
					else {
						tkString = pattNumRem.matcher(tkString).replaceAll("");
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
