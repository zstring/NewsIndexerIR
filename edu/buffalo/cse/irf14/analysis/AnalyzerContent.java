package edu.buffalo.cse.irf14.analysis;

public class AnalyzerContent implements Analyzer {
	private TokenStream stream;
	
	public AnalyzerContent(TokenStream stream) {
		// TODO Auto-generated constructor stub
		this.stream = stream;
	}
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		
		if (stream.hasNext()) {
			TokenFilterFactory tfFactory = TokenFilterFactory.getInstance();
			TokenFilter tfStem = tfFactory.getFilterByType
					(TokenFilterType.STEMMER, stream);
			return tfStem.increment();
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
