package wyan.unicode.rule;

/**
 * token stream used to parse word break rule definition.
 * 
 * @author wyan
 *
 */
public class TokenStream {

    /**
     * token types
     * 
     * @author wyan
     *
     */
    public static enum TYPE {
	LEFT_BRAKET("("), RIGHT_BRAKET(")"), OR("|"), NAME("<NAME>"), BOUNDARY("÷"), NOT_BOUNDARY("×"), TRANSFORM("→"), UNKNOWN(
		"<unknown>");

	private String value;

	private TYPE(String value) {
	    this.value = value;
	}

	public String getValue() {
	    return value;
	}
    }

    /**
     * token
     * 
     * @author wyan
     *
     */
    public static class Token {
	public TYPE type;
	public String value;

	public Token(TYPE type) {
	    this.type = type;
	    this.value = type.getValue();
	}

	public Token(TYPE type, String value) {
	    this.type = type;
	    this.value = value;
	}
    }

    /**
     * current index
     */
    private int index;
    /**
     * text input
     */
    private String input;
    /**
     * next token
     */
    private Token nextToken;

    /**
     * create a token stream from text
     * 
     * @param input
     *            input text, can not be null
     */
    TokenStream(String input) {
	assert input != null && input.length() > 0;
	this.input = input;
	this.index = 0;
    }

    /**
     * has next token
     * 
     * @return true the stream is not end, false the stream has no more token.
     */
    public boolean hasNext() {
	if (nextToken == null) {
	    nextToken = getNextToken();
	}
	return nextToken != null;
    }

    /**
     * get next token.
     * 
     * @return next token, null if the stream is empty or end of stream.
     */
    public Token next() {
	if (hasNext()) {
	    Token returnToken = nextToken;
	    nextToken = null;
	    return returnToken;
	}
	return null;
    }

    /**
     * scan the stream to return the next token.
     * 
     * @return next token, null if it is empty or end of stream.
     */
    private Token getNextToken() {
	if (index >= input.length()) {
	    return null;
	}
	// skip whitespace
	readWhitespace();
	if (index >= input.length()) {
	    return null;
	}
	char ch = input.charAt(index);
	index++;
	switch (ch) {
	case '|':
	    return new Token(TYPE.OR);
	case '÷':
	    return new Token(TYPE.BOUNDARY);
	case '×':
	    return new Token(TYPE.NOT_BOUNDARY);
	case '→':
	    return new Token(TYPE.TRANSFORM);
	case '(':
	    return new Token(TYPE.LEFT_BRAKET);
	case ')':
	    return new Token(TYPE.RIGHT_BRAKET);
	default:
	    String name = readName(input, index - 1);
	    index += name.length() - 1;
	    return new Token(TYPE.NAME, name);
	}
    }

    private void readWhitespace() {
	while (index < input.length()) {
	    if (!Character.isWhitespace(input.charAt(index))) {
		break;
	    }
	    index++;
	}
    }

    private String readName(String input, int start) {
	int index = start;
	while (index < input.length()) {
	    char ch = input.charAt(index);
	    if (ch == '|' || ch == '÷' || ch == '×' || ch == '*' || ch == '→' || ch == '(' || ch == ')'
		    || Character.isWhitespace(ch)) {
		break;
	    }
	    index++;
	}
	return input.substring(start, index);
    }
}
