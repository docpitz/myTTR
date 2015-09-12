package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by J. Melzer on 22.04.2015.
 * class for unit test the MyTischtennisParser class
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class MyTischtennisParserTest {
    MyTischtennisParser parser;

    final String ASSETS_DIR = "assets";

    @Before
    public void setUp() throws Exception {
        parser = new MyTischtennisParser();
    }

    @Test
    public void parseLastnameFromBadName() throws Exception {
        assertEquals("Michel", parser.parseLastNameFromBadName("Michel, Dennis'"));
    }
    @Test
    public void testParseEventsWithPlayerId() throws Exception {
        String page = readFile(ASSETS_DIR + "/events_425165.htm");
        assertNotNull(page);
        Player player = parser.parseEvents(page, false);
        assertEquals(1893, player.getTtrPoints());
        assertEquals("Dennis", player.getFirstname());
        assertEquals("Michel", player.getLastname());
        assertNotNull(player);
        assertTrue(player.getEvents().size() > 20);
    }

    @Test
    public void testParseOwnEvents() throws Exception {
        String page = readFile(ASSETS_DIR + "/events.htm");
        assertNotNull(page);
        Player player = parser.parseEvents(page, true);
        assertEquals(1645, player.getTtrPoints());
        assertEquals("Jürgen Melzer (1645)", player.getFullName());
        assertNotNull(player);
        assertTrue(player.getEvents().size() > 20);
    }

    @Test
     public void testParseOwnEvents201506() throws Exception {
        String page = readFile(ASSETS_DIR + "/myHistory_2015_06.htm");
        assertNotNull(page);
        Player player = parser.parseEvents(page, false);
        assertEquals(1662, player.getTtrPoints());
        assertEquals("Jürgen Melzer (1662)", player.getFullName());
        assertNotNull(player);
        assertTrue(player.getEvents().size() > 20);
    }

    @Test
    public void testFindPlaySSF() throws Exception {
        String page = readFile(ASSETS_DIR + "/ssf-mannschaft.htm");
        assertNotNull(page);
        List<Player> list = new ArrayList<>();
        List<Player> player = parser.parseForPlayer(null, null,page,list, 0);
        assertEquals(6, player.size());
    }

    @Test
    public void testFindPlayerUmlaut() throws Exception {
        String page = readFile(ASSETS_DIR + "/search_koehler.htm");
        assertNotNull(page);
        List<Player> list = new ArrayList<>();
        List<Player> players = parser.parseForPlayer("Stefan ", "Köhler", page, list, 0);
        assertEquals(6, players.size());
        assertEquals("Stefan", players.get(1).getFirstname());
        assertEquals("Köhler", players.get(1).getLastname());
        assertEquals("SC 1904 Nürnberg e.V.", players.get(1).getClub());
    }
}
