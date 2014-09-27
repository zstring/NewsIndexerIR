package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenFilterSpecialChars extends TokenFilter {
	private static Pattern pattSpChar, pattSpCharRem;
	static {
		pattSpChar = Pattern.compile("(.*[a-zA-Z])(-)([a-zA-Z].*)");
		pattSpCharRem = Pattern.compile("[^\\.\\sa-zA-Z0-9-]");
	}

	public TokenFilterSpecialChars(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext() || this.isAnalyzer) {
			Token token;
			if (!this.isAnalyzer) {
				token = stream.next();
			}
			else {
				token = stream.getCurrent();
			}
			if (token != null) {
				String tkString = token.toString();
				// - sign is included because it is used by the symbol class. Only in the case of alpha-alpha, it is removed becaused of the test case a+b-c.
				//String specChar = "[^\\.\\sa-zA-Z0-9@-]";
				String specChar = "[^\\.\\sa-zA-Z0-9-]";
				//Matcher matcher = Pattern.compile("(.*[a-zA-Z])(-)([a-zA-Z].*)").matcher(tkString);
				Matcher matcher = pattSpChar.matcher(tkString);
				if (matcher.matches()) {
					tkString = matcher.group(1)+matcher.group(3);
				}
				tkString = pattSpCharRem.matcher(tkString).replaceAll("");
//				tkString = tkString.replaceAll(specChar, "");
				/*if (tkString.matches(".*\\w@\\w.*")) {
				int posAt = tkString.indexOf('@');
				String splitString = tkString.substring(posAt+1);
				tkString = tkString.substring(0,posAt);
				Token newToken = new Token();
				if (!"".equals(splitString)) {
					newToken.setTermText(splitString);
					stream.insertAt(stream.getCurrentIndex()+1, newToken);
					//intentionally not moved the stream to next index so
					//that it does not interfere with other filters in chain
				}

			}
			else if (tkString.matches("@")){
				tkString = tkString.replaceAll("@", "");	
			}*/
				if (!tkString.equals("")) {
					token.setTermText(tkString);
				}
				else {
					stream.remove();
				}
			}
		}
		return stream.hasNext();
	}
}
