package wyan.unicode.rule;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import wyan.unicode.rule.Rule.Event;

public class RuleParserTest {

    @Test
    public void testParser() throws Exception {
	RuleParser p = new RuleParser();
	List<Rule> rules = p.parse("WB1	sot	÷	");
	Assert.assertEquals(1, rules.size());
	Rule rule = rules.get(0);
	Assert.assertEquals("WB1", rule.getName());
	Assert.assertEquals(new String[] { "SOT" }, rule.getSources());
	Assert.assertEquals(new String[] { "ANY" }, rule.getNexts());
	Assert.assertEquals("ANY", rule.getTarget());
	Assert.assertEquals(Event.BREAK, rule.getEvent());

	rules = p.parse("WB2	 	÷	eot");
	Assert.assertEquals(1, rules.size());
	rule = rules.get(0);
	Assert.assertEquals("WB2", rule.getName());
	Assert.assertEquals(new String[] { "ANY" }, rule.getSources());
	Assert.assertEquals(new String[] { "EOT" }, rule.getNexts());
	Assert.assertEquals("EOT", rule.getTarget());
	Assert.assertEquals(Event.BREAK, rule.getEvent());

	rules = p.parse("WB4	X (Extend | Format)	→	X");
	Assert.assertEquals(2, rules.size());
	rule = rules.get(0);
	Assert.assertEquals("WB4", rule.getName());
	Assert.assertEquals(new String[] { "X" }, rule.getSources());
	Assert.assertEquals(new String[] { "EXTEND" }, rule.getNexts());
	Assert.assertEquals("X", rule.getTarget());
	Assert.assertEquals(Event.TRANS, rule.getEvent());
	rule = rules.get(1);
	Assert.assertEquals("WB4", rule.getName());
	Assert.assertEquals(new String[] { "X" }, rule.getSources());
	Assert.assertEquals(new String[] { "FORMAT" }, rule.getNexts());
	Assert.assertEquals("X", rule.getTarget());
	Assert.assertEquals(Event.TRANS, rule.getEvent());

	rules = p.parse("XXX	ALetter ALetter	×	ALetter ALetter");
	Assert.assertEquals(1, rules.size());
	rule = rules.get(0);
	Assert.assertEquals("XXX", rule.getName());
	Assert.assertEquals(new String[] { "ALETTER", "ALETTER" }, rule.getSources());
	Assert.assertEquals(new String[] { "ALETTER", "ALETTER" }, rule.getNexts());
	Assert.assertEquals("ALETTER", rule.getTarget());
	Assert.assertEquals(Event.NOBREAK, rule.getEvent());
    }
}
