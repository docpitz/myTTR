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

import java.util.List;

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

        assertResultExact("TuRa Germania Oberdrees", 0.8f,  0);
        assertResultGreater("TuRa Germania Oberdrees", 0.49f, 0);
        assertResultExact("Oberdrees", 0.49f, 1);
        assertResultExact("Augustin", 0.49f, 3);
        assertResultExact("Bergheim", 0.49f, 5);
        assertResultGreater("ESV Blau-Rot Bonn", 0.49f, 0);
        assertNotNull(clubParser.getClubExact("ESV BR Bonn"));
        assertTrue(clubParser.getClubNameUnsharp("ESV Bonn").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("TTC Blau-Rot 1963 Uedorf").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("1. TTC Münster").size() > 0);
        assertResultExact("München", 0.49f, 31);
        assertResultExact("TTC Rhön-Sprudel Fulda-Maberzell", 0.49f, 1);

    }

    private void assertResultExact(String search, float minScore, int count) {
        ClubParser clubParser = new ClubParser();
        List<String> result = clubParser.getClubNameUnsharp(search, minScore);
        if (result.size() != count) {
            fail(count + "!=" + result.size()+ " \n" + result);
        }
    }

    private void assertResultGreater(String search, float minScore, int count) {
        ClubParser clubParser = new ClubParser();
        List<String> result = clubParser.getClubNameUnsharp(search, minScore);
        if (result.size() < count) {
            fail(count + "<" + result.size()+ " \n" + result);
        }
    }
}
