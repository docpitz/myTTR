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
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.logic.*;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;

public class IntegrationTest extends TestCase {
    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
    AppointmentParser appointmentParser = new AppointmentParser();

    @SmallTest
    public void test() throws TooManyPlayersFound, PlayerNotWellRegistered {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("un", "pw"));


        int myPoints = myTischtennisParser.getPoints();
        assertEquals(1567, myPoints);
        String name = myTischtennisParser.getNameOfOwnClub();
        assertEquals("TTG St. Augustin", name);

        Player p = myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        assertNotNull(p);
        System.out.println("ttrB = " + p.getTtrPoints());
        assertEquals(1722, p.getTtrPoints());
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(15, calculator.calcPoints(myPoints, p.getTtrPoints(), true));
    }

    @SmallTest
    public void testNextApp() throws TooManyPlayersFound, PlayerNotWellRegistered {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("un", "pw"));

        String name = myTischtennisParser.getNameOfOwnClub();
        List<TeamAppointment> teamAppointments = appointmentParser.read(name);
        String teamid = teamAppointments.get(0).getId();
        String clubName= teamAppointments.get(0).getTeam();
        Log.i(Constants.LOG_TAG, "clubname:" + clubName);

        List<Player> foreignTeamPlayers = myTischtennisParser.readPlayersFromTeam(teamid);
        for (Player teamPlayer : foreignTeamPlayers) {
            Player p = null;
            try {
                Log.i(Constants.LOG_TAG, "try to find player:" + teamPlayer);
                p = myTischtennisParser.findPlayerWithClubName(teamPlayer.getFirstname(),
                                                   teamPlayer.getLastname(),
                                                   clubName);
                Log.i(Constants.LOG_TAG, "found player:" + p);
            } catch (TooManyPlayersFound tooManyPlayersFound) {
                fail(teamPlayer.toString());
            }
        }
    }
}
