/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{
	
	private List<Token> tokenList;
	private int idx;
	
	public TokenStream() {
		tokenList = new ArrayList<Token>();
		idx = -1;
	}
	
	
	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	
	@Override
	public boolean hasNext() {
		// TODO YOU MUST IMPLEMENT THIS
		if (idx < tokenList.size() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		// TODO YOU MUST IMPLEMENT THIS
		if (hasNext()) {
			idx += 1;
			return tokenList.get(idx);
		}
		return null;
		
	}
	
	public boolean hasPrevious() {
		if (idx > 0) {
			return true;
		}
		return false;
	}
	
	public Token previous() {
		if (hasPrevious()) {
			idx -= 1 ;
			return tokenList.get(idx);
		}
		return null;
	}
	
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		// TODO YOU MUST IMPLEMENT THIS
		if (idx != -1 && idx < tokenList.size()) {
			tokenList.remove(idx);
			idx -= 1;
		}
	}
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	public void removeAt(int index) {
		// TODO YOU MUST IMPLEMENT THIS
		if (index != -1 && index < tokenList.size()) {
			tokenList.remove(index);
			if (index == idx) {
				idx -= 1;
			}
		}
	}
	
	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		//TODO : YOU MUST IMPLEMENT THIS
		idx = -1;
	}
	
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS
		tokenList.addAll(stream.tokenList);
	}
	
	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS
		return tokenList.get(idx);
	}
	
	public List<Token> getTokenList() {
		return tokenList;
	}
	
	public void setTokenList(List<Token> list) {
		tokenList = list;
	}
	
	public int getCurrentIndex() {
		return idx;
	}
	
	public void setCurrentIndex(int index) {
		idx = index;
	}
	
	public void insertAt(int index,Token token) {
		tokenList.add(index, token);
	}
	public Token[] getPrevTokens() {
		Token[] tkPrevList = new Token[10];
		int sz = tokenList.size();
		for (int i = 0; i < 11 && idx - i >= 0 && idx + i < sz; i++) {
			tkPrevList[i] = tokenList.get(idx + i - 5);
		}
		return tkPrevList;
	}

	/**
	 * Get the Token at particular Index position
	 * @param index
	 * @return
	 */
	public Token getTokenAt(int index) {
		// TODO Auto-generated method stub
		if (index >= 0 && index < tokenList.size())
			return tokenList.get(index);
		return null;
	}
}
