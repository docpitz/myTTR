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
import junit.framework.TestCase;

public class ClubParserTest extends TestCase {

    @SmallTest
    public void testGetVereinExact() {
        ClubParser clubParser = new ClubParser();
        assertNull(clubParser.getClubExact("Bla"));
        assertNotNull(clubParser.getClubExact("TTG St. Augustin"));
        assertNotNull(clubParser.getClubExact("TV Bergheim/Sieg"));
        assertNotNull(clubParser.getClubExact("TV Geislar"));

    }

    @SmallTest
    public void testGetVereinUncharp() {
        ClubParser clubParser = new ClubParser();
        assertEquals(0, clubParser.getClubNameUnsharp("Bjsagsjdgla").size());
        assertTrue(clubParser.getClubNameUnsharp("Augustin").size() > 1);
        assertTrue(clubParser.getClubNameUnsharp("Bergheim").size() > 1);

    }
}
