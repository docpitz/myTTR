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
import com.jmelzer.myttr.Player;
import junit.framework.Assert;
import junit.framework.TestCase;

public class IntegrationTest extends TestCase {

    @SmallTest
    public void test() throws TooManyPlayersFound, PlayerNotWellRegistered {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("un", "pw"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();
        assertEquals(1564, myPoints);

        Player p  = myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        assertNotNull(p);
        System.out.println("ttrB = " + p.getTtrPoints());
        assertEquals(1734, p.getTtrPoints());
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(15, calculator.calcPoints(myPoints, p.getTtrPoints(), true));
    }
}
