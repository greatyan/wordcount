package wyan.unicode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import wyan.unicode.rule.Rule;
import wyan.unicode.rule.Rule.Event;
import wyan.unicode.type.Type;

public class WordBreakerTest {

    @Test
    public void testWordBreaker() {

	Unicode unicode = defineUnicode();
	WordBreaker wb = new WordBreaker(unicode);
	Assert.assertEquals(new String[] { "123", "ABC", "abc" }, splitWords(wb, "123ABCabc"));
    }

    @Test
    public void testUnicodeWB() {
	WordBreaker wb = WordBreaker.getDefaultInstance();
	// #Do not break within CRLF.
	// WB3 CR × LF
	Assert.assertEquals(new String[] { "\r\n", "\r\n" }, splitWords(wb, "\r\n\r\n"));
	// #Otherwise break before and after Newlines (including CR and LF)
	// WB3a (Newline | CR | LF) ÷
	Assert.assertEquals(new String[] { "\r", "a" }, splitWords(wb, "\ra"));
	Assert.assertEquals(new String[] { "\n", "a" }, splitWords(wb, "\na"));
	Assert.assertEquals(new String[] { "\u2028", "a" }, splitWords(wb, "\u2028a"));
	// WB3b ÷ (Newline | CR | LF)
	Assert.assertEquals(new String[] { "a", "\r" }, splitWords(wb, "a\r"));
	Assert.assertEquals(new String[] { "a", "\n" }, splitWords(wb, "a\n"));
	Assert.assertEquals(new String[] { "a", "\u2028" }, splitWords(wb, "a\u2028"));
	// #Ignore Format and Extend characters, except when they appear at the
	// beginning of a region of text.
	// #(See Section 6.2, Replacing Ignore Rules.)
	// WB4 X (Extend | Format) → X
	Assert.assertEquals(new String[] { "a\u00AD\u00ADb" }, splitWords(wb, "a\u00AD\u00ADb"));
	Assert.assertEquals(new String[] { "a\u0300\u0030b" }, splitWords(wb, "a\u0300\u0030b"));
	Assert.assertEquals(new String[] { "a\u0300\u00ADb" }, splitWords(wb, "a\u0300\u00ADb"));

	// #Do not break between most letters.
	// WB5 AHLetter × AHLetter
	Assert.assertEquals(new String[] { "a\u05D0a\u05D0" }, splitWords(wb, "a\u05D0a\u05D0"));

	// #Do not break letters across certain punctuation.
	// WB6 AHLetter × (MidLetter | MidNumLetQ) AHLetter
	Assert.assertEquals(new String[] { "a:b" }, splitWords(wb, "a:b"));
	Assert.assertEquals(new String[] { "a\u002Eb" }, splitWords(wb, "a\u002Eb"));
	Assert.assertEquals(new String[] { "\u05d0:\u05d0" }, splitWords(wb, "\u05d0:\u05d0"));
	Assert.assertEquals(new String[] { "\u05d0\u002E\u05d0" }, splitWords(wb, "\u05d0\u002E\u05d0"));

	// WB7 AHLetter (MidLetter | MidNumLetQ) × AHLetter
	Assert.assertEquals(new String[] { "a:b" }, splitWords(wb, "a:b"));
	// WB7a Hebrew_Letter × Single_Quote
	Assert.assertEquals(new String[] { "\u05d0'a" }, splitWords(wb, "\u05d0'a"));
	// WB7b Hebrew_Letter × Double_Quote Hebrew_Letter
	// WB7c Hebrew_Letter Double_Quote × Hebrew_Letter
	Assert.assertEquals(new String[] { "\u05d0\"\u05d0" }, splitWords(wb, "\u05d0\"\u05d0"));
	// #Do not break within sequences of digits, or digits adjacent to
	// letters (“3a”, or “A3”).
	// WB8 Numeric × Numeric
	Assert.assertEquals(new String[] { "123" }, splitWords(wb, "123"));
	// WB9 AHLetter × Numeric
	Assert.assertEquals(new String[] { "\u05D0123" }, splitWords(wb, "\u05D0123"));
	// WB10 Numeric × AHLetter
	Assert.assertEquals(new String[] { "1a" }, splitWords(wb, "1a"));
	// #Do not break within sequences, such as “3.2” or “3,456.789”.
	// WB11 Numeric (MidNum | MidNumLetQ) × Numeric
	// WB12 Numeric × (MidNum | MidNumLetQ) Numeric
	Assert.assertEquals(new String[] { "1.2" }, splitWords(wb, "1.2"));
	Assert.assertEquals(new String[] { "1,3.2" }, splitWords(wb, "1,3.2"));
	// #Do not break between Katakana.
	// WB13 Katakana × Katakana
	Assert.assertEquals(new String[] { "\u30A0\u30A0" }, splitWords(wb, "\u30A0\u30A0"));
	// #Do not break from extenders.
	// WB13a (AHLetter | Numeric | Katakana | ExtendNumLet) × ExtendNumLet
	Assert.assertEquals(new String[] { "a\u005F" }, splitWords(wb, "a\u005F"));
	Assert.assertEquals(new String[] { "1\u005F" }, splitWords(wb, "1\u005F"));
	Assert.assertEquals(new String[] { "\u30A0\u005F" }, splitWords(wb, "\u30A0\u005F"));
	Assert.assertEquals(new String[] { "\u005F\u005F" }, splitWords(wb, "\u005F\u005F"));
	// WB13b ExtendNumLet × (AHLetter | Numeric | Katakana)
	Assert.assertEquals(new String[] { "\u005Fa" }, splitWords(wb, "\u005Fa"));
	Assert.assertEquals(new String[] { "\u005F1" }, splitWords(wb, "\u005F1"));
	Assert.assertEquals(new String[] { "\u005F\u30A0" }, splitWords(wb, "\u005F\u30A0"));
	Assert.assertEquals(new String[] { "\u005F\u005F" }, splitWords(wb, "\u005F\u005F"));
	// #Do not break between regional indicator symbols.
	// WB13c Regional_Indicator × Regional_Indicator
	Assert.assertEquals(new String[] { "\uD83C\uDDE6\uD83C\uDDE6" },
		splitWords(wb, "\uD83C\uDDE6\uD83C\uDDE6")); // 1F1E6
	// #Otherwise, break everywhere (including around ideographs).
	// WB14 Any ÷ Any */
	Assert.assertEquals(new String[] { "A", "\uD83C\uDDE6", "A" }, splitWords(wb, "A\uD83C\uDDE6A"));
	Assert.assertEquals(new String[] { "$", "123", " ", "CDE" }, splitWords(wb, "$123 CDE"));
	Assert.assertEquals(new String[] { "中", "文" }, splitWords(wb, "中文"));
    }

    private String[] splitWords(WordBreaker wb, String text) {
	ArrayList<String> words = new ArrayList<>();
	Iterator<String> iter = wb.iterator(text);
	while (iter.hasNext()) {
	    words.add(iter.next());
	}
	return words.toArray(new String[words.size()]);
    }

    // @formatter:off
    private Unicode defineUnicode() {

	return new Unicode(Arrays.asList(new Type("UPPER_CASE", 'A', 'Z'), new Type("LOWER_CASE", 'a', 'z'), new Type(
		"NUMBER", '0', '9')), Arrays.asList(new Rule("UPPER_CASE", new String[] { "UPPER_CASE" },
		new String[] { "UPPER_CASE" }, "UPPER_CASE", Event.NOBREAK), new Rule("LOW_CASE",
		new String[] { "LOWER_CASE" }, new String[] { "LOWER_CASE" }, "LOWER_CASE", Event.NOBREAK), new Rule(
		"NUMBER", new String[] { "NUMBER" }, new String[] { "NUMBER" }, "NUMBER", Event.NOBREAK), new Rule(
		"OTHER", new String[] { "ANY" }, new String[] { "ANY" }, "ANY", Event.BREAK)));
    }
    // @formatter:off
}
