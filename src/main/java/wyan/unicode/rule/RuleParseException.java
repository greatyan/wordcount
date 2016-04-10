
package wyan.unicode.rule;

import java.io.IOException;

/**
 * exception to indicate there is a error during rule parsering.
 * 
 * @author wyan
 *
 */
public class RuleParseException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 8603891987494003466L;

    public RuleParseException(String msg) {
	super(msg);
    }
}
