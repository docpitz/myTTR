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
import com.jmelzer.myttr.Player;

import java.io.IOException;
import java.util.List;

public class MyTischtennisParserTest extends BaseTestCase {

    @SmallTest
    public void testgetPoints() throws PlayerNotWellRegistered, IOException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        System.out.println("ttrPointParser.parse() = " + myTischtennisParser.getPoints());
        assertEquals(1664, myPoints);
    }

    @SmallTest
    public void testreadGames() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEvents();
        for (Event event : events) {
            String s = event.toString();
//            Log.i(Constants.LOG_TAG, event.toString());
        }
    }

    @SmallTest
    public void testreadGamesForForeignPlayer() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEventsForForeignPlayer(425165L);
        for (Event event : events) {
//            Log.i(Constants.LOG_TAG, event.toString());
        }
    }

    @SmallTest
    public void testreadDetailGame() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEvents();
        for (Event event : events) {
            Log.i(Constants.LOG_TAG, event.toString());
            EventDetail eventDetail = myTischtennisParser.readEventDetail(event);
            Log.i(Constants.LOG_TAG, eventDetail.toString());
        }
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
        }
    }

    @SmallTest
    public void testreadPlayersFromTeam() throws TooManyPlayersFound, IOException, NetworkException {
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
    public void testparsePlayerFromTeam() throws TooManyPlayersFound, IOException, NetworkException {
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
    public void testreadPlayersFromTeamNoId() throws TooManyPlayersFound, IOException, NetworkException {
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
    public void testGetRealName() throws TooManyPlayersFound, IOException {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getRealName();
        assertEquals("Jürgen Melzer", name);
    }

    @SmallTest
    public void testgetNameOfOwnClub() throws TooManyPlayersFound, IOException {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);
    }

    @SmallTest
    public void testSearch() throws TooManyPlayersFound, IOException, NetworkException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer(null, null, "TV Bergheim/Sieg");
        assertTrue(players.size() > 20);
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
        }
    }

    @SmallTest
    public void testFindPlayer() throws TooManyPlayersFound, IOException, NetworkException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("Astrid", "Schulz", null);
        assertEquals(2, players.size());

        assertTrue(myTischtennisParser.findPlayer("Marco", "Vester", null).get(0).getTtrPoints() > 1900);

        assertTrue(myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").get(0).getTtrPoints() > 1700);
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
    public void testFindPlayer1() throws TooManyPlayersFound, IOException, NetworkException {
        login();
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.findPlayer("achim ", "hugo", "");
        assertEquals(2, players.size());
        assertEquals("Achim", players.get(0).getFirstname());
        assertEquals("Hugo", players.get(0).getLastname());
        assertEquals("TTC Troisdorf", players.get(0).getClub());
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
