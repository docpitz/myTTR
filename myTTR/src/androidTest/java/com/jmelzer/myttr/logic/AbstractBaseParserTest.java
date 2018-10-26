package com.jmelzer.myttr.logic;


import androidx.test.filters.SmallTest;

/**
 * Created by J. Melzer on 19.02.2015.
 */
public class AbstractBaseParserTest extends BaseTestCase {
    @SmallTest
    public void testreadBetweenOpenTag() {
        String s = "<td class=\"tabelle-rowspan\"> dummy </td>";
        AbstractBaseParser.ParseResult result = new AbstractBaseParser().readBetweenOpenTag(s, 0, "<td", "</td>");
        assertNotNull(result);
        assertEquals("dummy", result.result);
        assertEquals(s.length(), result.end);
    }
}
