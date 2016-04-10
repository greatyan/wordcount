package wyan.unicode.type;

import java.io.IOException;

/**
 * exception if something wrong with character type definition.
 * 
 * @author wyan
 *
 */
public class TypeParseException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = -4405475488704086830L;

    TypeParseException(String message) {
	super(message);
    }

}
