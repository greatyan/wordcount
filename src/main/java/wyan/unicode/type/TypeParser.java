package wyan.unicode.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * parse type definition.
 * 
 * The type definition is defined in two formats:
 * 
 * <pre>
 * HHHH       ; TYPE # .. 
 * HHHH..HHHH ; TYPE # ...
 * </pre>
 * 
 * the codepoint are defined in hex format.
 * 
 * @author wyan
 *
 */
public class TypeParser {
    /**
     * Pattern to match the type definition.
     */
    private static Pattern TYPE_DEFN_PATTERN = Pattern
	    .compile("([0-9|A-H]+)(\\.\\.([0-9A-H]+))?\\s+;\\s*(\\S+)\\s*#.*");

    /**
     * parse a type definition stream with UTF-8 encoding.
     * 
     * @param in
     *            input stream with UTF-8 encoding.
     * @return rules defined in the stream.
     * @throws IOException
     *             fail to read the stream or syntax error.
     */
    public List<Type> parse(InputStream in) throws IOException {
	return parse(in, "UTF-8");
    }

    /**
     * parse all rules defined in a stream.
     * 
     * empty lines and lines with '#' prefix are ignored.
     * 
     * @param in
     *            input stream
     * @param encoding
     *            stream encoding.
     * @return rules defined in the stream.
     * @throws IOException
     *             fail to read the stream or syntax error.
     */
    public List<Type> parse(InputStream in, String encoding) throws IOException {
	Map<String, Type> types = new LinkedHashMap<>();
	BufferedReader r = new BufferedReader(new InputStreamReader(in, encoding));
	String line = r.readLine();
	while (line != null) {
	    if (line.length() > 0 && line.charAt(0) != '#') {
		Type t = parse(line);
		Type ot = types.get(t.getName());
		if (ot != null) {
		    ot.addCodePoints(t.getCodePoints());
		} else {
		    types.put(t.getName(), t);
		}
	    }
	    line = r.readLine();
	}
	return new ArrayList<>(types.values());
    }

    /**
     * parse a single type definition.
     * 
     * type is defined as:
     * 
     * <pre>
     * HHHH       ; [NAME] # ...
     * HHHH...HHH ; [NAME] # ...
     * </pre>
     * 
     * @param line
     *            type definition.
     * @return type, null if the line format is incorrect.
     * @throws TypeParseException
     *             wrong definition
     */
    public Type parse(String line) throws TypeParseException {
	assert line != null;
	Matcher match = TYPE_DEFN_PATTERN.matcher(line);
	if (match.matches()) {
	    String start = match.group(1);
	    String end = match.group(3);
	    String type = match.group(4);
	    if (end == null)
		end = start;
	    assert start != null;
	    assert type != null;
	    return new Type(type.toUpperCase(), Integer.parseInt(start, 16), Integer.parseInt(end, 16));
	}
	throw new TypeParseException("invalid type definition:" + line);
    }
}
