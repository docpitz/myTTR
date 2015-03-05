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

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Verband;

import java.util.List;

public class ClickTTParserIntegrationTest extends BaseTestCase {

    ClickTTParser parser;

    @Override
    protected void setUp() throws Exception {
        parser = new ClickTTParser();
    }

    @SmallTest
    public void testReadIntegration() throws NetworkException {
        List<Verband> verbaende = parser.readVerbaende();
        assertEquals(12, verbaende.size());
        Verband v = verbaende.get(0);
        assertNotNull(v);
        assertSame(Verband.dttb, v);

        //first one is dttp
        Verband verband = parser.readTopLigen();
//        for (Liga liga : ligen) {
//            Log.d(Constants.LOG_TAG, "liga = " + liga);
//        }
        assertSame(Verband.dttb, verband);
        assertEquals("must be 34 ", 34, verband.getLigaList().size());

        //read ligen for one verband
        Verband nrw = verbaende.get(verbaende.size() - 1);
        parser.readLigen(nrw);
        assertEquals(47, nrw.getLigaList().size());

        parser.readBezirke(nrw);
        assertEquals(5, nrw.getBezirkList().size());

        Bezirk bezirk = nrw.getBezirkList().get(2);
        assertEquals("Mittelrhein", bezirk.getName());
        assertNotNull(bezirk.getUrl());
        parser.readKreiseAndLigen(bezirk);
        assertEquals(9, bezirk.getKreise().size());
        Kreis rheinSieg = bezirk.getKreise().get(5);
        assertEquals("Rhein-Sieg", rheinSieg.getName());

        parser.readLigen(rheinSieg);

        Liga liga = rheinSieg.getLigen().get(0);
        ligaTest(liga, "Kreisliga", 12);

        //selecting one liga
        liga = verband.getLigaList().get(5);
        ligaTest(liga, "Regionalliga West", 10);

    }

    void ligaTest(Liga liga, String name, int mcount) throws NetworkException {
        parser.readLiga(liga);
        Log.d(Constants.LOG_TAG, "liga = " + liga);

        assertEquals(name, liga.getName());
        assertEquals(mcount, liga.getMannschaften().size());

        parser.readVR(liga);

        assertTrue(liga.getSpieleVorrunde().size() > 5);

        Mannschaftspiel spiel = liga.getSpieleVorrunde().get(5);
        parser.readDetail(liga, spiel);

        assertTrue(spiel.getSpiele().size() > 0);
    }
}


