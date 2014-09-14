package edu.buffalo.cse.irf14.analysis;

public class TokenFilterSpecialChars extends TokenFilter {

	public TokenFilterSpecialChars(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token token = stream.next();
			String tkString = token.toString();
			String specChar = "@!#$%^&";
			tkString = tkString.replace(specChar, "");
			if (!tkString.equals("")) {
				token.setTermText(tkString);
			}
			else {
				stream.remove();
			}
		}
		return stream.hasNext();
	}
}
