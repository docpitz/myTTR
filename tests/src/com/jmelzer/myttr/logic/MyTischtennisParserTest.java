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
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;

public class MyTischtennisParserTest extends TestCase {

    @SmallTest
    public void testgetPoints() throws PlayerNotWellRegistered, IOException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        System.out.println("ttrPointParser.parse() = " + myTischtennisParser.getPoints());
        assertEquals(1650, myPoints);
    }

    @SmallTest
    public void testreadGames() throws PlayerNotWellRegistered, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Event> events = myTischtennisParser.readEvents();
        for (Event event : events) {
            Log.i(Constants.LOG_TAG, event.toString());
        }
    }
    @SmallTest
    public void testreadDetailGame() throws PlayerNotWellRegistered, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

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
        assertEquals("06.12.2014", new MyTischtennisParser().readBetween(toTest , 7, "<td>", "</td>").result);
    }
    @SmallTest
    public void teststripTags() {
        String toTest = "style=\"width: 550px;\"><a href=\"javascript:openmoreinfos(423703061," +
                "'eventdiv1');\" class=\"trigger2\" title=\"Details anzeigen\">BK-Herren | TV Bergheim II : TTG St. " +
                "Augustin II";
        assertEquals("BK-Herren | TV Bergheim II : TTG St. Augustin II", new MyTischtennisParser().stripTags(toTest ));
    }
    @SmallTest
    public void testGetClubList() throws TooManyPlayersFound, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> clublist = myTischtennisParser.getClubList();
        for (Player player : clublist) {
            Log.i(Constants.LOG_TAG, player.toString());
        }
    }

    @SmallTest
    public void testreadPlayersFromTeam() throws TooManyPlayersFound, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.readPlayersFromTeam("1411599");
        boolean found = false;
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Werner"))
                found = true;
        }
        assertTrue(found);
    }

    @SmallTest
    public void testGetRealName() throws TooManyPlayersFound, IOException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getRealName();
        assertEquals("Jürgen Melzer", name);
    }

    @SmallTest
    public void testgetNameOfOwnClub() throws TooManyPlayersFound, IOException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);
    }

    @SmallTest
    public void testFindPlayer() throws TooManyPlayersFound, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        try {
            myTischtennisParser.findPlayer("Astrid", "Schulz", null);
            fail("TooManyPlayersFound expected");
        } catch (TooManyPlayersFound tooManyPlayersFound) {
            //ok
        }
        assertEquals(2014, myTischtennisParser.findPlayer("Marco", "Vester", null).getTtrPoints());

        assertEquals(1711, myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").getTtrPoints());
        Player p = myTischtennisParser.findPlayer("christian", "hinrichs", "TTG St. Augustin");
        assertEquals("Hinrichs", p.getLastname());
        assertEquals("Christian", p.getFirstname());

        p = myTischtennisParser.findPlayer("manfred", "Hildebrand", "TTG St. Augustin");
        assertEquals("Hildebrandt", p.getLastname());
        assertEquals("Manfred", p.getFirstname());

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
