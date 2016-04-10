package wyan.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HeapTest {

    @Test
    public void testHeapSize1() {
	Heap<String> heap = new Heap<String>(1);
	// offer 1 element return true
	Assert.assertTrue(heap.offer("3"));
	// offer small return false
	Assert.assertFalse(heap.offer("1"));
	// offer big return true
	Assert.assertTrue(heap.offer("4"));
	// contains only one elements
	Assert.assertEquals("4", heap.peek());
	Assert.assertEquals("4", heap.poll());
	Assert.assertNull(heap.poll());
    }

    @Test
    public void testHeapSize2() {

	Heap<String> heap = new Heap<String>(2);
	// offer 2 elements return true
	Assert.assertTrue(heap.offer("5"));
	Assert.assertTrue(heap.offer("4"));
	// offer big element return true
	Assert.assertTrue(heap.offer("6"));
	// offer small return false
	Assert.assertFalse(heap.offer("0"));
	// pool two biggest elements
	Assert.assertEquals("6", heap.peek());
	Assert.assertEquals("6", heap.poll());
	Assert.assertEquals("5", heap.peek());
	Assert.assertEquals("5", heap.poll());
	Assert.assertNull(heap.poll());
    }

    @Test
    public void testHeapSize7() {
	Heap<String> heap = new Heap<String>(7);
	// offer 7 element return true
	Assert.assertTrue(heap.offer("5"));
	Assert.assertTrue(heap.offer("4"));
	Assert.assertTrue(heap.offer("6"));
	Assert.assertTrue(heap.offer("7"));
	Assert.assertTrue(heap.offer("8"));
	Assert.assertTrue(heap.offer("3"));
	Assert.assertTrue(heap.offer("2"));
	// offer a small string return false
	Assert.assertFalse(heap.offer("0"));
	// offer a big string return true
	Assert.assertTrue(heap.offer("9"));
	// contains 7 string in order
	Assert.assertEquals("9", heap.peek());
	Assert.assertEquals("9", heap.poll());
	Assert.assertEquals("8", heap.peek());
	Assert.assertEquals("8", heap.poll());
	Assert.assertEquals("7", heap.peek());
	Assert.assertEquals("7", heap.poll());
	Assert.assertEquals("6", heap.peek());
	Assert.assertEquals("6", heap.poll());
	Assert.assertEquals("5", heap.peek());
	Assert.assertEquals("5", heap.poll());
	Assert.assertEquals("4", heap.peek());
	Assert.assertEquals("4", heap.poll());
	Assert.assertEquals("3", heap.peek());
	Assert.assertEquals("3", heap.poll());
	Assert.assertNull(heap.poll());
    }

    @Test
    public void testRandom() {
	String[] values = Stream.generate(() -> String.valueOf(Math.random())).limit(200).toArray(String[]::new);
	Heap<String> heap = new Heap<String>(100);
	Stream.of(values).forEach(s -> heap.offer(s));
	Arrays.sort(values);
	for (int i = values.length - 1; i >= 100; i--) {
	    Assert.assertEquals(values[i], heap.poll());
	}
	Assert.assertNull(heap.poll());
    }

    @Test
    public void testIterator() {
	Heap<String> heap = new Heap<String>(50);
	for (int i = 0; i < 100; i++) {
	    heap.offer(Integer.toString(i));
	}
	Iterator<String> iter = heap.iterator();
	int size = 0;
	while (iter.hasNext()) {
	    size++;
	    String v = iter.next();
	    Assert.assertTrue(v.compareTo("50") > 0);
	}
	Assert.assertTrue(size == 50);
    }
}
