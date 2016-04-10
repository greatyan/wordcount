package wyan.unicode.rule;

/**
 * a break rule to define word breaking behavior between two or more characters.
 * 
 * Rule is defined as:
 * 
 * <code>
 * [SOURCE CHAR TYPE] + [EXPECTED CHAR TYPE] =&gt; [TARGET CHAR TYPE] OPERATION
 * </code>
 * 
 * It supports three kinds of operations: 1. break 2. not break 3. state
 * transfer.
 * 
 * @author wyan
 *
 */
public class Rule {

    /**
     * event issued by the rule.
     * 
     * @author wyan
     *
     */
    public static enum Event {
	BREAK, TRANS, NOBREAK
    }

    private String name;
    private String[] sources;
    private String[] nexts;
    private String target;
    private Event event;

    /**
     * define a rule from sources matches next and goto target, raise event.
     * 
     * @param name
     *            rule name
     * @param sources
     *            source types
     * @param nexts
     *            expected types.
     * @param target
     *            final type after executed the rule
     * @param event
     *            action to be executed.
     */
    public Rule(String name, String[] sources, String[] nexts, String target, Event event) {
	this.name = name;
	this.sources = sources;
	this.nexts = nexts;
	this.target = target;
	this.event = event;
    }

    public String getName() {
	return name;
    }

    public String[] getSources() {
	return sources;
    }

    public String getLastSource() {
	return sources[sources.length - 1];
    }

    public String[] getNexts() {
	return nexts;
    }

    public String getFirstNext() {
	return nexts[0];
    }

    public Event getEvent() {
	return event;
    }

    public String getTarget() {
	return target;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(name);
	sb.append(":(");
	sb.append(String.join(",", sources));
	sb.append(")+(");
	sb.append(String.join(",", nexts));
	sb.append(")=>");
	sb.append(target);
	return sb.toString();
    }
}
