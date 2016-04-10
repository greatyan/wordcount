package wyan.unicode;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import wyan.unicode.type.Type;

public class TypeIndexTest {

    @Test
    public void testIndex() {
	TypeIndex index = new TypeIndex(Arrays.asList(new Type("low", 'a', 'z'), new Type("upper", 'A', 'Z'), new Type(
		"number", '0', '9')));
	Type t = index.getType("low");
	Assert.assertEquals("low", t.getName());
	Assert.assertEquals(index.getType("OTHER"), index.getType('`'));
	Assert.assertEquals(index.getType("low"), index.getType('a'));
	Assert.assertEquals(index.getType("low"), index.getType('b'));
	Assert.assertEquals(index.getType("low"), index.getType('z'));
	Assert.assertEquals(index.getType("OTHER"), index.getType('{'));

	Assert.assertEquals(index.getType("OTHER"), index.getType('@'));
	Assert.assertEquals(index.getType("upper"), index.getType('A'));
	Assert.assertEquals(index.getType("upper"), index.getType('B'));
	Assert.assertEquals(index.getType("upper"), index.getType('Z'));
	Assert.assertEquals(index.getType("OTHER"), index.getType('['));

	Assert.assertEquals(index.getType("OTHER"), index.getType('/'));
	Assert.assertEquals(index.getType("number"), index.getType('0'));
	Assert.assertEquals(index.getType("number"), index.getType('1'));
	Assert.assertEquals(index.getType("number"), index.getType('9'));
	Assert.assertEquals(index.getType("OTHER"), index.getType(':'));

    }
}
