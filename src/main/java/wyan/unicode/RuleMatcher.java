package wyan.unicode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyan.unicode.rule.Rule;
import wyan.unicode.type.Type;

/**
 * Give a source state and input, search the rules used to test the word break.
 * 
 * The search rules in following order:
 * <ol>
 * <li>SOURCE -&gt; TARGET</li>
 * <li>SOURCE -&gt; ANY</li>
 * <li>ANY -&gt; TARGET</li>
 * <li>ANY -&gt; ANY</li>
 * <li>ALWYAS BREAK</li>
 * </ol>
 * 
 * If one step return multiple rules, it select the rules as:
 * <ol>
 * <li>rules with longest match length.</li>
 * <li>rules without ANY</li>
 * <li>rules with ANY</li>
 * </ol>
 * 
 * TODO: optimize the rule match algorithm
 * 
 * @author wyan
 *
 */
public class RuleMatcher {

    /**
     * type to rules map. source -> target -> rule list
     */
    private Map<Type, Map<Type, List<Rule>>> ruleIndex;
    /**
     * unicode setting of this matcher.
     */
    private Unicode unicode;

    /**
     * create a rule matcher based on the unicode setting.
     * 
     * @param unicode
     *            word break settings.
     */
    public RuleMatcher(Unicode unicode) {
	this.unicode = unicode;
	buildRuleIndex();
    }

    /**
     * build rule index to optimize search algorithm.
     */
    private void buildRuleIndex() {
	ruleIndex = new HashMap<>();
	for (Rule rule : unicode.getRules()) {
	    Type fromType = unicode.getType(rule.getLastSource());
	    Type toType = unicode.getType(rule.getFirstNext());
	    assert fromType != null;
	    assert toType != null;
	    Map<Type, List<Rule>> toIndex = ruleIndex.get(fromType);
	    if (toIndex == null) {
		toIndex = new HashMap<>();
		ruleIndex.put(fromType, toIndex);
	    }
	    List<Rule> toRules = toIndex.get(toType);
	    if (toRules == null) {
		toRules = new ArrayList<>();
		toIndex.put(toType, toRules);
	    }
	    toRules.add(rule);
	}
	// sort all lists according the rule match depth
	ruleIndex.values()
		.forEach(t -> t.values().forEach(rules -> Collections.sort(rules, this::compareRulePriority)));
    }

    /**
     * compare two rules by match depth.
     * 
     * rule has longer match path has high priority. rule with Any has low
     * priority.
     * 
     * @param r1
     *            rule1
     * @param r2
     *            rule2
     * @return -1,0,1 to indicate which one has high priority. -1 means rule1
     *         has high priority.
     */
    private int compareRulePriority(Rule r1, Rule r2) {
	int v = -(r1.getSources().length + r1.getNexts().length - (r2.getSources().length + r2.getNexts().length));
	if (v == 0) {
	    if (Type.ANY.getName().equals(r1.getTarget())) {
		return 1;
	    } else if (Type.ANY.getName().equals(r2.getTarget())) {
		return 1;
	    }
	    return 0;
	}
	return v;
    }

    /**
     * get rules define in FROM state with TO input.
     * 
     * @param from
     *            source state
     * @param to
     *            next input
     * @return rule list
     */
    private List<Rule> getRules(Type from, Type to) {
	Map<Type, List<Rule>> toRules = ruleIndex.get(from);
	if (toRules != null) {
	    List<Rule> rules = toRules.get(to);
	    if (rules != null) {
		return rules;
	    }
	}
	return null;
    }

    /**
     * match result. it contains: 1. read size 2. target type. 3. matched rule.
     * 
     * @author wyan
     *
     */
    public static class MatchResult {
	public int size;
	public Type type;
	public Rule rule;

	public MatchResult(Rule rule, int size, Type type) {
	    this.rule = rule;
	    this.size = size;
	    this.type = type;
	}
    }

    /**
     * find the rule used to break word at index of text.
     * 
     * @param text
     *            input text
     * @param index
     *            next input
     * @param from
     *            source state
     * @return result if find any rules. should never be null.
     */
    public MatchResult match(String text, int index, Type from) {
	int nextCp = -1;
	int nextSize = 0;
	Type nextType = Type.EOT;

	if (index < text.length()) {
	    nextCp = Character.codePointAt(text, index);
	    nextSize = Character.charCount(nextCp);
	    nextType = unicode.getType(nextCp);
	}

	// 1. match with from -> nextType
	List<Rule> rules = getRules(from, nextType);
	if (rules != null) {
	    if (rules.size() == 1) {
		return new MatchResult(rules.get(0), nextSize, nextType);
	    }
	    RuleMatcher.MatchResult result = match(rules, text, index);
	    if (result != null) {
		return result;
	    }
	}
	// 2. match with from -> Any
	rules = getRules(from, Type.ANY);
	if (rules != null) {
	    if (rules.size() == 1) {
		return new MatchResult(rules.get(0), nextSize, nextType);
	    }
	    RuleMatcher.MatchResult result = match(rules, text, index);
	    if (result != null) {
		return result;
	    }
	}
	// 3. Any -> nextType
	rules = getRules(Type.ANY, nextType);
	if (rules != null) {
	    if (rules.size() == 1) {
		return new MatchResult(rules.get(0), nextSize, nextType);
	    }

	    RuleMatcher.MatchResult result = match(rules, text, index);
	    if (result != null) {
		return result;
	    }
	}
	// 4. Any -> Any
	rules = getRules(Type.ANY, Type.ANY);
	if (rules != null) {
	    if (rules.size() == 1) {
		return new MatchResult(rules.get(0), nextSize, nextType);
	    }

	    RuleMatcher.MatchResult result = match(rules, text, index);
	    if (result != null) {
		return result;
	    }
	}
	// 5. No rule, break directly
	return new MatchResult(null, nextSize, nextType);
    }

    /**
     * find match rule by test if input is same with its expect pattern.
     * 
     * @param rules
     * @param text
     * @param index
     * @return
     */
    private MatchResult match(List<Rule> rules, String text, int index) {
	for (Rule rule : rules) {
	    int prevSize = matchPrev(rule.getSources(), text, index);
	    int nextSize = matchNext(rule.getNexts(), text, index);
	    if (prevSize != -1 && nextSize != -1) {
		return new MatchResult(rule, nextSize, unicode.getType(rule.getNexts()[rule.getNexts().length - 1]));
	    }
	}
	return null;
    }

    /**
     * match characters before input with the pattern.
     * 
     * @param types
     *            pattern
     * @param text
     *            text
     * @param index
     *            input
     * @return match size, -1 not match.
     */
    private int matchPrev(String[] types, String text, int index) {
	int start = index;
	for (int i = types.length - 1; i >= 0; i--) {
	    if (index <= 0) {
		return -1;
	    }
	    int cp = Character.codePointBefore(text, index);
	    if (unicode.getType(cp) != unicode.getType(types[i])) {
		return -1;
	    }
	    index -= Character.charCount(cp);
	}
	return start - index;
    }

    /**
     * match characters after input with pattern.
     * 
     * @param types
     *            pattern
     * @param text
     *            inut text
     * @param index
     *            input index
     * @return match size, -1 not match.
     */
    private int matchNext(String[] types, String text, int index) {
	int start = index;
	for (int i = 0; i < types.length; i++) {
	    if (index >= text.length()) {
		return -1;
	    }
	    int cp = Character.codePointAt(text, index);
	    if (unicode.getType(cp) != unicode.getType(types[i])) {
		return -1;
	    }
	    index += Character.charCount(cp);
	}
	return index - start;
    }
}
