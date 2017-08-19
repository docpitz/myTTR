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

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Player;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
    @Ignore
    public void callSome() throws TooManyPlayersFound, NetworkException {
        ClubParser clubParser = new ClubParser();
        clubParser.readClubs();
        MyTischtennisParser parser = new MyTischtennisParser();
        Random generator = new Random();
        Object[] values = ClubParser.clubHashMap.values().toArray();
        for (int i = 0; i < 100; i++) {
            Club randomValue = (Club)values[generator.nextInt(values.length)];
            List<Player> list = parser.findPlayer(null,null,randomValue.getName());
            assertNotNull(list);
        }
    }

    @Test
    public void testGetVereinUncharp() {

        ClubParser clubParser = new ClubParser();
//        assertEquals(0, clubParser.getClubNameUnsharp("Bjsagsjdgla").size());

        assertResultExact("Weinh", 0.49f, 3, true);
        assertResultExact("TuRa Germania Oberdrees", 0.8f, 0, false);
        assertResultGreater("TuRa Germania Oberdrees", 0.49f, 0, false);
        assertResultExact("Oberdrees", 0.49f, 1, true);
        assertResultExact("Augustin", 0.49f, 3, true);
        assertResultExact("Bergheim", 0.49f, 5, true);
        assertResultGreater("ESV Blau-Rot Bonn", 0.49f, 0, false);
        assertResultGreater("ESV Blau-Rot Bonn", 0.49f, 1, true);
        assertNotNull(clubParser.getClubExact("ESV BR Bonn"));
        assertTrue(clubParser.getClubNameUnsharp("ESV Bonn").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("TTC Blau-Rot 1963 Uedorf").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("1. TTC Münster").size() > 0);
        assertResultExact("München", 0.49f, 31, true);
        assertResultExact("Bonn", 0.49f, 8, true);
        assertResultExact("TTC Rhön-Sprudel Fulda-Maberzell", 0.49f, 5, true);

    }

    private void assertResultExact(String search, float minScore, int count, boolean recursiv) {
        ClubParser clubParser = new ClubParser();
        List<String> result = clubParser.getClubNameUnsharp(search, minScore, recursiv);
        if (result.size() != count) {
            fail(count + "!=" + result.size() + " \n" + result);
        }
    }

    private void assertResultGreater(String search, float minScore, int count, boolean recursiv) {
        ClubParser clubParser = new ClubParser();
        List<String> result = clubParser.getClubNameUnsharp(search, minScore, recursiv);
        if (result.size() < count) {
            fail(count + "<" + result.size() + " \n" + result);
        }
    }
}
