/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

/**
 * @author avinav
 *
 */
public class TokenFilterAccent extends TokenFilter{

	public TokenFilterAccent(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			Token t = stream.next();
			if (t != null) {
				String tString = t.getTermText();
				if (tString != null && !tString.equals("")) {
					tString = Normalizer.normalize(tString, Normalizer.Form.NFD);
					tString = tString.replaceAll("[^\\p{ASCII}]", "");
					t.setTermText(tString);
				}
			}
		}
		
		return stream.hasNext();
	}

}
