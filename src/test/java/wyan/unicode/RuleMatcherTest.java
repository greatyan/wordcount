package wyan.unicode;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import wyan.unicode.rule.Rule;
import wyan.unicode.rule.Rule.Event;
import wyan.unicode.type.Type;

public class RuleMatcherTest {

    
    //@formatter:off 
    /**
     * test rule match with different length
     */
    @Test
    public void testMatch() {

	new Runnable() {
	    Unicode unicode = new Unicode(Arrays.asList(
			new Type("UPPER_CASE", 'A', 'Z'), 
			new Type("LOWER_CASE", 'a', 'z'),
			new Type("QUOTE", '\'', '\''),
			new Type( "NUMBER", '0', '9')), 
		Arrays.asList(
			new Rule("LEFT_QUOTE", new String[] { "QUOTE", "UPPER_CASE" },new String[] { "UPPER_CASE" }, "LOWER_CASE", Event.NOBREAK), 
			new Rule("RIGHT_QUOTE", new String[] { "UPPER_CASE" },new String[] { "UPPER_CASE", "QUOTE" }, "QUOTE", Event.NOBREAK), 
			new Rule("UPPER_CASE", new String[] { "UPPER_CASE" },new String[] { "UPPER_CASE" }, "UPPER_CASE", Event.NOBREAK), 
			new Rule("LOWER_CASE", new String[] { "LOWER_CASE" }, new String[] { "LOWER_CASE" }, "LOWER_CASE", Event.NOBREAK), 
			new Rule("NUMBER", new String[] { "NUMBER" },new String[] { "NUMBER" }, "NUMBER", Event.NOBREAK), 
			new Rule("OTHER", new String[] { "ANY" }, new String[] { "ANY" }, "ANY", Event.BREAK)));
	    String text = "'ABCD'abc123";
	    RuleMatcher match = new RuleMatcher(unicode);

	    public void run() {
		assertRule("OTHER", "SOT", 0); //SOT->'
		assertRule("OTHER", "QUOTE", 1); // '->A
		assertRule("LEFT_QUOTE", "UPPER_CASE", 2); //'A->B
		assertRule("UPPER_CASE", "UPPER_CASE", 3); //B->C
		assertRule("RIGHT_QUOTE", "UPPER_CASE", 4); //C->D'
		assertRule("OTHER", "UPPER_CASE", 5); //D->'
		assertRule("OTHER", "QUOTE", 6); //'->a
		assertRule("LOWER_CASE", "LOWER_CASE", 7); //a->b
		assertRule("LOWER_CASE", "LOWER_CASE", 8); //b->c
		assertRule("OTHER", "LOWER_CASE", 9); //c->1
		assertRule("NUMBER", "NUMBER", 10); //1->2
		assertRule("NUMBER", "NUMBER", 11); //2->3
		assertRule("OTHER", "NUMBER", 12); //3->EOT
	    }

	    private void assertRule(String expect, String from, int index) {
		RuleMatcher.MatchResult r = match.match(text, index, unicode.getType(from));
		Assert.assertEquals(expect, r.rule.getName());
	    }
	}.run();
    }
    
    /**
     * test rule match with any.
     */
    @Test
    public void testMatchAnyOrder() {

	new Runnable() {
	    Unicode unicode = new Unicode(Arrays.asList(new Type("CHAR", 'A', 'Z')), 
		Arrays.asList(
			new Rule("ANY_TO_ANY", new String[] { "ANY"},new String[] { "ANY" }, "CHAR", Event.NOBREAK), 
			new Rule("ANY_TO_CHAR", new String[] { "ANY" },new String[] { "CHAR"}, "CHAR", Event.NOBREAK), 
			new Rule("CHAR_TO_ANY", new String[] { "CHAR" },new String[] { "ANY" }, "CHAR", Event.NOBREAK), 
			new Rule("CHAR_TO_CHAR", new String[] { "CHAR" }, new String[] { "CHAR" }, "CHAR", Event.NOBREAK) 
			));
	    String text = "AB12B";
	    RuleMatcher match = new RuleMatcher(unicode);

	    public void run() {
		assertRule("ANY_TO_CHAR", "SOT", 0); //SOT->A
		assertRule("CHAR_TO_CHAR", "CHAR", 1); // A->B
		assertRule("CHAR_TO_ANY", "CHAR", 2); //B->1
		assertRule("ANY_TO_ANY", "OTHER", 3); //1->2
		assertRule("ANY_TO_CHAR", "OTHER", 4); //2->B
	    }

	    private void assertRule(String expect, String from, int index) {
		RuleMatcher.MatchResult r = match.match(text, index, unicode.getType(from));
		Assert.assertEquals(expect, r.rule.getName());
	    }
	}.run();
    }
    
   // @formatter:on 
}
