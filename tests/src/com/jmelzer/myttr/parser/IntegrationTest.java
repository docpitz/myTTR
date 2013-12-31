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
    public void test()  {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        TTRPointParser ttrPointParser = new TTRPointParser();
        int myPoints = ttrPointParser.getPoints();

        Player p  = ttrPointParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        assertNotNull(p);
        System.out.println("ttrB = " + p.getTtrPoints());
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(15, calculator.calcPoints(myPoints, p.getTtrPoints(), true));
    }
}
