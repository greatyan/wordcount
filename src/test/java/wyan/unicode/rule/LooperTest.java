package wyan.unicode.rule;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LooperTest {

    @Test
    public void testLooper() {
	String[][] seeds = new String[][] { new String[] { "1", "2" }, new String[] { "1", "2", "3" },
		new String[] { "1" } };

	StringBuilder sb = new StringBuilder();
	for (String[] vs : new Looper<String>(seeds)) {
	    sb.append(String.join(",", vs));
	    sb.append(" ");
	}
	Assert.assertEquals("1,1,1 1,2,1 1,3,1 2,1,1 2,2,1 2,3,1 ", sb.toString());
    }

    @Test
    public void testNullLooper() {
	Iterator<String[]> iter = new Looper<String>(null).iterator();
	Assert.assertTrue(iter.hasNext());
	Assert.assertNull(iter.next());
	Assert.assertFalse(iter.hasNext());
    }
}
