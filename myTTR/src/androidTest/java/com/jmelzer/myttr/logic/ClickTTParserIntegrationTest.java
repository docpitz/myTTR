/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;

import android.test.suitebuilder.annotation.MediumTest;
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

    @MediumTest
    public void testAllVerbaende() throws Exception {
        List<Verband> verbaende = parser.readVerbaende();
        for (Verband verband : verbaende) {
            parser.readLigen(verband);
            assertTrue(verband.toString(), verband.getLigaList().size() > 0);
        }

//        Verband dttb = parser.readTopLigen();
//        parser.readLigen(dttb);
//        for (Liga liga : dttb.getLigaList()) {
//            Log.i(Constants.LOG_TAG, "read liga '" + liga.getNameForFav() + "'");
//            parser.readLiga(liga);
//            assertTrue(liga.toString(), liga.getMannschaften().size() > 0);
//            Log.i(Constants.LOG_TAG, "liga mannschaften = " + liga.getMannschaften().size() );
//        }

        //bezirke
        for (Verband verband : verbaende) {

//            readLigenAndTest(verband);

            Log.i(Constants.LOG_TAG, "read bezirke from '" + verband.getName() + "'");
            parser.readBezirke(verband);
            if (verband.getBezirkList().size() == 0) {
                Log.e(Constants.LOG_TAG, "no bezirk in '" + verband.getName() + "'");
            } else {
                for (Bezirk bezirk : verband.getBezirkList()) {
                    Log.i(Constants.LOG_TAG, "read kreis & liga from '" + bezirk.getName() + "'");
                    parser.readKreiseAndLigen(bezirk);
                    assertTrue(bezirk.getLigen().size() > 0);
                    for (Liga liga : bezirk.getLigen()) {
                        Log.i(Constants.LOG_TAG, "read liga '" + liga + "'");
                        parser.readLiga(liga);
                    }
                    assertTrue(bezirk.getKreise().size() > 0);
                }
            }
        }
    }

    void readLigenAndTest(Verband verband) throws NetworkException {
        Log.i(Constants.LOG_TAG, "read ligen from '" + verband.getName() + "'");
        parser.readLigen(verband);
        for (Liga liga : verband.getLigaList()) {
            Log.i(Constants.LOG_TAG, "read liga '" + liga.getNameForFav() + "'");
            parser.readLiga(liga);
            if (liga.getMannschaften().size() == 0) {
                Log.i(Constants.LOG_TAG, "liga don't have any mannschaften :-(");
            } else {
                Log.i(Constants.LOG_TAG, "liga mannschaften = " + liga.getMannschaften().size());
            }
        }
    }

}


