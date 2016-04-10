package wyan.unicode;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import wyan.unicode.type.Type;

public class UnicodeTest {

    @Test
    public void testGetTypeByCP() throws IOException {
	Unicode u = Unicode.getDefault();

	Assert.assertEquals(u.getType("NEWLINE"), u.getType(0xB));
	Assert.assertEquals(u.getType("CR"), u.getType(0xD));
	Assert.assertEquals(u.getType("DOUBLE_QUOTE"), u.getType(0x22));
    }

    @Test
    public void testGetTypeByName() throws IOException {
	Unicode u = Unicode.getDefault();
	// test default type are added
	Assert.assertEquals(Type.SOT, u.getType("SOT"));
	Assert.assertEquals(Type.ANY, u.getType("ANY"));
	Assert.assertEquals(Type.EOT, u.getType("EOT"));
	Assert.assertEquals(Type.OTHER, u.getType("OTHER"));
	// test some type loaded from property file
	Assert.assertNotNull(u.getType("HEBREW_LETTER"));
    }
}
