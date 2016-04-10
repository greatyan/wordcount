package wyan.unicode;

import java.util.Collections;
import java.util.Iterator;

import wyan.unicode.rule.Rule;
import wyan.unicode.type.Type;

/**
 * word breaker to break text into words.
 * 
 * It uses a rule based word breaking algorithm to test if there should be a
 * break between two characters. each character defined in the text is returned.
 * 
 * @author wyan
 *
 */
public class WordBreaker {

    /**
     * default word breaker with rules defined in UNICODE spec.
     */
    private static WordBreaker instance;

    /**
     * get a default word breaker defined in UNICODE spec.
     * 
     * @return default word breaker, it is thread safe
     */
    public static synchronized WordBreaker getDefaultInstance() {
	if (instance == null) {
	    instance = new WordBreaker(Unicode.getDefault());
	}
	return instance;
    }

    /**
     * word breaking rules metadata.
     */
    private Unicode unicode;

    /**
     * create a word breaker with user defined rules.
     * 
     * @param uni
     *            word breaking rules.
     */
    public WordBreaker(Unicode uni) {
	this.unicode = uni;
    }

    /**
     * return iterator contains words in the text.
     * 
     * @param text
     *            input text to be breaking.
     * @return iterator of words.
     */
    public Iterator<String> iterator(String text) {
	if (text == null || text.length() == 0) {
	    return Collections.<String> emptyIterator();
	}
	return new WordIterator(text);
    }

    private class WordIterator implements Iterator<String> {

	private RuleExecutor executor;

	private WordIterator(String text) {
	    assert text != null;
	    this.executor = new RuleExecutor(text);
	}

	@Override
	public boolean hasNext() {
	    return executor.getType() != Type.EOT;
	}

	@Override
	public String next() {
	    if (hasNext()) {
		while (executor.getType() != Type.EOT) {
		    if (executor.execute()) {
			return executor.word();
		    }
		}
	    }
	    return null;
	}
    }

    /**
     * a executor to execute the word breaking rules.
     * 
     * @author wyan
     *
     */
    private class RuleExecutor {

	private String input;
	private Type type;
	private int index;

	private String word;
	private int wordStart;

	RuleMatcher matcher;

	RuleExecutor(String input) {
	    this.input = input;
	    this.type = Type.SOT;
	    this.matcher = new RuleMatcher(unicode);
	}

	public Type getType() {
	    return type;
	}

	/**
	 * return word if the execute return true.
	 * 
	 * @return word generated.
	 */
	public String word() {
	    return word;
	}

	/**
	 * read next character and execute the rule.
	 * 
	 * @return true if there is a word break, the caller can get the word
	 *         through <code>word()</code> API. false there is no word
	 *         break.
	 */
	public boolean execute() {
	    RuleMatcher.MatchResult result = matcher.match(input, index, type);
	    return execute(result.rule, result.size, result.type);
	}

	/**
	 * execute breaking rule.
	 * 
	 * @param nextSize
	 * @param nextType
	 * @return
	 */
	private boolean executeBreak(int nextSize, Type nextType) {
	    if (index > wordStart) {
		word = input.substring(wordStart, index);
	    } else {
		word = null;
	    }
	    wordStart = index;
	    index += nextSize;
	    type = nextType;
	    return word != null;
	}

	/**
	 * execute none breaking rule.
	 * 
	 * @param nextSize
	 * @param nextType
	 * @return
	 */
	private boolean executeNoBreak(int nextSize, Type nextType) {
	    index += nextSize;
	    setTargetType(nextType);
	    word = null;
	    return false;
	}

	/**
	 * execute transfer rule.
	 * 
	 * @param nextSize
	 * @param nextType
	 * @return
	 */
	private boolean executeTransfer(int nextSize, Type nextType) {
	    index += nextSize;
	    setTargetType(nextType);
	    word = null;
	    return false;
	}

	/**
	 * execute a rule with nextSize input with type nextType.
	 * 
	 * @param rule
	 *            rule to be executed.
	 * @param nextSize
	 *            input size.
	 * @param nextType
	 *            last character type.
	 * @return true, word genreated, false otherwise.
	 */
	private boolean execute(Rule rule, int nextSize, Type nextType) {
	    if (rule == null) {
		return executeBreak(nextSize, nextType);
	    }
	    switch (rule.getEvent()) {
	    case BREAK:
		return executeBreak(nextSize, nextType);
	    case NOBREAK:
		return executeNoBreak(nextSize, nextType);
	    case TRANS:
		return executeTransfer(nextSize, unicode.getType(rule.getTarget()));
	    default:
		throw new IllegalStateException("unknow event:" + rule.getEvent());
	    }
	}

	private void setTargetType(Type targetType) {
	    if (targetType != Type.ANY) {
		type = targetType;
	    }
	}
    }

}
