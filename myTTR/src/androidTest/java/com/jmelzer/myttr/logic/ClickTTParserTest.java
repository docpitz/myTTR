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
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;

import java.io.IOException;
import java.util.List;

public class ClickTTParserTest extends BaseTestCase {

    ClickTTParser parser;

    @Override
    protected void setUp() throws Exception {
        parser = new ClickTTParser();
    }

    @SmallTest
    public void testReadTopligen() throws Exception{
        String page = readFile("assets/clicktt/dttb-click-TT Ligen-n.htm");
        assertNotNull(page);
        List<Liga> ligen = parser.parseUntilOberliga(page);

        for (Liga liga : ligen) {
            Log.d(Constants.LOG_TAG, "liga = " + liga);
        }
        assertTrue("must be > 10 not " + ligen.size(), ligen.size() > 10);
    }

    @SmallTest
    public void testReadStaffel() throws Exception{
        String page = readFile("assets/clicktt/staffel.htm");
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);

        for (Mannschaft m : liga.getMannschaften()) {
            Log.d(Constants.LOG_TAG, "m = " + m);
        }
    }
    @SmallTest
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

    @SmallTest
    public void testParseMannschaftspiel() throws Exception {
        Liga liga = ergebnisse();
        String page = readFile("assets/clicktt/mannschafts-spiel.htm");
        assertTrue(liga.getSpieleVorrunde().size() > 0);
        Mannschaftspiel mannschaftspiel = liga.getSpieleVorrunde().get(0);
        parser.parseMannschaftspiel(page, mannschaftspiel);
        Log.d(Constants.LOG_TAG, "sp = " + mannschaftspiel);
    }
}


