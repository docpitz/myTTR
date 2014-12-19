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

public class IntegrationTest extends BaseTestCase {

    @SmallTest
    public void test() throws TooManyPlayersFound, PlayerNotWellRegistered, IOException, NetworkException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        assertTrue(myPoints > 0);
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);

        Player p = myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        assertNotNull(p);
        System.out.println("ttrB = " + p.getTtrPoints());
        assertTrue(p.getTtrPoints() > 0);
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(11, calculator.calcPoints(myPoints, p.getTtrPoints(), true));
    }

    @SmallTest
    public void testReadPointsFromNextAppointment() throws TooManyPlayersFound, PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {
        login();

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        AppointmentParser parser = new AppointmentParser();
        List<TeamAppointment> list = parser.read("TTG St. Augustin");

        assertTrue(list.size() > 0);
        int i = 0;
        for (TeamAppointment teamAppointment : list) {
//            if (i++ == 0) continue;
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
            List<Player> players = myTischtennisParser.readPlayersFromTeam(teamAppointment.getId());
            for (Player player : players) {
                Player fp = null;
                try {
                    fp = myTischtennisParser.findPlayer(player.getFirstname(), player.getLastname(), player.getClub());
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                } catch (NetworkException e) {
                }
                if (fp != null) {
                    Log.d(Constants.LOG_TAG, "player found = " + fp.toString());
                } else {
                    Log.d(Constants.LOG_TAG, "could not find player " + player);
                    fp = myTischtennisParser.completePlayerWithTTR(player);
                }
                assertTrue("must be found " + fp, fp.getTtrPoints() > 0);
                Log.d(Constants.LOG_TAG, fp.toString());
            }
        }

    }
}
