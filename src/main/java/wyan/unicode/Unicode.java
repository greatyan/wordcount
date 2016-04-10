package wyan.unicode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyan.unicode.rule.Rule;
import wyan.unicode.rule.RuleParser;
import wyan.unicode.type.Type;
import wyan.unicode.type.TypeParser;

/**
 * unicode word break metadata.
 * 
 * see http://unicode.org/reports/tr29/#Word_Boundaries
 * 
 * @author wyan
 *
 */
public class Unicode {

    /**
     * word break rules.
     */
    private List<Rule> rules;
    /**
     * utility to find character type related information.
     */
    private TypeIndex typeIndex;

    /**
     * create a unicode with user defined types and rules.
     * 
     * @param types
     *            character types.
     * @param rules
     *            word breaking rules.
     */
    public Unicode(List<Type> types, List<Rule> rules) {
	this.rules = rules;
	this.typeIndex = new TypeIndex(types);
    }

    /**
     * get the type of a code point
     * 
     * @param codepoint
     *            code point to be tested.
     * @return type of the code point
     */
    public Type getType(int codepoint) {
	return typeIndex.getType(codepoint);
    }

    /**
     * get character type of name.
     * 
     * @param name
     *            type name.
     * @return character type.
     */
    public Type getType(String name) {
	return typeIndex.getType(name);
    }

    /**
     * return all rules defined in the metadata.
     * 
     * @return read only list of the rules.
     */
    public List<Rule> getRules() {
	return Collections.unmodifiableList(rules);
    }

    /**
     * default metadata defined in unicode spec.
     */
    private static Unicode _default;

    /**
     * get word breaking meta data defined in unicode spec.
     * 
     * @return the metadata defined in unicode specificaiton.
     */
    public static Unicode getDefault() {
	if (_default == null) {
	    try {
		List<Type> types = loadDefaultTypes();
		List<Rule> rules = loadDefaultRules();
		_default = new Unicode(types, rules);
	    } catch (IOException ex) {
		throw new RuntimeException("Failed to load default metadata", ex);
	    }
	}
	return _default;
    }

    /**
     * load unicode defined word breaking rules.
     * 
     * @return rules defined in unicode spec.
     * @throws IOException
     */
    private static List<Rule> loadDefaultRules() throws IOException {
	Map<String, String[]> macros = new HashMap<>();
	macros.put("AHLETTER", new String[] { "ALETTER", "HEBREW_LETTER" });
	macros.put("MIDNUMLETQ", new String[] { "MIDNUMLET", "SINGLE_QUOTE" });
	macros.put("X", new String[] { "ANY" });
	InputStream in = Unicode.class.getResourceAsStream("WordBoundaryRule.txt");
	try {

	    RuleParser p = new RuleParser(macros);
	    return p.parse(in);
	} finally {
	    in.close();
	}
    }

    /**
     * load unicode defined character types.
     * 
     * @return types defined in unicode spec.
     * @throws IOException
     */
    private static List<Type> loadDefaultTypes() throws IOException {
	InputStream in = Unicode.class.getResourceAsStream("WordBreakProperty.txt");
	try {
	    return new TypeParser().parse(in);
	} finally {
	    in.close();
	}
    }

    /**
     * return all types defined in the meta
     * 
     * @return a read only map contains the types.
     */
    public Map<String, Type> getTypes() {
	return Collections.unmodifiableMap(typeIndex.getTypes());
    }
}
