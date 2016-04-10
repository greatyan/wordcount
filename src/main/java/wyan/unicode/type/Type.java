package wyan.unicode.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * character types
 * 
 * @author wyan
 *
 */
public class Type {

    /** type of start of text */
    public static final Type SOT = new Type("SOT");
    /** type of end of text */
    public static final Type EOT = new Type("EOT");
    /** type of any character */
    public static final Type ANY = new Type("ANY");
    /** type of other characters not define in spec */
    public static final Type OTHER = new Type("OTHER");

    /** type name */
    private String name;
    /** type code point ranges */
    private List<int[]> codePoints;

    /** define a type without any codepoint */
    private Type(String name) {
	this.name = name;
	this.codePoints = new ArrayList<>(0);
    }

    /**
     * define a type with codepoint range [start,end] the range is inclusive.
     * 
     * @param name
     *            type name
     * @param start
     *            start codepoint.
     * @param end
     *            end codepoint
     */
    public Type(String name, int start, int end) {
	this(name);
	this.codePoints.add(new int[] { start, end });
    }

    /**
     * type name
     * 
     * @return type name
     */
    public String getName() {
	return this.name;
    }

    /**
     * code points of the type.
     * 
     * @return codepoints defined in this type.
     */
    public List<int[]> getCodePoints() {
	return Collections.unmodifiableList(codePoints);
    }

    /**
     * add more code point to the type.
     * 
     * @param cps
     *            code points added to the type.
     */
    void addCodePoints(List<int[]> cps) {
	codePoints.addAll(cps);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(name);
	if (codePoints != null && !codePoints.isEmpty()) {
	    sb.append("(");
	    codePoints.forEach(cp -> {
		if (cp[0] == cp[1]) {
		    sb.append(Integer.toHexString(cp[0]));
		} else {
		    sb.append(Integer.toHexString(cp[0]));
		    sb.append("..");
		    sb.append(Integer.toHexString(cp[1]));
		}
		sb.append(",");
	    });
	    sb.setCharAt(sb.length() - 1, ')');
	}
	return sb.toString();
    }
}
