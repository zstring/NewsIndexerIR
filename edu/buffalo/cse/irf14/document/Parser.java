/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		try {
			FileReader reader = new FileReader(filename);
			BufferedReader buff = new BufferedReader(reader);
			Document d = new Document();
			int lastIndex = filename.lastIndexOf(File.separator), titleCounter = 0;
			String categoryPath = filename.substring(0,lastIndex);
			String line;
			String[] strAuthors=null;				//Had to use it because Document.SetField() accepts a String[]
			List<String> authors = new ArrayList<String>();   
			// Had to use it because lists were easily modified.
			
			while ((line = buff.readLine()) != null) {
				line = line.trim();
				if (!line.equals("")) {
					//Parse the first line as titile.
					if (titleCounter == 0) {
						d.setField(FieldNames.TITLE,line);
						titleCounter += 1;
					}
					//If the second line has <AUTHOR> tag, call extractAuthor function to parse auhtors and organization.
					else if(line.contains("<AUTHOR>")) {
						authors=extractAuthor(line);
						strAuthors = authors.subList(1,authors.size()).toArray(new String[authors.size()-1]);
						
						if(authors.get(0).equals("n")) {
							d.setField(FieldNames.AUTHOR, strAuthors ); 
						}
						else {
							d.setField(FieldNames.AUTHOR,Arrays.copyOfRange(strAuthors, 1, strAuthors.length-2));
							d.setField(FieldNames.AUTHORORG,strAuthors[strAuthors.length-1]);
						}
					}
					else {
						//To Implement: Parse line with place,date and content.
							List<String> placeDate = extractPlaceAndDate(line);
							d.setField(FieldNames.PLACE, placeDate.get(0));
							d.setField(FieldNames.NEWSDATE, placeDate.get(0));
							d.setField(FieldNames.CONTENT, placeDate.get(0));
					}
				}
			}

			String fileId = filename.substring(lastIndex+1);
			String category = categoryPath.substring(categoryPath.lastIndexOf(File.separator+1));
			
		} catch (Exception e) {
			System.out.println("File may not exist.");
		}
		

		return null;
	}
	
	/**
	 * This method extract the Place, date and remaining 
	 * content of the line.
	 * @param line a String line that to be parsed
	 * @return the extracted place, date and content from that line
	 */
	public static List<String> extractPlaceAndDate(String line) {
		List<String> extract = new ArrayList<String>();
		String months = "(-)|((jan)|(feb)|(mar)|(apr)|(may)|(jun)|"
				+ "(jul)|(aug)|(sep)|(oct)|(nov)|(dec))";
		Matcher mat = Pattern.compile(months,
				Pattern.CASE_INSENSITIVE).matcher(line);
		int start = 0, end = 0;
		String place = "", date = "", content = "";
		while (mat.find()) {
			System.out.println(mat.group());
			if ("-".equals(mat.group())) {
				end = mat.end();
				date = line.substring(start, end - 1);
				content = line.substring(end);
			}
			else if (mat.group() != null) {
				start = mat.start();
				end = mat.end();
				place = line.substring(0, start);
				place = place.trim().substring(0, place.length() - 2);
			}
		}
		extract.add(place);
		extract.add(date);
		extract.add(content);
		return extract;
	}
	
	/**
	 * Function to extract parse line with tag <AUTHOR> </AUTHOR>
	 * @param line
	 * @return
	 */
	public static List<String> extractAuthor(String line) {
		int authorStartIndex = 0, authorEndIndex = 0, orgStartIndex = 0, flag = 0;
		List <String> authors = new ArrayList<String>();
		String orgName = null;
		//if first element = n, there is no organization name in the list
		authors.add("n");
		Matcher mat=Pattern.compile("[Bb][Yy]|[aA][Nn][Dd]|,|</AUTHOR>").matcher(line);
		while(mat.find()) {
			System.out.println(mat.group()+" "+mat.start()+" "+mat.end());
			if(mat.group().equalsIgnoreCase("by")) {
				authorStartIndex = mat.end(); 
			} 
			if(mat.group().equalsIgnoreCase("and")&&flag==0) {
				authorEndIndex = mat.start();
				authors.add(line.substring(authorStartIndex, authorEndIndex));
				authorStartIndex =authorEndIndex+4;
			}
			if(mat.group().equals(",")) {
				authorEndIndex = mat.start();
				authors.add(line.substring(authorStartIndex, authorEndIndex));
				orgStartIndex = authorEndIndex+1;
			}
			if(mat.group().equalsIgnoreCase("</AUTHOR>")) {
				if (orgStartIndex != 0) {
					orgName = line.substring(orgStartIndex, mat.start());
				}
				else {
					authors.add(line.substring(authorStartIndex,mat.start()));
				}
			}
		}
	if (orgName != null) {
		authors.set(0, "y");		//Setting it as y so that calling function know that the last element is organization name.
		authors.add(orgName);
	}
	return authors;
	}

}
