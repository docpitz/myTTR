/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.parser;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;

public class MyTischtennisParserTest extends TestCase {

    @SmallTest
    public void testgetPoints() throws PlayerNotWellRegistered {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        System.out.println("ttrPointParser.parse() = " + myTischtennisParser.getPoints());
        assertEquals(1564, myPoints);
    }

    @SmallTest
    public void testGetClubList() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> clublist = myTischtennisParser.getClubList();
        for (Player player : clublist) {
            Log.i(Constants.LOG_TAG, player.toString());
        }
    }

    @SmallTest
    public void testreadPlayersFromTeam() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> players = myTischtennisParser.readPlayersFromTeam("1234868");
        boolean found = false;
        for (Player player : players) {
            Log.i(Constants.LOG_TAG, player.toString());
            if (player.getLastname().equals("Hugo"))
                found = true;
        }
        assertTrue(found);
    }

    @SmallTest
    public void testGetRealName() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getRealName();
        assertEquals("Jürgen Melzer", name);
    }

    @SmallTest
    public void testgetNameOfOwnClub() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);
    }

    @SmallTest
    public void testFindPlayer() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        try {
            myTischtennisParser.findPlayer("Jens", "Bauer", null);
            fail("TooManyPlayersFound expected");
        } catch (TooManyPlayersFound tooManyPlayersFound) {
            //ok
        }
        assertEquals(2017, myTischtennisParser.findPlayer("Marco", "Vester", null).getTtrPoints());

        assertEquals(1734, myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").getTtrPoints());
        Player p = myTischtennisParser.findPlayer("christian", "hinrichs", "TTG St. Augustin");
        assertEquals("Hinrichs", p.getLastname());
        assertEquals("Christian", p.getFirstname());

        p = myTischtennisParser.findPlayer("manfred", "Hildebrand", "TTG St. Augustin");
        assertEquals("Hildebrandt", p.getLastname());
        assertEquals("Manfred", p.getFirstname());

    }
}
