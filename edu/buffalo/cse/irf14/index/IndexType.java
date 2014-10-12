/**
 * 
 */
package edu.buffalo.cse.irf14.index;

/**
 * @author nikhillo
 *
 */
public enum IndexType {
	TERM, AUTHOR, CATEGORY, PLACE;
	@Override
	public String toString() {
		switch(this) {
		case TERM: return "Term";
		case AUTHOR: return "Author";
		case CATEGORY: return "Category";
		case PLACE: return "Place";
		default: throw new IllegalArgumentException();
		}
	}
};

