/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author avinav sharan
 *
 */
public class AnalyzerPlace implements Analyzer {

	private TokenStream stream;
	private TokenFilter filter[];

	public AnalyzerPlace(TokenStream stream) {
		// TODO Auto-generated constructor stub
		this.stream = stream;
		TokenFilterFactory tfFactory = TokenFilterFactory.getInstance();
		TokenFilterType[] filters = {TokenFilterType.STOPWORD,TokenFilterType.ACCENT,
				TokenFilterType.SYMBOL, TokenFilterType.STEMMER, TokenFilterType.CAPITALIZATION,
				TokenFilterType.DATE, TokenFilterType.NUMERIC, TokenFilterType.SPECIALCHARS
				};
		this.filter = new TokenFilter[filters.length];
		for (int i = 0; i < filters.length; i++) {
			this.filter[i] = tfFactory.getFilterByType(filters[i],
					stream);
			}
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream.hasNext()) {
			int i;
			for (i = 0; i < filter.length; i++) {
				while(filter[i].increment()) {
				}
				if (i != filter.length - 1) {
					stream.reset();
				}
			}
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
