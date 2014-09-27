package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenFilterStemmer extends TokenFilter {

	private Pattern pattAlpha;
	private Matcher matAlpha;
	/**
	 * parameterized constructor calling super constructor
	 * @param stream
	 */
	public TokenFilterStemmer(TokenStream stream) {
		super(stream);
		String alphaRegex = "[a-zA-Z]+";
		pattAlpha = Pattern.compile(alphaRegex);
		matAlpha = pattAlpha.matcher("");
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stu
		if (stream.hasNext()) {
			Token token;
			token = stream.next();
			if (token != null) {
				String tkText = token.getTermText();
				if (tkText != null && tkText.length() > 1) {
//					Matcher mat = pattAlpha.matcher(tkText);
					matAlpha.reset(tkText);
					if (matAlpha.matches()) {
						Stemmer stemmer = new Stemmer();
						char[] tkTextChar = tkText.toCharArray();
						stemmer.add(tkTextChar, tkTextChar.length);
						stemmer.stem();
						String stemString = stemmer.toString();
						if (!tkText.equals(stemString)) {
							token.setTermText(stemString);
						}
					}
				}
			}
		}
		return stream.hasNext();
	}

}
