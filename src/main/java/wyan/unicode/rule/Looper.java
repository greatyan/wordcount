package wyan.unicode.rule;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * a utility to create combinations.
 * 
 * [A, B, C] * [a,b] * [1,2] will create a combination of 3 * 2 * 2 as: [A,a,1]
 * [A,a,2], [A,b,1], [A,b,2] ... [C,b,2]
 * 
 * @author wyan
 *
 * @param <T>
 *            type of the value
 */
public class Looper<T> implements Iterable<T[]> {

    private T[][] values;

    public Looper(T[][] values) {
	assert values == null || values[0][0] != null;
	this.values = values;
    }

    @Override
    public Iterator<T[]> iterator() {
	if (values == null) {
	    return new Iterator<T[]>() {

		boolean accessed = false;

		@Override
		public boolean hasNext() {
		    return !accessed;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T[] next() {
		    if (hasNext()) {
			accessed = true;
			return null;
		    }
		    throw new NoSuchElementException();
		}
	    };
	}

	return new Iterator<T[]>() {
	    private int[] index = new int[values.length];

	    @Override
	    public boolean hasNext() {
		if (index[0] < values[0].length) {
		    return true;
		}
		return false;
	    }

	    @Override
	    public T[] next() {
		assert hasNext();
		@SuppressWarnings("unchecked")
		T[] v = (T[]) Array.newInstance(values[0][0].getClass(), values.length);
		for (int i = 0; i < values.length; i++) {
		    v[i] = values[i][index[i]];
		}
		// increase index
		for (int i = index.length - 1; i >= 0; i--) {
		    index[i]++;
		    if (index[i] < values[i].length) {
			break;
		    }
		    if (i != 0) {
			index[i] = 0;
		    }
		}
		return v;
	    }
	};
    }
}
