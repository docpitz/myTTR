package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyTTLiga;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.SpielerAndBilanz;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.model.Head2HeadResult;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by J. Melzer on 22.04.2015.
 * class for unit test the MyTischtennisParser class
 */
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
    public void parseOtherTeam() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/other-team.htm");
        Mannschaft m = new Mannschaft();

        Mannschaft mannschaft = parser.parseOtherTeam(m, page);
        assertNotNull(mannschaft);
        assertEquals(18, mannschaft.getSpiele().size());
        assertEquals(9, mannschaft.getFutureAppointments().size());
        for (Mannschaftspiel mannschaftspiel : mannschaft.getSpiele()) {
            System.out.println("mannschaftspiel.getDate() = " + mannschaftspiel.getDate());
        }

        for (TeamAppointment appointment : mannschaft.getFutureAppointments()) {
            assertNotNull(appointment.getId1());
            assertNotNull(appointment.getId2());
        }

        assertNotNull(mannschaft.getLiga());
        assertNotNull(mannschaft.getLiga().getGroupId());
    }

    @Test
    public void parseTTRIndexPage() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/ttrechner-index.htm");
        List<Mannschaft> mannschaften = parser.parseTTRIndexPage(page);
        assertNotNull(mannschaften);
        assertEquals(22, mannschaften.size());
        for (Mannschaft mannschaft : mannschaften) {
            assertNotNull(mannschaft.toString(), mannschaft.getVereinId());
            assertNotNull(mannschaft.toString(), mannschaft.getName());
            System.out.println("id, name = " + mannschaft.getVereinId() + ", " + mannschaft.getName());
        }

    }

    @Test
    public void readOwnTeam() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/team.html");
        Mannschaft mannschaft = parser.parseOwnTeam(page);
        assertNotNull(mannschaft);
        assertEquals(13, mannschaft.getSpielerBilanzen().size());
        for (SpielerAndBilanz spielerAndBilanz : mannschaft.getSpielerBilanzen()) {
            System.out.println("spielerAndBilanz = " + spielerAndBilanz);
        }
        assertEquals(0, mannschaft.getSpiele().size());
        assertEquals("TTG St. Augustin III", mannschaft.getName());
//        for (Mannschaftspiel ms : mannschaft.getSpiele()) {
//            System.out.println(ms.getDate() + "  " + ms.getHeimMannschaft().getName() +
//                    "  " + ms.getGastMannschaft().getName() +  " " + ms.getErgebnis());
//            System.out.println("mannschaftspiel = " + ms);
//        }
    }

    @Test
    public void readOwnTeamEmptyTable() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/teamEmptyTable.htm");
        Mannschaft mannschaft = parser.parseOwnTeam(page);
        assertNotNull(mannschaft);
        assertEquals(0, mannschaft.getSpielerBilanzen().size());
    }

    @Test
    public void testParseEventsWithPlayerId() throws Exception {
        String page = readFile(ASSETS_DIR + "/events_425165.htm");
        assertNotNull(page);
        Player player = parser.parseEvents(page, false);
        assertEquals(1913, player.getTtrPoints());
        assertEquals("Dennis", player.getFirstname());
        assertEquals("Michel", player.getLastname());
        assertNotNull(player);
        assertTrue(player.getEvents().size() > 20);
    }

    @Test
    public void testParseDetail() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/eventDetails.htm");
        assertNotNull(page);
        EventDetail detail = parser.parseDetail(page);
        assertThat(detail.getGames().size(), is(1));
        assertThat(detail.getGames().get(0).getPlayer(), is("Berger, Patrick"));
    }

    @Test
    public void testParseOwnEvents() throws Exception {
        String page = readFile(ASSETS_DIR + "/events.htm");
        assertNotNull(page);
        Player player = parser.parseEvents(page, false);
        assertEquals(1703, player.getTtrPoints());
        assertEquals("Jürgen Melzer (1703)", player.getFullName());
        assertNotNull(player);
        assertTrue(player.getEvents().size() > 20);
    }

    @Test
    public void testParseHead2Head() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/head2head.htm");
        assertNotNull(page);
        List<Head2HeadResult> head2HeadResult = parser.parseHead2Head(page);
        assertNotNull(head2HeadResult);
        assertThat(head2HeadResult.size(), is(4));
        for (Head2HeadResult result : head2HeadResult) {
            System.out.println("result = " + result);
        }
    }

    @Test
    @Ignore
    public void testFindPlaySSF() throws Exception {
        String page = readFile(ASSETS_DIR + "/ssf-mannschaft.htm");
        assertNotNull(page);
        List<Player> list = new ArrayList<>();
        List<Player> player = parser.parseForPlayer(null, null, page, list, 0);
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

    @Test
    public void testDirk() throws Exception {
        String page = readFile(ASSETS_DIR + "/parsertest/rankingListDirk.htm");
        assertNotNull(page);
        List<Player> list = new ArrayList<>();
        List<Player> players = parser.parseForPlayer("", "", page, list, 0);
        assertEquals(59, players.size());

        for (Player player : players) {
            Assert.assertNotNull(player.toString(), player.getPersonId());
            Assert.assertNotNull(player.toString(), player.getClub());
            Assert.assertNotNull(player.toString(), player.getLastname());
            Assert.assertNotNull(player.toString(), player.getLastname());
//            System.out.println("player = " + player);
        }
    }

    @Test
    public void testFindPlayerBug57() throws Exception {
        String page = readFile(ASSETS_DIR + "/parsertest/rankingList.htm");
        assertNotNull(page);
        List<Player> list = new ArrayList<>();
        List<Player> players = parser.parseForPlayer("", "", page, list, 0);
        assertEquals(99, players.size());

        for (Player player : players) {
            Assert.assertNotNull(player.toString(), player.getPersonId());
            Assert.assertNotNull(player.toString(), player.getClub());
            Assert.assertNotNull(player.toString(), player.getLastname());
            Assert.assertNotNull(player.toString(), player.getLastname());
//            System.out.println("player = " + player);
        }
        assertEquals("Marco", players.get(1).getFirstname());
        assertEquals("Vester", players.get(1).getLastname());
        assertEquals("TTG St. Augustin", players.get(1).getClub());
    }

    @Test
    public void testparseGroupForRanking() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/group.htm");
        assertEquals(0, parser.parseGroupForRanking("jsfsflsdkf").size());

        assertEquals(1, parser.parseGroupForRanking(page).size());
        assertEquals("249747", parser.parseGroupForRanking(page).get(0));
    }

    @Test
    public void testreadClubName() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/userMasterPage.htm");
        assertEquals("TTF Bad Honnef", parser.readClubName(page));

        page = readFile(ASSETS_DIR + "/mytt/userMasterPageTWahl.htm");
        assertEquals("SV Sandkamp", parser.readClubName(page));
    }

    @Test
    public void testparseMultipleGroupForRanking() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/group-multiple.htm");

        List<String> list = parser.parseGroupForRanking(page);
        assertEquals(2, list.size());
        assertEquals("251421", list.get(0));
        assertEquals("250870", list.get(1));
    }

    @Test
    public void testparseGroupRanking() throws Exception {
        String page = readFile(ASSETS_DIR + "/mytt/group-ranking.htm");
        MyTTLiga myTTLiga = parser.parseGroupRanking(page);
        assertNotNull(myTTLiga);
        assertEquals("Herren-Bezirksliga 3", myTTLiga.getLigaName());
        List<Player> players = myTTLiga.getRanking();
        assertNotNull(players);
        assertEquals(84, players.size());
        assertEquals(1667, players.get(28).getTtrPoints());
        assertEquals("Jürgen", players.get(28).getFirstname());
        assertEquals("Melzer", players.get(28).getLastname());
        assertEquals("TTG St. Augustin", players.get(28).getClub());
        for (Player player : players) {
            assertTrue(player.toString(), player.getTtrPoints() > 0);
            assertNotNull(player.toString(), player.getClub());
            assertTrue(player.toString(), player.getPersonId() > 0);
            assertNotNull(player.toString(), player.getFirstname());
            assertNotNull(player.toString(), player.getLastname());
        }

    }
}
