package wyan.unicode.rule;

import org.testng.Assert;
import org.testng.annotations.Test;

import wyan.unicode.rule.TokenStream.TYPE;

public class TokenStreamTest {

    @Test
    public void testTokenStream() {
	TokenStream s = new TokenStream("WB11 	Numeric ( MidNum | MidNumLetQ )  ×	 	Numeric");
	Assert.assertEquals(TYPE.NAME, s.next().type);
	Assert.assertEquals(TYPE.NAME, s.next().type);
	Assert.assertEquals(TYPE.LEFT_BRAKET, s.next().type);
	Assert.assertEquals(TYPE.NAME, s.next().type);
	Assert.assertEquals(TYPE.OR, s.next().type);
	Assert.assertEquals(TYPE.NAME, s.next().type);
	Assert.assertEquals(TYPE.RIGHT_BRAKET, s.next().type);
	Assert.assertEquals(TYPE.NOT_BOUNDARY, s.next().type);
	Assert.assertEquals(TYPE.NAME, s.next().type);
	Assert.assertNull(s.next());

	s = new TokenStream("WB4	X (Extend | Format)	→	X");
	Assert.assertEquals("WB4", s.next().value);
	Assert.assertEquals("X", s.next().value);
	Assert.assertEquals("(", s.next().value);
	Assert.assertEquals("Extend", s.next().value);
	Assert.assertEquals("|", s.next().value);
	Assert.assertEquals("Format", s.next().value);
	Assert.assertEquals(")", s.next().value);
	Assert.assertEquals("→", s.next().value);
	Assert.assertEquals("X", s.next().value);
	Assert.assertNull(s.next());
    }
}
