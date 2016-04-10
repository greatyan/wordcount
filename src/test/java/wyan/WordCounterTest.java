package wyan;

import org.testng.Assert;
import org.testng.annotations.Test;

public class WordCounterTest {

    @Test
    public void testWordCount() {
	WordCounter t = new WordCounter();
	Assert.assertEquals(new String[] {}, t.count(null, 3));
	Assert.assertEquals(new String[] {}, t.count("", 3));
	Assert.assertEquals(new String[] { "ABC", "123", "A" }, t.count("123 123 123 ABC ABC ABC ABC A A 2 2", 3));
    }
}
