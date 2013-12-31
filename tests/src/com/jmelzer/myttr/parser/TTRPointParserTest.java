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
        assertNull(ttrPointParser.findPlayer("Jens", "Bauer", null));
        assertEquals(2016, ttrPointParser.findPlayer("Marco", "Vester", null).getTtrPoints());

        assertEquals(1742, ttrPointParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").getTtrPoints());
        Player p = ttrPointParser.findPlayer("christian", "hinrichs", "TTG St. Augustin");
        assertEquals("Hinrichs", p.getLastname());
        assertEquals("Christian", p.getFirstname());

        p = ttrPointParser.findPlayer("manfred", "Hildebrand", "TTG St. Augustin");
        assertEquals("Hildebrandt", p.getLastname());
        assertEquals("Manfred", p.getFirstname());

    }
}
