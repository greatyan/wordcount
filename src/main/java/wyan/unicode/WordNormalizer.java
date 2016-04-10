package wyan.unicode;

/**
 * a special normalizer works with default word breaker to ignore empty/none
 * meaningful text.
 * 
 * @author wyan
 *
 */
public class WordNormalizer implements IWordNormalizer {

    public String normalize(String word) {
	if (word == null || word.length() == 0) {
	    return null;
	}
	switch (Character.getType(word.charAt(0))) {
	case Character.COMBINING_SPACING_MARK:
	case Character.CONNECTOR_PUNCTUATION:
	case Character.CONTROL:
	case Character.CURRENCY_SYMBOL:
	case Character.DASH_PUNCTUATION:
	    // Character.DECIMAL_DIGIT_NUMBER:
	case Character.ENCLOSING_MARK:
	case Character.END_PUNCTUATION:
	case Character.FINAL_QUOTE_PUNCTUATION:
	case Character.FORMAT:
	case Character.INITIAL_QUOTE_PUNCTUATION:
	    // Character.LETTER_NUMBER:
	case Character.LINE_SEPARATOR:
	    // Character.LOWERCASE_LETTER:
	case Character.MATH_SYMBOL:
	    // Character.MODIFIER_LETTER:
	case Character.MODIFIER_SYMBOL:
	case Character.NON_SPACING_MARK:
	    // Character.OTHER_LETTER:
	    // Character.OTHER_NUMBER:
	case Character.OTHER_PUNCTUATION:
	case Character.OTHER_SYMBOL:
	case Character.PARAGRAPH_SEPARATOR:
	    // Character.PRIVATE_USE:
	case Character.SPACE_SEPARATOR:
	case Character.START_PUNCTUATION:
	    // Character.SURROGATE:
	    // Character.TITLECASE_LETTER:
	    // Character.UNASSIGNED:
	    // Character.UPPERCASE_LETTER:
	    return null;
	}
	return word;
    }
}
