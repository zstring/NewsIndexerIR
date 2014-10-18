/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.document.FieldNames;

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
	public FieldNames toFieldNames() {
		switch(this) {
		case TERM: return FieldNames.CONTENT;
		case AUTHOR: return FieldNames.AUTHOR;
		case CATEGORY: return FieldNames.CATEGORY;
		case PLACE: return FieldNames.PLACE;
		default: throw new IllegalArgumentException();
		}
	}
};

