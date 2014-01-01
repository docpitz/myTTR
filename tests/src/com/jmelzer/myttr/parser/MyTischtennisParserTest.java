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

public class MyTischtennisParserTest extends TestCase {

    @SmallTest
    public void testgetPoints() {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("myttlogin", "mytpw"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        System.out.println("ttrPointParser.parse() = " + myTischtennisParser.getPoints());
    }

    @SmallTest
    public void testFindPlayer() throws TooManyPlayersFound {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("myttlogin", "mytpw"));

        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        try {
            myTischtennisParser.findPlayer("Jens", "Bauer", null);
            fail("TooManyPlayersFound expected");
        } catch (TooManyPlayersFound tooManyPlayersFound) {
            //ok
        }
        assertEquals(2016, myTischtennisParser.findPlayer("Marco", "Vester", null).getTtrPoints());

        assertEquals(1742, myTischtennisParser.findPlayer("Jens", "Bauer", "TV Bergheim/Sieg").getTtrPoints());
        Player p = myTischtennisParser.findPlayer("christian", "hinrichs", "TTG St. Augustin");
        assertEquals("Hinrichs", p.getLastname());
        assertEquals("Christian", p.getFirstname());

        p = myTischtennisParser.findPlayer("manfred", "Hildebrand", "TTG St. Augustin");
        assertEquals("Hildebrandt", p.getLastname());
        assertEquals("Manfred", p.getFirstname());

    }
}
