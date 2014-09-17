package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenFilterStemmer extends TokenFilter {

	/**
	 * parameterized constructor calling super constructor
	 * @param stream
	 */
	public TokenFilterStemmer(TokenStream stream) {
		super(stream);
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stu
		if (stream.hasNext()) {
			Token tk = stream.next();
			String tkText = tk.getTermText();
			if (tkText != null && tkText.length() > 1) {
				String alphaRegex = "[a-zA-Z]+";
				Matcher mat = Pattern.compile(alphaRegex).matcher(tkText);
				if (mat.matches()) {
					Stemmer stemmer = new Stemmer();
					char[] tkTextChar = tkText.toCharArray();
					stemmer.add(tkTextChar, tkTextChar.length);
					stemmer.stem();
					String stemString = stemmer.toString();
					if (!tkText.equals(stemString)) {
						tk.setTermText(stemString);
					}
				}
			}
		}
		return stream.hasNext();
	}

}
