package wyan.util;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * A heap based on TreeMap.
 * 
 * The heap has maximum size, if the offered number exceed the limit, the
 * minimum value will be removed and the capacity is unchanged.
 * 
 * The heap uses a <code>TreeMap&lt;T,List&lt;T&gt;&gt;</code> to save internal
 * values.
 * 
 * The complexity of all operation are log(n)
 * <ol>
 * <li>peek() : log(n)</li>
 * <li>poll() : log(n)</li>
 * <li>offer() : log(n)</li>
 * </ol>
 *
 * @param <T>
 *            values saved in the heap
 */
public class Heap<T> extends AbstractQueue<T> {

    private long modifyCount;
    private TreeMap<T, LinkedList<T>> valueTree;
    private int maxSize;
    private int valueSize;
    private Comparator<T> comparator;

    /**
     * create a heap with max size using default comparator.
     * 
     * @param maxSize
     *            max size of the heap.
     */
    public Heap(int maxSize) {
	this(maxSize, new DefaultComparator<T>());
    }

    /**
     * create a heap with max size
     * 
     * @param maxSize
     *            max size of the heap.
     * @param c
     *            comparator
     */
    public Heap(int maxSize, Comparator<T> c) {
	assert maxSize >= 1;
	assert c != null;
	this.maxSize = maxSize;
	this.valueSize = 0;
	this.comparator = c;
	this.valueTree = new TreeMap<>(c);
    }

    private static class DefaultComparator<T> implements Comparator<T> {

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(T o1, T o2) {
	    return ((Comparable) o1).compareTo((Comparable) o2);
	}
    }

    @Override
    public boolean offer(T e) {
	if (e == null) {
	    throw new NullPointerException();
	}
	if (valueSize == maxSize) {
	    if (comparator.compare(valueTree.firstKey(), e) >= 0) {
		return false;
	    }
	}
	addValue(e);
	if (valueSize > maxSize) {
	    T v = removeMinValue();
	    if (v == e) {
		return false;
	    }
	}
	return true;
    }

    /**
     * add a value to heap.
     * 
     * @param t
     *            value to be added.
     */
    private void addValue(T t) {
	modifyCount++;
	LinkedList<T> values = valueTree.get(t);
	if (values == null) {
	    values = new LinkedList<>();
	    valueTree.put(t, values);
	}
	values.add(t);
	valueSize++;
    }

    /**
     * remove the minimum value in the heap
     * 
     * @return minimum value in the heap.
     */
    private T removeMinValue() {
	modifyCount++;
	Map.Entry<T, LinkedList<T>> entry = valueTree.firstEntry();
	if (entry.getValue().size() == 1) {
	    valueTree.remove(entry.getKey());
	}
	valueSize--;
	return entry.getValue().removeLast();
    }

    /**
     * remove the max value from the heap.
     * 
     * @return maximum value in the heap.
     */
    private T removeMaxValue() {
	modifyCount++;
	Map.Entry<T, LinkedList<T>> entry = valueTree.lastEntry();
	if (entry.getValue().size() == 1) {
	    valueTree.remove(entry.getKey());
	}
	valueSize--;
	return entry.getValue().removeFirst();
    }

    @Override
    public T poll() {
	if (valueSize == 0) {
	    return null;
	}
	return removeMaxValue();
    }

    @Override
    public T peek() {
	if (valueSize == 0) {
	    return null;
	}
	return valueTree.lastEntry().getValue().getFirst();
    }

    @Override
    public Iterator<T> iterator() {
	if (this.valueSize == 0) {
	    return new Iterator<T>() {

		@Override
		public boolean hasNext() {
		    return false;
		}

		@Override
		public T next() {
		    throw new NoSuchElementException();
		}
	    };
	}
	return new Iterator<T>() {
	    long modifyId = Heap.this.modifyCount;
	    Iterator<Entry<T, LinkedList<T>>> keyIter = valueTree.entrySet().iterator();
	    Iterator<T> valueIter = keyIter.next().getValue().iterator();

	    @Override
	    public boolean hasNext() {
		if (!valueIter.hasNext()) {
		    if (!keyIter.hasNext()) {
			return false;
		    }
		    valueIter = keyIter.next().getValue().iterator();
		}
		return valueIter.hasNext();
	    }

	    @Override
	    public T next() {
		if (modifyId != modifyCount) {
		    throw new ConcurrentModificationException();
		}
		return valueIter.next();
	    }
	};
    }

    @Override
    public int size() {
	return valueSize;
    }

}
