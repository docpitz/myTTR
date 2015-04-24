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
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.Player;

import java.io.IOException;
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
        MockResponses.forRequestDoAnswer(".*personId=425165", "events_425165.htm");
        MockResponses.forRequestDoAnswer(".*eventDetails.*", "eventDetails.htm");
        MockResponses.forRequestDoAnswer(".*showclubinfo.*", "showclubinfo.htm");
        MockResponses.forRequestDoAnswer(".*vereinid=3147.*", "ranking_verein.htm");
    }

    @SmallTest
    public void testreadGames() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEvents().getEvents();
        boolean found = false;
        for (Event event : events) {
            String s = event.toString();
            Log.i(Constants.LOG_TAG, s);
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
        for (Event event : events) {
            Log.i(Constants.LOG_TAG, event.toString());
            EventDetail eventDetail = myTischtennisParser.readEventDetail(event);
            Log.i(Constants.LOG_TAG, eventDetail.toString());
        }
    }

    @SmallTest
    public void testreadDetailGameError1() throws Exception {

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String page = readFile("assets/eventDetails_error1.htm");
        EventDetail eventDetail = myTischtennisParser.parseDetail(page);
        assertNotNull(eventDetail);
        assertEquals(6, eventDetail.getGames().size());
        Log.i(Constants.LOG_TAG, eventDetail.toString());
    }

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
            Log.i(Constants.LOG_TAG, player.toString());
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
            Log.i(Constants.LOG_TAG, player.toString());
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
            Log.i(Constants.LOG_TAG, player.toString());
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
    public void testreadPlayersFromTeamNoId() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.readPlayersFromTeam(null);
        boolean found = false;
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Kraut")) {
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
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);
    }

    @SmallTest
    public void testSearch() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer(null, null, "TV Bergheim/Sieg");
        assertTrue(players.size() > 20);
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
        }
    }

    @SmallTest
    public void testSearchError() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("Michael Stefan", "Keller", null);
        assertTrue(players.size() > 0);
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
        }
        myTischtennisParser = new MyTischtennisParser();
        players = myTischtennisParser.findPlayer("Peter", "Meyers", null);
        assertEquals(0, players.size());
    }

    @SmallTest
    public void testFindPlayer() throws Exception {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("Astrid", "Schulz", null);
        assertEquals(2, players.size());

        assertTrue(myTischtennisParser.findPlayer("Marco", "Vester", null).get(0).getTtrPoints() > 1900);

        assertTrue(myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").get(0).getTtrPoints() > 1600);
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
        List<Player> players = myTischtennisParser.findPlayer("timo ", "boll", "");
        assertEquals(4, players.size());
        assertEquals("Timo", players.get(2).getFirstname());
        assertEquals("Boll", players.get(2).getLastname());
        assertEquals("Borussia Düsseldorf", players.get(2).getClub());
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
}
