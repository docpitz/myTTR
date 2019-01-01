package com.jmelzer.myttr.logic;

import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.activities.LoginActivity;
import com.jmelzer.myttr.model.SearchPlayer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static com.jmelzer.myttr.logic.LogicTestHelper.login;
import static com.jmelzer.myttr.logic.LogicTestHelper.readFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyTischtennisParserIntegrationTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

    @Before
    public void before() throws Exception {
        login();
    }

    @Test
    public void testgetPoints() throws Exception {

        int myPoints = myTischtennisParser.getPoints();
        assertTrue(myPoints > 1600);
    }

    @Test
    public void testReadEvents() throws Exception {

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEvents().getEvents();
        boolean found = false;
        for (Event event : events) {
            String s = event.toString();
            assertNotNull(s, event.getAk());
            assertNotNull(s, event.getBilanz());
            assertNotNull(s, event.getDate());
            assertNotNull(s, event.getEvent());
            assertTrue(s, event.getTtr() > 0);
            assertTrue(s, event.getSum() > -1000);
//            Log.i(Constants.LOG_TAG, s);
            if (event.getEvent().equals("Bezirksmeisterschaften Mittelrhein Erwachsene - Senioren 40")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testreadGamesForForeignPlayer() throws Exception {

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEventsForForeignPlayer(425165L).getEvents();
        boolean found = false;
        for (Event event : events) {
            if (event.getEvent().equals("RL-Herren | TTC RG Porz : TTC Elz")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testreadDetailGame() throws Exception {

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEvents().getEvents();
        assertTrue(events.size() > 0);
//        Log.i(Constants.LOG_TAG, events.get(0).toString());
        EventDetail eventDetail = myTischtennisParser.readEventDetail(events.get(0));
        assertNotNull(eventDetail);
        Log.i(Constants.LOG_TAG, "eventDetail=" + eventDetail.toString());
        assertTrue(eventDetail.getGames().size() >= 1);

        for (Game g : eventDetail.getGames()) {
            assertNotNull(g);
            assertNotNull(g.getPlayer());
            assertNotNull(g.getPlayerWithPoints());
            assertNotNull(g.getSetsInARow());
            assertFalse(g.getResult().contains("<"));
            assertTrue(g.getSets().size() >= 3);
            Log.i(Constants.LOG_TAG, "Game=" + g.toString());
        }
        assertEquals(2, eventDetail.getGames().size());
    }

//    @Test
//    public void testreadDetailGameError1() throws Exception {
//
//        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
//        String page = readFile("assets/eventDetails_error1.htm");
//        EventDetail eventDetail = myTischtennisParser.parseDetail(page);
//        assertNotNull(eventDetail);
//        assertEquals(6, eventDetail.getGames().size());
//        for (Game g : eventDetail.getGames()) {
//            assertNotNull(g);
//            assertNotNull(g.getPlayer());
//            assertNotNull(g.getPlayerWithPoints());
//            assertNotNull(g.getSetsInARow());
//            Log.i(Constants.LOG_TAG, "Game=" + g.toString());
//        }
//    }

    @Test
    public void testReadBetween() {
        String toTest = "bla bla<td>06.12.2014</td><td style=\"width: 550px;";
        assertEquals("06.12.2014", new MyTischtennisParser().readBetween(toTest, 7, "<td>", "</td>").result);
    }

    @Test
    public void teststripTags() {
        String toTest = "style=\"width: 550px;\"><a href=\"javascript:openmoreinfos(423703061," +
                "'eventdiv1');\" class=\"trigger2\" title=\"Details anzeigen\">BK-Herren | TV Bergheim II : TTG St. " +
                "Augustin II";
        assertEquals("BK-Herren | TV Bergheim II : TTG St. Augustin II", new MyTischtennisParser().stripTags(toTest));
    }

    @Test
    public void testGetClubList() throws Exception {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> clublist = myTischtennisParser.getClubList(true);
        for (Player player : clublist) {
            assertTrue(player.toString(), player.getPersonId() > 0);
            assertNotNull(player.toString(), player.getFirstname());
            assertNotNull(player.toString(), player.getFullName());
            assertTrue(player.toString(), player.getTtrPoints() > 0);
//            Log.i(Constants.LOG_TAG, player.toString());
            assertTrue(player.getPersonId() > 0);
            assertTrue(player.toString(), player.getTtrPoints() > 0);
        }
    }

    @Test
    public void testreadPlayersFromTeam() throws Exception {
        List<Player> players = myTischtennisParser.readPlayersFromTeam("1411599");
        boolean found = false;
        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Werner")) {
                assertFalse("player should be found once " + player, found);
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testparsePlayerFromTeam() throws Exception {
        String rueckrunde = readFile("assets/mannschaft-rueckrunde.html");
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.parsePlayerFromTeam(rueckrunde);
        boolean found = false;
        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Melzer")) {
                assertFalse("player should be found once " + player, found);
                found = true;
            }
        }
        //wait for RR file
//        assertTrue(found);

        String hinrunde = readFile("assets/mytt/teamplayers-vr.htm");
        players = myTischtennisParser.parsePlayerFromTeam(hinrunde);
        found = false;
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Kettler")) {
                assertFalse("player should be found once " + player, found);
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testcompletePlayerWithTTR() throws Exception {
        Player p = myTischtennisParser.findPlayer("Timo", "Boll", "Borussia Düsseldorf").get(0);
        assertNotNull(p);
        p.setTtrPoints(0);
        assertEquals(0, p.getTtrPoints());

        myTischtennisParser.completePlayerWithTTR(p);
        assertTrue(p.getTtrPoints() > 2000);
    }

    @Test
    public void testreadPlayersFromTeamNoId() throws Exception {
        List<Player> players = myTischtennisParser.readPlayersFromTeam(null);
        boolean found = false;
        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Zenner")) {
                found = true;
            }
        }
        assertTrue(found);
    }


    @Test
    public void testGetRealName() throws Exception {
        String name = myTischtennisParser.getRealName();
        assertEquals("Jürgen Melzer", name);
    }

    @Test
    public void testgetNameOfOwnClub() throws Exception {
        MyApplication.manualClub = "Dummy";

        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("Dummy", name);

        MyApplication.manualClub = null;
        name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);

    }

    @Test
    public void testSearch() throws Exception {

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer(null, null, "TV Bergheim/Sieg");
        assertTrue(players.size() > 20);
        //todo add chekcs
//        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
//        }
    }

    @Test
    public void testSearchError() throws Exception {
        List<Player> players = myTischtennisParser.findPlayer("Michael Stefan", "Keller", null);
        assertTrue(players.size() > 0);
        //todo add checks
//        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
//        }
        myTischtennisParser = new MyTischtennisParser();
        try {
            myTischtennisParser.findPlayer("Peter", "Meyers", null);
            fail();
        } catch (ValidationException e) {
            //ok
        }
    }

    @Test
    public void testPerf() throws Exception {
        long start = System.currentTimeMillis();
        myTischtennisParser.findPlayer(null, null, "TTG St. Augustin");
        Log.i(Constants.LOG_TAG, "findPlayer time " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void testFindPlayerWithClubName() throws Exception {

        assertTrue(myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim-Sieg").get(0).getTtrPoints() > 1500);
        List<Player> p = myTischtennisParser.findPlayer("christian", "hinrichs", "TTG St. Augustin");
        assertEquals("Hinrichs", p.get(0).getLastname());
        assertEquals("Christian", p.get(0).getFirstname());

        p = myTischtennisParser.findPlayer("manfred", "Hildebrand", "TTG St. Augustin");
        assertEquals("Hildebrandt", p.get(0).getLastname());
        assertEquals("Manfred", p.get(0).getFirstname());


        p = myTischtennisParser.findPlayer("Patrick", "Kaufmann", "Aggertaler TTC Gummersbach");
        assertNotNull(p);
    }

    @Test
    public void testFindPlayer() throws Exception {

        SearchPlayer searchPlayer = new SearchPlayer();
        List<Player> players = myTischtennisParser.findPlayer(searchPlayer);
        assertEquals(100, players.size());

        players = myTischtennisParser.findPlayer("Astrid", "Schulz", null);
        assertEquals(3, players.size());

        assertTrue(myTischtennisParser.findPlayer("Marco", "Vester", null).get(0).getTtrPoints() > 1900);


        players = myTischtennisParser.findPlayer("Johannes ", "hinrichs", "");
        assertNotNull(players);
//        152009

    }

    @Test
    public void testFindPlayer1() throws Exception {
        List<Player> players = myTischtennisParser.findPlayer("achim ", "hugo", "");
        assertEquals(2, players.size());
        assertEquals("Achim", players.get(0).getFirstname());
        assertEquals("Hugo", players.get(0).getLastname());
        assertEquals("TTC Troisdorf", players.get(0).getClub());
    }

    @Test
    public void testFindPlayerUmlaut() throws Exception {
        List<Player> players = myTischtennisParser.findPlayer("Stefan ", "Köhler", "");
        assertEquals(6, players.size());
        assertEquals("Stefan", players.get(1).getFirstname());
        assertEquals("Köhler", players.get(1).getLastname());
        assertEquals("SC 1904 Nürnberg e.V.", players.get(1).getClub());
    }

    @Test
    public void testGetClubNameFromTeamName() {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel II"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel III"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel IV"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel V"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel VI"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel VII"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel VIII"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel IX"));
        assertEquals("TTG Niederkassel", myTischtennisParser.getClubNameFromTeamName("TTG Niederkassel X"));
    }

    @Test
    public void testreadOwnLigaRanking() throws Exception {
        List<Player> players = myTischtennisParser.readOwnLigaRanking().get(0).getRanking();
        assertTrue(players.size() > 80);

        for (Player player : players) {
            if (!player.getLastname().equals("Quante")) {
                assertTrue(player.toString(), player.getTtrPoints() > 0);
            }
            assertNotNull(player.toString(), player.getClub());
            assertNotNull(player.toString(), player.getFirstname());
            assertNotNull(player.toString(), player.getLastname());
        }
    }
}
