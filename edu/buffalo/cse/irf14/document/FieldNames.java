/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import edu.buffalo.cse.irf14.index.IndexType;

/**
 * @author nikhillo
 * This is an enumeration that defines the different field names
 */
public enum FieldNames {
	FILEID, CATEGORY, TITLE, AUTHOR, AUTHORORG, PLACE, NEWSDATE, CONTENT;
	public IndexType toIndexType() {
		switch(this) {
		case TITLE: return IndexType.TERM;
		case AUTHOR: return IndexType.AUTHOR;
		case CATEGORY: return IndexType.CATEGORY;
		case PLACE: return IndexType.PLACE;
		case CONTENT: return IndexType.TERM;
		case AUTHORORG: return IndexType.AUTHOR;
		case FILEID: return IndexType.TERM;
		case NEWSDATE: return IndexType.TERM;
		default: return IndexType.TERM;
		}
	}
};

