/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.util.HashMap;

/**
 * @author nikhillo
 * Wrapper class that holds {@link FieldNames} to value mapping
 */
public class Document {
	//Sample implementation - you can change this if you like
	private HashMap<FieldNames, String[]> map;
	
	/**
	 * Default constructor
	 */
	public Document() {
		map = new HashMap<FieldNames, String[]>();
	}
	
	/**
	 * Method to set the field value for the given {@link FieldNames} field
	 * @param fn : The {@link FieldNames} to be set
	 * @param o : The value to be set to
	 */
	public void setField(FieldNames fn, String... o) {
		map.put(fn, o);
	}
	
	/**
	 * Method to get the field value for a given {@link FieldNames} field
	 * @param fn : The field name to query
	 * @return The associated value, null if not found
	 */
	public String[] getField(FieldNames fn) {
		return map.get(fn);
	}
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Category :" + this.getField(FieldNames.CATEGORY)[0] + "\n");
		result.append("FILEID : " + this.getField(FieldNames.FILEID)[0] + "\n");
		result.append("Title : " + this.getField(FieldNames.TITLE)[0] + "\n");
		if (this.getField(FieldNames.AUTHOR) != null && this.getField(FieldNames.AUTHOR).length > 0) {
//			for (int i = 0; i<this.getField(FieldNames.AUTHOR).length; i++)
		result.append("Author : " + this.getField(FieldNames.AUTHOR)[0] + "\n");
		}
		if (this.getField(FieldNames.AUTHORORG) != null)
			result.append("Author Org : " + this.getField(FieldNames.AUTHORORG)[0] + "\n");
		if (this.getField(FieldNames.NEWSDATE) != null)
			result.append("News Date : " + this.getField(FieldNames.NEWSDATE)[0] + "\n");
		if (this.getField(FieldNames.PLACE) != null)
			result.append("Place : " + this.getField(FieldNames.PLACE)[0] + "\n");
		if (this.getField(FieldNames.CONTENT) != null)
			result.append("Content : " + this.getField(FieldNames.CONTENT)[0] + "\n");
		return result.toString();
	}
}
