/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;

import java.util.List;

public class MyTischtennisParserTest extends BaseTestCase {

    @SmallTest
    public void testgetPoints() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        assertTrue(myPoints > 1600);
    }

    @Override
    protected void prepareMocks() {
        super.prepareMocks();
        MockResponses.forRequestDoAnswer(".*/events", "events.htm");
        MockResponses.forRequestDoAnswer(".*vereinId=156012.*", "ranking_verein.htm");
        MockResponses.forRequestDoAnswer(".*personId=425165", "events_425165.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Astrid&nachname=Schulz.*", "search_schulz.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Marco&nachname=Vester.*", "search_vester.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Jens&nachname=Bauer.*", "search_bauer.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Christian&nachname=Hinrichs.*", "search_hinni.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Manfred&nachname=Hildebrand.*", "search_manfred.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Patrick&nachname=Kaufmann.*", "search_kaufmann.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Johannes&nachname=Hinrichs.*", "search_joh_hinrichs.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Achim&nachname=Hugo.*", "search_hugo.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Timo&nachname=Boll.*", "search_boll.htm");
        MockResponses.forRequestDoAnswer(".*verein=TV.Bergheim.*", "search_bergheim.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Michael.Stefan&nachname=Keller.*", "search_keller.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Peter&nachname=Meyers.*", "search_meyers.htm");
        MockResponses.forRequestDoAnswer(".*showclubinfo.*", "showclubinfo.htm");
        MockResponses.forRequestDoAnswer(".*vereinid=3147.*", "ranking_verein.htm");
        MockResponses.forRequestDoAnswer(".*userMasterPage.*", "userMasterPage.htm");
        MockResponses.forRequestDoAnswer(".*62679901.*", "62679901.htm");
        MockResponses.forRequestDoAnswer(".*62656668.*", "62679901.htm");
        MockResponses.forRequestDoAnswer(".*1411599.*", "team_id_1411599.htm");
        MockResponses.forRequestDoAnswer(".*teamplayers.*", "teamplayers.htm");
    }

    @SmallTest
    public void testReadEvents() throws Exception {
        login();

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

    @SmallTest
    public void testreadGamesForForeignPlayer() throws Exception {
        login();

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

    @SmallTest
    public void testreadDetailGame() throws Exception {
        login();

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

//    @SmallTest
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

    @SmallTest
    public void testReadBetween() {
        String toTest = "bla bla<td>06.12.2014</td><td style=\"width: 550px;";
        assertEquals("06.12.2014", new MyTischtennisParser().readBetween(toTest, 7, "<td>", "</td>").result);
    }

    @SmallTest
    public void teststripTags() {
        String toTest = "style=\"width: 550px;\"><a href=\"javascript:openmoreinfos(423703061," +
                "'eventdiv1');\" class=\"trigger2\" title=\"Details anzeigen\">BK-Herren | TV Bergheim II : TTG St. " +
                "Augustin II";
        assertEquals("BK-Herren | TV Bergheim II : TTG St. Augustin II", new MyTischtennisParser().stripTags(toTest));
    }

    @SmallTest
    public void testGetClubList() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> clublist = myTischtennisParser.getClubList();
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

    @SmallTest
    public void testreadPlayersFromTeam() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
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

    @SmallTest
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
        assertTrue(found);

        String hinrunde = readFile("assets/mannschaft-hinrunde.html");
        players = myTischtennisParser.parsePlayerFromTeam(hinrunde);
        found = false;
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Werner")) {
                assertFalse("player should be found once " + player, found);
                found = true;
            }
        }
        assertTrue(found);
    }

    @SmallTest
    public void testcompletePlayerWithTTR() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        Player p = myTischtennisParser.findPlayer("Timo", "Boll", "Borussia Düsseldorf").get(0);
        assertNotNull(p);
        p.setTtrPoints(0);
        assertEquals(0, p.getTtrPoints());

        myTischtennisParser.completePlayerWithTTR(p);
        assertTrue(p.getTtrPoints() > 2000);
    }

    @SmallTest
    public void testreadPlayersFromTeamNoId() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.readPlayersFromTeam(null);
        boolean found = false;
        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Schramm")) {
                found = true;
            }
        }
        assertTrue(found);
    }


    @SmallTest
    public void testGetRealName() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getRealName();
        assertEquals("Jürgen Melzer", name);
    }

    @SmallTest
    public void testgetNameOfOwnClub() throws Exception {
        login();
        MyApplication.manualClub = "Dummy";
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("Dummy", name);

        MyApplication.manualClub = null;
        name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTF Bad Honnef", name);

    }

    @SmallTest
    public void testSearch() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer(null, null, "TV Bergheim/Sieg");
        assertTrue(players.size() > 20);
        //todo add chekcs
//        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
//        }
    }

    @SmallTest
    public void testSearchError() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("Michael Stefan", "Keller", null);
        assertTrue(players.size() > 0);
        //todo add checks
//        for (Player player : players) {
//            Log.i(Constants.LOG_TAG, player.toString());
//        }
        myTischtennisParser = new MyTischtennisParser();
        players = myTischtennisParser.findPlayer("Peter", "Meyers", null);
        assertEquals(0, players.size());
    }

    @SmallTest
    public void testPerf() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        long start = System.currentTimeMillis();
        myTischtennisParser.findPlayer(null, null, "TTG St. Augustin");
        Log.i(Constants.LOG_TAG, "findPlayer time " + (System.currentTimeMillis() - start) + " ms");
    }

    @SmallTest
    public void testFindPlayer() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("Astrid", "Schulz", null);
        assertEquals(2, players.size());

        assertTrue(myTischtennisParser.findPlayer("Marco", "Vester", null).get(0).getTtrPoints() > 1900);

        assertTrue(myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").get(0).getTtrPoints() > 1500);
        List<Player> p = myTischtennisParser.findPlayer("christian", "hinrichs", "TTG St. Augustin");
        assertEquals("Hinrichs", p.get(0).getLastname());
        assertEquals("Christian", p.get(0).getFirstname());

        p = myTischtennisParser.findPlayer("manfred", "Hildebrand", "TTG St. Augustin");
        assertEquals("Hildebrandt", p.get(0).getLastname());
        assertEquals("Manfred", p.get(0).getFirstname());


        p = myTischtennisParser.findPlayer("Patrick", "Kaufmann", "Aggertaler TTC Gummersbach");
        assertNotNull(p);

        p = myTischtennisParser.findPlayer("Johannes ", "hinrichs", "");
        assertNotNull(p);
//        152009

    }

    @SmallTest
    public void testFindPlayer1() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("achim ", "hugo", "");
        assertEquals(2, players.size());
        assertEquals("Achim", players.get(0).getFirstname());
        assertEquals("Hugo", players.get(0).getLastname());
        assertEquals("TTC Troisdorf", players.get(0).getClub());
    }

    @SmallTest
    public void testFindPlayerUmlaut() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("Stefan ", "Köhler", "");
        assertEquals(6, players.size());
        assertEquals("Stefan", players.get(1).getFirstname());
        assertEquals("Köhler", players.get(1).getLastname());
        assertEquals("SC 1904 Nürnberg e.V.", players.get(1).getClub());
    }

    @SmallTest
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

    @SmallTest
    public void testreadOwnLigaRanking() throws Exception {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
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
