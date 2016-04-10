package wyan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import wyan.unicode.IWordNormalizer;
import wyan.unicode.WordBreaker;
import wyan.unicode.WordNormalizer;
import wyan.util.Heap;

/**
 * count the word in a document and output highest n words
 *
 * @see <a href="http://unicode.org/reports/tr29/#Word_Boundaries">UNICODE WORD
 *      BOUNDARIES</a>
 */
public class WordCounter {

    private WordBreaker wb;
    private IWordNormalizer wn;

    /**
     * create word counter with default word breaker and normalizer.
     */
    public WordCounter() {
	this(WordBreaker.getDefaultInstance(), new WordNormalizer());
    }

    /**
     * create a word counter with customized word breaker and normalizer.
     * 
     * @param wb
     *            word breaker,
     * @param wn
     *            word normalizer, optional may be null
     */
    public WordCounter(WordBreaker wb, IWordNormalizer wn) {
	this.wb = wb;
	this.wn = wn;
    }

    /**
     * return n words with highest occurrences in document.
     * 
     * @param text
     *            text to be process.
     * @param n
     *            number of word returned.
     * @return words with highest n occurrences.
     */
    public String[] count(String text, int n) {
	if (n <= 0) {
	    throw new IllegalArgumentException("n must be greater than 0");
	}
	if (text == null || text.length() == 0) {
	    return new String[] {};
	}
	HashMap<String, WordCount> counts = new HashMap<>();
	Iterator<String> iter = wb.iterator(text);
	while (iter.hasNext()) {
	    String w = iter.next();
	    if (wn != null)
		w = wn.normalize(w);
	    if (w != null) {
		WordCount c = counts.get(w);
		if (c == null) {
		    c = new WordCount(w);
		    counts.put(w, c);
		}
		c.count++;
	    }
	}
	return count(counts.values(), n);
    }

    static private class WordCount {
	String word;
	int count;

	public WordCount(String word) {
	    this.word = word;
	    this.count = 0;
	}
    }

    /**
     * use heap to return highest N words
     * 
     * @param counts
     *            word count entries
     * @param n
     *            number of entries to return
     * @return words with highest occurrences.
     */
    private String[] count(Collection<WordCount> counts, int n) {
	Heap<WordCount> heap = new Heap<>(n, (c1, c2) -> c1.count - c2.count);
	counts.forEach(c -> heap.offer(c));
	ArrayList<String> words = new ArrayList<>();
	WordCount wc = null;
	while ((wc = heap.poll()) != null) {
	    words.add(wc.word);
	}
	return words.toArray(new String[words.size()]);
    }

    /**
     * main application to read a input file and return top N words.
     * 
     * It accepts two arguments [FILE NAME] [COUNT]
     * 
     * @param args
     *            arguments of the application
     * @throws IOException
     *             file can not be read
     */
    public static void main(String[] args) throws IOException {
	if (args == null || args.length != 2) {
	    System.out.println("WordCounter <file_name|URL> <word_count>");
	    System.exit(-1);
	}

	String text = null;
	try {
	    URL url = new URL(args[0]);
	    text = readURL(url);
	} catch (IOException ex) {
	    text = readFile(args[0]);
	}
	int count = Integer.parseInt(args[1]);
	String[] words = new WordCounter().count(text, count);
	for (int i = 0; i < words.length; i++) {
	    String word = words[i];
	    System.out.println(i + "\t:" + word);
	}
	System.exit(0);
    }

    /**
     * load UTF-8 encoded file into memory
     * 
     * @param fileName
     *            file to be loaded
     * @return text in the file name.
     * @throws IOException
     */
    private static String readFile(String fileName) throws IOException {
	RandomAccessFile rf = new RandomAccessFile(fileName, "r");
	try {
	    long length = rf.length();
	    if (length > Integer.MAX_VALUE) {
		throw new IOException(fileName + " is too huge to read into memory");
	    }
	    byte[] contents = new byte[(int) length];
	    rf.readFully(contents);
	    return new String(contents, "utf-8");
	} finally {
	    rf.close();
	}
    }

    private static String readURL(URL url) throws IOException {

	InputStream in = url.openStream();
	try {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096];
	    int size = in.read(buffer);
	    while (size > 0) {
		out.write(buffer, 0, size);
		size = in.read(buffer);
	    }
	    return new String(out.toByteArray(), "utf-8");
	} finally {
	    in.close();
	}
    }

}
