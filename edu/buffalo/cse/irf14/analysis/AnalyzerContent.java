package edu.buffalo.cse.irf14.analysis;

public class AnalyzerContent implements Analyzer {
	private TokenStream stream;
	private TokenFilter[] filter;
	
	public AnalyzerContent(TokenStream stream) {
		// TODO Auto-generated constructor stub
		this.stream = stream;
		TokenFilterFactory tfFactory = TokenFilterFactory.getInstance();
		TokenFilterType[] filters = {TokenFilterType.STOPWORD,TokenFilterType.ACCENT, TokenFilterType.SYMBOL,	
				TokenFilterType.STEMMER, TokenFilterType.CAPITALIZATION,TokenFilterType.DATE,
				TokenFilterType.NUMERIC,TokenFilterType.SPECIALCHARS,
				};
//		TokenFilterType[] filters = {TokenFilterType.STEMMER};
		this.filter = new TokenFilter[filters.length];
		for (int i = 0; i < filters.length; i++) {
			this.filter[i] = tfFactory.getFilterByType(filters[i],
					stream);
			this.filter[i].setIsAnalyzer(true);
		}
	}
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		
		if (stream.hasNext()) {
			stream.next();
			int i;
			for (i = 0; i < filter.length - 1; i++) {
				filter[i].increment();
			}
			return filter[i].increment();
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
