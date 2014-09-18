/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author avinav and himanshu
 *
 */
public class TokenFilterNumber extends TokenFilter{
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
			Token token = stream.next();
			boolean isDate = false;
			String datePattern = "(\\W[AapP].?[mM].?\\W)|(\\W[bB].?[cC].?\\W)|(\\W[Aa].?[dD].?\\W)";
			String datePatternNextToken = "((^|\\s)[AapP].?[mM].?\\W)|((^|\\s)[bB].?[cC].?\\W)|((\\s|^)[Aa].?[dD].?\\W)";
			String monthPattern = "((jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
					+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec))";
			String tkString = token.toString();
			if (tkString.matches(".*[0-9].*")) {
				if (tkString.matches(datePattern)) {
					isDate = true;
				}
				else if (stream.next().toString().matches(datePatternNextToken+"|"+monthPattern)) {
					isDate = true;
					stream.previous();
				}
				else if (stream.previous().toString().matches(monthPattern)) {
					isDate = true;
					stream.next();
				}
				else if(tkString.matches("\\W[12]\\d{3}\\W")) {
					isDate = true;
				}
				else {
					tkString = tkString.replaceAll("(,|\\.)?[0-9]", "");
				}
			}
			if ("".equals(tkString.trim())) {
				stream.remove();
			}
			else {
				token.setTermText(tkString);
			}
			return true;
		}
		return false;
	}

}
