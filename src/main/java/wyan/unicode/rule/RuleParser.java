package wyan.unicode.rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyan.unicode.rule.Rule.Event;
import wyan.unicode.rule.TokenStream.TYPE;
import wyan.unicode.rule.TokenStream.Token;

/**
 * parser of word breaking rule. a rule is defined as:
 * 
 * [NAME] [TYPE]* [OPERATOR] [TYPE]*
 * 
 * 
 * @author wyan
 *
 */
public class RuleParser {

    /**
     * predefined type macro. Marco is a alias name of one or multiple types.
     */
    private Map<String, String[]> typeMacro = new HashMap<>();

    /**
     * create default parser.
     */
    public RuleParser() {
	this(Collections.<String, String[]> emptyMap());
    }

    /**
     * create parse with predefined macros.
     * 
     * @param macros
     *            predefined type macros.
     */
    public RuleParser(Map<String, String[]> macros) {
	typeMacro.putAll(macros);
    }

    /**
     * parse a input stream with UTF-8 encoding.
     * 
     * empty and lines with '#' prefix are ignored.
     * 
     * 
     * @param in
     *            input stream encoding with UTF-8
     * @return rules defined in the stream.
     * @throws IOException
     *             failed to read stream or syntax error.
     */
    public List<Rule> parse(InputStream in) throws IOException {
	return parse(in, "UTF-8");
    }

    /**
     * parse a rule definition stream with defined encoding.
     * 
     * @param in
     *            input stream.
     * @param encoding
     *            stream encoding.
     * @return rules defined in the stream.
     * @throws IOException
     *             failed to read stream or syntax error.
     */
    public List<Rule> parse(InputStream in, String encoding) throws IOException {
	List<Rule> rules = new ArrayList<>();
	BufferedReader r = new BufferedReader(new InputStreamReader(in, encoding));
	String line = r.readLine();
	while (line != null) {
	    if (line.length() > 0 && line.charAt(0) != '#') {
		rules.addAll(parse(line));
	    }
	    line = r.readLine();
	}
	return rules;
    }

    /**
     * parse a rule from one line.
     * 
     * @param line
     *            text contains rule definition.
     * @return rule defined in the text
     * @throws RuleParseException
     *             syntax error.
     */
    public List<Rule> parse(String line) throws RuleParseException {
	LookAheadTokenStream s = new LookAheadTokenStream(new TokenStream(line));
	String name = readName(s);
	checkEof(s);
	String[][] sources = null;
	String[][] targets = null;
	Token t = s.peek();
	if (t.type == TYPE.NAME || t.type == TYPE.LEFT_BRAKET)
	    sources = readStates(s);
	Event operator = readOperator(s);
	if (s.hasNext()) {
	    targets = readStates(s);
	}
	List<Rule> rules = new ArrayList<>();
	for (String[] source : new Looper<String>(sources)) {
	    for (String[] next : new Looper<String>(targets)) {
		rules.add(buildRule(name, source, next, operator));
	    }
	}
	return rules;
    }

    private Rule buildRule(String name, String[] source, String[] next, Event event) {

	if (event == Event.TRANS) {
	    assert source.length > 1;
	    assert next.length == 1;
	    return new Rule(name, new String[] { source[0] }, Arrays.copyOfRange(source, 1, source.length), next[0],
		    event);
	}
	if (source == null || source.length == 0) {
	    source = new String[] { "ANY" };
	}
	if (next == null || next.length == 0) {
	    next = new String[] { "ANY" };
	}
	return new Rule(name, source, next, next[0], event);
    }

    private void checkEof(LookAheadTokenStream s) throws RuleParseException {
	if (!s.hasNext()) {
	    throw new RuleParseException("unexpected eof");
	}
    }

    private String[][] readStates(LookAheadTokenStream s) throws RuleParseException {
	List<String[]> states = new ArrayList<>();
	checkEof(s);
	Token t = s.peek();
	while (t.type == TYPE.NAME || t.type == TYPE.LEFT_BRAKET) {
	    states.add(readTypes(s));
	    if (!s.hasNext()) {
		break;
	    }
	    t = s.peek();
	}
	return states.toArray(new String[states.size()][]);
    }

    private Event readOperator(LookAheadTokenStream s) throws RuleParseException {
	checkEof(s);
	Token t = s.next();
	if (t.type == TYPE.BOUNDARY) {
	    return Event.BREAK;
	} else if (t.type == TYPE.NOT_BOUNDARY) {
	    return Event.NOBREAK;
	} else if (t.type == TYPE.TRANSFORM) {
	    return Event.TRANS;
	}
	throw new RuleParseException("unexpected token:" + t.value);
    }

    private String[] readTypes(LookAheadTokenStream s) throws RuleParseException {
	checkEof(s);
	Token t = s.peek();
	if (t.type == TYPE.NAME) {
	    return readType(s);
	} else if (t.type == TYPE.LEFT_BRAKET) {
	    List<String> types = new ArrayList<>();
	    // skip left_braket
	    s.next();
	    while (true) {
		checkEof(s);
		types.addAll(Arrays.asList(readType(s)));
		checkEof(s);
		t = s.next();
		if (t.type == TYPE.OR) {
		    continue;
		}
		if (t.type == TYPE.RIGHT_BRAKET) {
		    return types.toArray(new String[types.size()]);
		}
		throw new RuleParseException("unexpected token:" + t.value);
	    }
	}
	throw new RuleParseException("unexpected token:" + t.value);
    }

    private String readName(LookAheadTokenStream s) throws RuleParseException {
	checkEof(s);
	Token t = s.next();
	if (t.type == TYPE.NAME) {
	    return t.value;
	}
	throw new RuleParseException("unexpected token:" + t.value);
    }

    private String[] readType(LookAheadTokenStream s) throws RuleParseException {
	checkEof(s);
	Token t = s.next();
	if (t.type == TYPE.NAME) {
	    return decodeTypeMacro(t.value);
	}
	throw new RuleParseException("unexpected token:" + t.value);
    }

    private String[] decodeTypeMacro(String type) {
	type = type.toUpperCase();
	String[] types = typeMacro.get(type);
	if (types != null) {
	    return types;
	}
	return new String[] { type };
    }

    private static class LookAheadTokenStream {

	TokenStream stream;
	Token token;

	LookAheadTokenStream(TokenStream stream) {
	    this.stream = stream;
	}

	public boolean hasNext() {
	    if (token != null) {
		return true;
	    }
	    return stream.hasNext();
	}

	public Token next() {
	    if (token != null) {
		Token r = token;
		token = null;
		return r;
	    }
	    return stream.next();
	}

	public Token peek() {
	    if (token == null) {
		if (stream.hasNext()) {
		    token = stream.next();
		}
	    }
	    return token;
	}
    }

}
