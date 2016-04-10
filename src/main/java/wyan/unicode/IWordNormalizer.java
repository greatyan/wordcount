package wyan.unicode;

/**
 * a interface to normalize word.
 * 
 * a possible mapping may be: 
 * <ul>
 * <li>done -&gt; do</li>
 * <li>doing -&gt; do</li>
 * <li>" " -&gt; null</li>
 * <li>"\r\n" -&gt; null</li>
 * </ul>
 * 
 * @author wyan
 *
 */
public interface IWordNormalizer {

    /**
     * normalize a word.
     * 
     * @param text
     *            word to be normalized
     * @return a normalized word, null if the word should be ignore.
     */
    public String normalize(String text);
}
