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

public class TTRPointParserTest extends TestCase {

    @SmallTest
    public void testgetPoints() {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        TTRPointParser ttrPointParser = new TTRPointParser();
        System.out.println("ttrPointParser.parse() = " + ttrPointParser.getPoints());
    }

    @SmallTest
    public void testFindPlayer() {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        TTRPointParser ttrPointParser = new TTRPointParser();
        assertEquals(-1, ttrPointParser.findPlayer("Jens", "Bauer", null));
        assertEquals(2016, ttrPointParser.findPlayer("Marco", "Vester", null));

        assertEquals(1742, ttrPointParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg"));

    }
}
