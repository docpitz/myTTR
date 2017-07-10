package com.jmelzer.myttr.logic;

import org.junit.Before;
import org.junit.Test;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by J. Melzer on 01.08.2015.
 */
public class AbstractBaseParserTest {
    private DummyParser parser;
    final String ASSETS_DIR = "assets/parsertest";

    static class DummyParser extends AbstractBaseParser {

    }

    @Before
    public void setUp() throws Exception {
        parser = new DummyParser();
    }

    @Test
    public void testReadTableRow() throws Exception {
        String page = readFile(ASSETS_DIR + "/readtablerow-test1.txt");

        String[] columns = new DummyParser().tableRowAsArray(page, 10, false);
        assertNotNull(columns);
        assertEquals(10, columns.length);

        assertEquals("", columns[0]);
        assertEquals("1", columns[1]);
        assertEquals("<a alt=\"Mannschaftsportrait\" title=\"Mannschaftsportrait\" href=\"/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/teamPortrait?teamtable=2031834&pageState=vorrunde&championship=Mittelrhein+15%2F16&group=249747\">TSV Seelscheid II</a>", columns[2]);
        assertEquals("0", columns[3]);
        assertEquals("0", columns[4]);
        assertEquals("0", columns[5]);
        assertEquals("0", columns[6]);
        assertEquals("<span title=\"Saetze 0:0 / Baelle 0:0\">\r\n" +
                "                0:0\r\n" +
                "              </span>", columns[7]);
        assertEquals("0", columns[8]);
        assertEquals("0:0", columns[9]);
    }

    @Test
    public void listFromLiElement() {
        String html = "<ul>\n" +
                "                  <li>\n" +
                "  \n" +
                "  \n" +
                "\t<label style=\"width:300px;\">Tischmarke:</label>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t  \n" +
                "    \tJoola Rollomat, Andro Roller und Butterfly Centrefold\n" +
                "    \t\n" +
                "\t    \t\n" +
                "    \t\n" +
                "      \n" +
                "    \n" +
                "  \n" +
                "  \n" +
                "\n" +
                "\n" +
                "\n" +
                "</li>\n" +
                "                  <li>\n" +
                "  \n" +
                "  \n" +
                "\t<label style=\"width:300px;\">Tischfarbe:</label>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t  \n" +
                "    \tgrün\n" +
                "    \t\n" +
                "\t    \t\n" +
                "    \t\n" +
                "      \n" +
                "    \n" +
                "  \n" +
                "  \n" +
                "\n" +
                "\n" +
                "\n" +
                "</li>\n" +
                "                  <li>\n" +
                "  \n" +
                "  \n" +
                "\t<label style=\"width:300px;\">Ballmarke:</label>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t  \n" +
                "    \tDHS\n" +
                "    \t\n" +
                "\t    \t\n" +
                "    \t\n" +
                "      \n" +
                "    \n" +
                "  \n" +
                "  \n" +
                "\n" +
                "\n" +
                "\n" +
                "</li>\n" +
                "                  <li>\n" +
                "  \n" +
                "  \n" +
                "\t<label style=\"width:300px;\">Ballfarbe:</label>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t  \n" +
                "    \tZelluloid weiß\n" +
                "    \t\n" +
                "\t    \t\n" +
                "    \t\n" +
                "      \n" +
                "    \n" +
                "  \n" +
                "  \n" +
                "\n" +
                "\n" +
                "\n" +
                "</li>\n" +
                "                </ul>";

        String s = parser.listFromLiElement(new AbstractBaseParser.ParseResult(html, 0));
        System.out.println("s = " + s);
    }
}