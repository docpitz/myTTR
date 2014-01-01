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
    public void test() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("myttlogin", "mytpw"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        int myPoints = myTischtennisParser.getPoints();

        Player p  = myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        assertNotNull(p);
        System.out.println("ttrB = " + p.getTtrPoints());
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(15, calculator.calcPoints(myPoints, p.getTtrPoints(), true));
    }
}
