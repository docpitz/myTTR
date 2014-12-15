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
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.TeamAppointment;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;

public class IntegrationTest extends TestCase {

    @SmallTest
    public void test() throws TooManyPlayersFound, PlayerNotWellRegistered, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        assertTrue(myPoints > 0);
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);

        Player p  = myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        assertNotNull(p);
        System.out.println("ttrB = " + p.getTtrPoints());
        assertTrue(p.getTtrPoints() > 0);
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(11, calculator.calcPoints(myPoints, p.getTtrPoints(), true));
    }

    @SmallTest
    public void testReadPointsFromNextAppointment() throws TooManyPlayersFound, PlayerNotWellRegistered, IOException, NetworkException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        AppointmentParser parser= new AppointmentParser();
        List<TeamAppointment> list = parser.read("TTG St. Augustin");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
            List<Player> players = myTischtennisParser.readPlayersFromTeam(teamAppointment.getId());
            for (Player player : players) {
                Player fp = myTischtennisParser.findPlayer(player.getFirstname(), player.getLastname(), player.getClub());
                if (fp != null) {
                    Log.d(Constants.LOG_TAG, fp.toString());
                } else {
                    Log.d(Constants.LOG_TAG, "could not find player " + player);
                }
            }
        }

    }
}
