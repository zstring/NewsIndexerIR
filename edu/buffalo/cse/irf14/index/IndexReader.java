/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	private String indexDir;
	private IndexType type;
	private BaseIndexer biContent;
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		//TODO
		this.indexDir = indexDir;
		this.type = type;
		indexReaderOpen();
	}
	
	private void indexReaderOpen() {
		// TODO Auto-generated method stub
		FileInputStream fi;
		
		try {
			fi = new FileInputStream(indexDir + java.io.File.separator + "File");
			ObjectInputStream ois = new ObjectInputStream(fi);
			biContent = (BaseIndexer) ois.readObject();
			ois.close();
			fi.close();
			System.out.println("Writing Process Complete");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in Writer File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in Writer IO Exception");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		return -1;
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		return -1;
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		return null;
	}
	
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		return null;
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		//TODO : BONUS ONLY
		return null;
	}
	
	public static void main(String[] args) {
		String indexAddr = "/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/news_training";
		IndexType type = IndexType.CONTENT;
		IndexReader ir = new IndexReader(indexAddr, type);
	}
}
