package wyan.unicode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyan.unicode.type.Type;

/**
 * a utility to quick search character type related information.
 * 
 * HashMap to search type from name.
 * 
 * Binary search used to search type from codepoint.
 * 
 * @author wyan
 */
public class TypeIndex {

    private static class TypeRange {
	Type type;
	int start;
	int end;

	TypeRange(Type type, int start, int end) {
	    this.type = type;
	    this.start = start;
	    this.end = end;
	}
    }

    /**
     * map name to character type.
     */
    private Map<String, Type> typeMap;
    /**
     * code point range sorted by code point.
     */
    private TypeRange[] ranges;
    /**
     * sorted start code point of each range.
     */
    private int[] codePoints;

    /**
     * create index from types.
     * 
     * default types are added to the index automatically.
     * 
     * @param types
     *            list of types.
     */
    public TypeIndex(List<Type> types) {
	// create name search cache
	typeMap = new HashMap<>();
	types.forEach(t -> typeMap.put(t.getName(), t));
	typeMap.put(Type.ANY.getName(), Type.ANY);
	typeMap.put(Type.OTHER.getName(), Type.OTHER);
	typeMap.put(Type.SOT.getName(), Type.SOT);
	typeMap.put(Type.EOT.getName(), Type.EOT);
	// create range search cache, expand all code points to a array
	// and create a binary search table
	ranges = types.stream().flatMap(t -> t.getCodePoints().stream().map(cp -> new TypeRange(t, cp[0], cp[1])))
		.toArray(TypeRange[]::new);
	Arrays.sort(ranges, (r1, r2) -> r1.start - r2.start);
	codePoints = Arrays.stream(ranges).mapToInt(t -> t.start).toArray();
    }

    /**
     * return type of the name
     * 
     * @param name
     *            type name
     * @return type of the name, null if not found
     */
    public Type getType(String name) {
	return typeMap.get(name);
    }

    /**
     * all types defined.
     * 
     * @return map from string to type.
     */
    public Map<String, Type> getTypes() {
	return typeMap;
    }

    /**
     * character type of the codepoint.
     * 
     * @param cp
     *            code point defined in unicode spec.
     * @return type of the code point. OTHER is returned if the code point is
     *         not defined in type list.
     */
    public Type getType(int cp) {
	int index = Arrays.binarySearch(codePoints, cp);
	if (index < 0) {
	    // insert point, cp < codePoint[insertPoint]
	    index = (-index - 1);
	    // test previous range
	    index--;
	    if (index < 0) {
		return Type.OTHER;
	    }
	    if (ranges[index].end >= cp) {
		return ranges[index].type;
	    }
	    return Type.OTHER;
	}
	return ranges[index].type;
    }
}
