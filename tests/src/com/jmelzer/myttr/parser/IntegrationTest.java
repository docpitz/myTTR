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
import junit.framework.Assert;
import junit.framework.TestCase;

public class IntegrationTest extends TestCase {

    @SmallTest
    public void test()  {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        TTRPointParser ttrPointParser = new TTRPointParser();
        int myPoints = ttrPointParser.getPoints();

        int ttrB = ttrPointParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg");
        System.out.println("ttrB = " + ttrB);
        TTRCalculator calculator = new TTRCalculator();
        assertEquals(15, calculator.calcPoints(myPoints, ttrB, true));
    }
}
