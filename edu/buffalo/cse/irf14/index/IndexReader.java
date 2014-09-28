/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {

	private String indexDir;
	private IndexType type;
	private BaseIndexer bi;
	private Integer[] indexKeys;
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
		double start = System.currentTimeMillis();
		String fileName = "";
		try {
			if (IndexType.AUTHOR.equals(type)) {
				fileName = "AUTHOR";
			}
			else if (IndexType.CATEGORY.equals(type)) {
				fileName = "CATEGORY";
			}
			else if (IndexType.PLACE.equals(type)) {
				fileName = "PLACE";
			}
			else if (IndexType.TERM.equals(type)) {
				fileName = "TERM";
			}
			fi = new FileInputStream(indexDir + java.io.File.separator + fileName);
			GZIPInputStream gzipIn = new GZIPInputStream(fi);
			ObjectInputStream ois = new ObjectInputStream(gzipIn);
			//			fi = new FileInputStream(indexDir + java.io.File.separator + "File");
			//			ObjectInputStream ois = new ObjectInputStream(fi);
			bi = (BaseIndexer) ois.readObject();
			indexKeys = (Integer[]) ois.readObject();
			ois.close();
			fi.close();
			System.out.println("Reading Process Complete");
			double timeSpent = System.currentTimeMillis() - start;
			System.out.println("Time spent in Reading : " + timeSpent);
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
		if (bi != null && bi.termMap != null)
			return bi.termMap.keySet().size();
		return -1;
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		//return bi.t
		if (bi != null)
			return bi.getDocNum();
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
		Map<String, Integer> map = null;
		if (bi != null && bi.termDict != null) {
			Integer termId = bi.termDict.get(term);
			if (termId != null) {
				map = new HashMap<String, Integer>(bi.termMap.get(termId).getPosting());
				//			map = new HashMap<String, Integer>();
				//			Map<Integer, Posting> m = bi.termIndex.get(termId);
				//			Map<Integer, String> doc = bi.docDict;
				//			if (m != null) {
				//				for (Iterator<Integer> i = m.keySet().iterator(); i.hasNext();) {
				//					Integer in = i.next();
				//					String docName = doc.get(in);
				//					int termFreq = m.get(in).getTermFreq();
				//					map.put(docName, termFreq);
				//				}
				//			}
			}
		}
		return map;
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
		List<String> topKTerms = null;
		if (bi != null && bi.termMap != null) {
			int len = indexKeys.length;
			if (k > 0)
				topKTerms = new ArrayList<String>();
			for (int i = 0; i < k && i < len; i++) {
				String termText = bi.termMap.get(indexKeys[i]).getTermText();
				topKTerms.add(termText);
			}
		}
		return topKTerms;
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
		Map<String, Integer> output = null;
		if (terms != null) {
			output = getPostings(terms[0]);
			for (int i = 1; i < terms.length; i++) {
				Map<String, Integer> m = getPostings(terms[i]);
				output.keySet().retainAll(m.keySet());
				for (Iterator<String> it = output.keySet().iterator(); it.hasNext();) {
					String in = it.next();
					Integer freq = output.get(in) + m.get(in);
					output.put(in, freq);
				}
			}
			if (output.isEmpty()) return null;
		}
		return output;
	}

	public static void main(String[] args) {
		String indexAddr = "/home/inspire/Dropbox/UB/JavaWorkspace/newsindexer/news_training";
		IndexType type = IndexType.TERM;
		IndexReader ir = new IndexReader(indexAddr, type);
		System.out.println(ir.type);
	}
}
