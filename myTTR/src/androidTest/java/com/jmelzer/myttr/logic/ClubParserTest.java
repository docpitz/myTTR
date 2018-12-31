package com.jmelzer.myttr.logic;


import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static com.jmelzer.myttr.activities.TestHelper.login;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ClubParserTest  {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testGetVereinExact() {
        ClubParser clubParser = new ClubParser();
        assertNull(clubParser.getClubExact("Bla"));
        assertNotNull(clubParser.getClubExact("TTG St. Augustin"));
        assertNotNull(clubParser.getClubExact("TV Bergheim-Sieg"));
        assertNotNull(clubParser.getClubExact("TV Geislar"));

    }


    @Test
//    @Ignore
    public void callSome() throws Exception {
        login();

        ClubParser clubParser = new ClubParser();
        clubParser.readClubs();
        MyTischtennisParser parser = new MyTischtennisParser();
        Random generator = new Random();
        Object[] values = ClubParser.clubHashMap.values().toArray();
        for (int i = 0; i < 2; i++) {
            Club randomValue = (Club)values[generator.nextInt(values.length)];
            List<Player> list = null;
            try {
                list = parser.findPlayer(null,null,randomValue.getName());
            } catch (ValidationException e) {
                System.err.println(e.getMessage() +" - " + randomValue);
            }
            assertNotNull(list);
        }
    }

    @Test
    public void testGetVereinUncharp() {

        ClubParser clubParser = new ClubParser();
//        assertEquals(0, clubParser.getClubNameUnsharp("Bjsagsjdgla").size());

        assertResultGreater("Hausen", 0.5f, 3, true);
        assertResultGreater("Köln", 0.3f, 3, true);
        assertResultExact("Weinh", 0.3f, 3, true);
        assertResultExact("TuRa Germania Oberdrees", 0.8f, 1, false);
        assertResultGreater("Telekom-Post", 0.3f, 0, false);
        assertResultExact("Oberdrees", 0.3f, 1, true);
        assertResultExact("Neuss", 0.3f, 6, true);
        assertResultExact("Augustin", 0.3f, 3, true);
        assertResultExact("Bergheim", 0.3f, 5, true);
        assertResultGreater("ESV Blau-Rot Bonn", 0.3f, 0, false);
        assertResultGreater("ESV Blau-Rot Bonn", 0.3f, 1, true);
        assertNotNull(clubParser.getClubExact("ESV BR Bonn"));
        assertTrue(clubParser.getClubNameUnsharp("ESV Bonn").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("TTC Blau-Rot 1963 Uedorf").size() > 0);
        assertTrue(clubParser.getClubNameUnsharp("1. TTC Münster").size() > 0);
        assertResultExact("München", 0.3f, 34, true);
        assertResultExact("Bonn", 0.3f, 9, true);
        assertResultExact("TTC Rhön-Sprudel Fulda-Maberzell", 0.3f, 1, true);

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
        if (result.size() == 0 && count == 0 || result.size() < count) {
            fail(count + "<" + result.size() + " \n" + result);
        }
    }
}
