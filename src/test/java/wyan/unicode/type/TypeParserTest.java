package wyan.unicode.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TypeParserTest {
    @Test
    public void testParseLine() throws TypeParseException {

	TypeParser p = new TypeParser();
	Type type = p.parse("0000  ; ABC # ....");
	Assert.assertEquals("ABC", type.getName());
	Assert.assertEquals(0, type.getCodePoints().get(0)[0]);
	Assert.assertEquals(0, type.getCodePoints().get(0)[1]);

	type = p.parse("0000..000A  ; ABC # ....");
	Assert.assertEquals("ABC", type.getName());
	Assert.assertEquals(0, type.getCodePoints().get(0)[0]);
	Assert.assertEquals(10, type.getCodePoints().get(0)[1]);
    }

    @Test
    public void testParserStream() throws IOException {

	String typeDefn = "#COMMENT...\n" + "\n" + "0000  ; ABC # ....\n" + "002..003 ; ABC #...";
	TypeParser p = new TypeParser();

	List<Type> types = p.parse(new ByteArrayInputStream(typeDefn.getBytes("UTF-8")));
	Assert.assertEquals(1, types.size());
	Type t = types.get(0);
	Assert.assertEquals("ABC", t.getName());
	Assert.assertEquals(2, t.getCodePoints().size());
	Assert.assertEquals(0, t.getCodePoints().get(0)[0]);
	Assert.assertEquals(0, t.getCodePoints().get(0)[1]);
	Assert.assertEquals(2, t.getCodePoints().get(1)[0]);
	Assert.assertEquals(3, t.getCodePoints().get(1)[1]);
    }
}
