package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenFilterSpecialChars extends TokenFilter {
	//	private static Pattern pattSpChar, pattSpCharRem;
	private static Matcher matSpChar, matSpCharRem, matIsWord;
	static {
		matSpChar = Pattern.compile("(.*[a-zA-Z])(-)([a-zA-Z].*)").matcher("");
		matSpCharRem = Pattern.compile("[^\\.\\sa-zA-Z0-9-]").matcher("");
		matIsWord = Pattern.compile("[\\w]*").matcher("");
	}

	public TokenFilterSpecialChars(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token token;
			token = stream.next();
			if (token != null) {
				String tkString = token.toString();
				if (tkString != null && !matIsWord.reset(tkString).matches()) {
					// - sign is included because it is used by the symbol class.
					//Only in the case of alpha-alpha, it is removed becaused of
					//the test case a+b-c.
					if (tkString.contains("-")){
						matSpChar.reset(tkString);
						if (matSpChar.matches()) {
							tkString = matSpChar.group(1) + matSpChar.group(3);
						}
					}
					matSpCharRem.reset(tkString);
					tkString = matSpCharRem.replaceAll("");
					if (!tkString.equals("")) {
						token.setTermText(tkString);
					}
					else {
						stream.remove();
					}
				}
			}
		}
		return stream.hasNext();
	}
}
