/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;

import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Verband;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class ClickTTParserTest {

    ClickTTParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new ClickTTParser();
    }

    protected String readFile(String file) throws IOException {
        InputStream in = new FileInputStream("src/androidTest/" + file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return StringEscapeUtils.unescapeHtml4(stringBuilder.toString());
    }

    @Test
    public void testReadTopligen() throws Exception {
        String page = readFile("assets/clicktt/dttb-click-TT-Ligen.htm");
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            Log.d(Constants.LOG_TAG, "liga = " + liga);
        }
        assertTrue("must be > 10 not " + ligen.size(), ligen.size() > 10);
    }

    @Test
    public void testParseLigaLinks() throws Exception {
        String page = readFile("assets/clicktt/dttb-click-TT-Ligen.htm");
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            Log.d(Constants.LOG_TAG, "liga = " + liga);
        }
        assertEquals("must be 34 ", 34, ligen.size());
        assertEquals(17, count(ligen, "Herren"));
        assertEquals(17, count(ligen, "Damen"));
        page = readFile("assets/clicktt/liga-level2-wttv.htm");
        assertNotNull(page);
        ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            Log.d(Constants.LOG_TAG, "liga = " + liga);
        }
        assertEquals("must be 47 ", 47, ligen.size());
        assertEquals(18, count(ligen, "Herren"));
        assertEquals(6, count(ligen, "Damen"));
        assertEquals(4, count(ligen, "Jungen"));
        assertEquals(2, count(ligen, "Mädchen"));
        assertEquals(3, count(ligen, "Senioren 40"));
        assertEquals(1, count(ligen, "Seniorinnen 40"));
        assertEquals(3, count(ligen, "Senioren 50"));
        assertEquals(3, count(ligen, "Seniorinnen 50"));
        assertEquals(3, count(ligen, "Senioren 60"));
        assertEquals(1, count(ligen, "Seniorinnen 60"));
        assertEquals(3, count(ligen, "Senioren 70"));

        page = readFile("assets/clicktt/liga-level2-bremen.htm");
        assertNotNull(page);
        ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            Log.d(Constants.LOG_TAG, "liga = " + liga);
        }
        assertEquals("must be 13 ", 13, ligen.size());
        assertEquals(10, count(ligen, "Herren"));
        assertEquals(1, count(ligen, "Damen"));
        assertEquals(1, count(ligen, "Jungen"));
        assertEquals(1, count(ligen, "Senioren"));
    }

    int count(List<Liga> ligen, String kat) {
        int c = 0;
        for (Liga liga : ligen) {
            if (kat.equals(liga.getKategorie())) {
                c++;
            }
        }
        return c;
    }

    @Test
    public void testParseLinksSubLigen() throws Exception {
        String page = readFile("assets/clicktt/dttb-click-TT-Ligen.htm");
        assertNotNull(page);
        List<Verband> verbandList = parser.parseLinksSubLigen(page);
        for (Verband verband : verbandList) {
            Log.d(Constants.LOG_TAG, "verband = " + verband);
        }
        assertEquals("must be 11 ", 11, verbandList.size());
    }


    @Test
    public void testReadStaffel() throws Exception {
        String page = readFile("assets/clicktt/staffel.htm");
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);

        for (Mannschaft m : liga.getMannschaften()) {
            Log.d(Constants.LOG_TAG, "m = " + m);
        }
    }

    @Test
    public void testParseErgebnisse() throws Exception {
        Liga liga = ergebnisse();

        assertTrue(liga.getSpieleVorrunde().size() > 0);
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            Log.d(Constants.LOG_TAG, "sp = " + mannschaftspiel);
        }
    }

    Liga ergebnisse() throws IOException {
        String page = readFile("assets/clicktt/staffel.htm");
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);
        page = readFile("assets/clicktt/spiele.htm");
        parser.parseErgebnisse(liga, page, true);
        return liga;
    }

    @Test
    public void testParseMannschaftspiel() throws Exception {
        Liga liga = ergebnisse();
        String page = readFile("assets/clicktt/mannschafts-spiel.htm");
        assertTrue(liga.getSpieleVorrunde().size() > 0);
        Mannschaftspiel mannschaftspiel = liga.getSpieleVorrunde().get(0);
        parser.parseMannschaftspiel(page, mannschaftspiel);
        Log.d(Constants.LOG_TAG, "sp = " + mannschaftspiel);
    }

}


