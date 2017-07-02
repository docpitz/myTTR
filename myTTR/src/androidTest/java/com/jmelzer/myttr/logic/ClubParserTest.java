/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;


import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ClubParserTest extends TestCase {

    @Test
    public void testGetVereinExact() {
        ClubParser clubParser = new ClubParser();
        assertNull(clubParser.getClubExact("Bla"));
        assertNotNull(clubParser.getClubExact("TTG St. Augustin"));
        assertNotNull(clubParser.getClubExact("TV Bergheim/Sieg"));
        assertNotNull(clubParser.getClubExact("TV Geislar"));

    }

    @Test
    public void testGetVereinUncharp() {
        ClubParser clubParser = new ClubParser();
//        assertEquals(0, clubParser.getClubNameUnsharp("Bjsagsjdgla").size());

        assertTrue(clubParser.getClubNameUnsharp("TuRa Germania Oberdrees").size() == 0);
        assertTrue(clubParser.getClubNameUnsharp("TuRa Germania Oberdrees", 0.4f).size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("Oberdrees").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("Augustin").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("Bergheim").size() > 1);
        assertTrue(clubParser.getClubNameUnsharp("ESV Blau-Rot Bonn").size() > 0);
        assertNotNull(clubParser.getClubExact("ESV BR Bonn"));
        assertTrue(clubParser.getClubNameUnsharp("ESV Bonn").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("TTC Blau-Rot 1963 Uedorf").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("1. TTC Münster").size() > 0);

    }
}
