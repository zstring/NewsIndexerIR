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

	private static final String tab00c0 = "AAAAAAACEEEEIIII" +
		    "DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
		    "aaaaaaaceeeeiiii" +
		    "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
		    "AaAaAaCcCcCcCcDd" +
		    "DdEeEeEeEeEeGgGg" +
		    "GgGgHhHhIiIiIiIi" +
		    "IiJjJjKkkLlLlLlL" +
		    "lLlNnNnNnnNnOoOo" +
		    "OoOoRrRrRrSsSsSs" +
		    "SsTtTtTtUuUuUuUu" +
		    "UuUuWwYyYZzZzZzF";
	
	public TokenFilterAccent(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext() || this.isAnalyzer) {
			Token t;
			if (!this.isAnalyzer) {
				t = stream.next();
			}
			else {
				t = stream.getCurrent();
			}
			if (t != null) {
				String tString = t.getTermText();
				if (tString != null && !tString.equals("")) {
					tString = processString(tString);
//					tString = Normalizer.normalize(tString, Normalizer.Form.NFD);
//					tString = tString.replaceAll("[^\\p{ASCII}]", "");
					t.setTermText(tString);
				}
			}
		}
		return stream.hasNext();
	}

	private String processString(String tString) {
		// TODO Auto-generated method stub
		char[] v = new char[tString.length()];
		char one;
		for (int i = 0; i < tString.length(); i++) {
			one = tString.charAt(i);
			if (one >= '\u00c0' && one <= '\u017f') {
				one = tab00c0.charAt((int) one - '\u00c0');
			}
			v[i] = one;
		}
		return new String(v);
	}

}
