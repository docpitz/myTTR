package com.jmelzer.myttr.logic;


import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Saison;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by cicgfp on 26.11.2017.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class MyTTClickTTParserIntegrationTest extends BaseTestCase {
    public static final Saison SAISON = Saison.SAISON_2018;
    MytClickTTWrapper parser = new MytClickTTWrapper();

    @SmallTest
    public void testReadBezirkeAndLigen() throws Exception {
        login();

        Verband wttv = Verband.verbaende.get(Verband.verbaende.size() - 1);
        parser.readBezirkeAndLigen(wttv, Saison.SAISON_2018);
        assertEquals(5, wttv.getBezirkList().size());
        for (Bezirk bezirk : wttv.getBezirkList()) {
            Log.d(Constants.LOG_TAG, "bezirk = " + bezirk);
        }
        assertEquals(48, wttv.getLigaList().size());
    }

    @Test
    public void testAllVerbaende() throws Exception {

        //bezirke
        for (Verband verband : Verband.verbaende) {
            Log.d(Constants.LOG_TAG, "verband '" + verband.getName() + "'");
//            readLigenAndTest(verband); //allready ok, uncomment if you want to test it
            if (!verband.getName().equals("Westdeutscher TTV")) {
                continue;
            }
//            if (verband == dttb) {
//                continue;
//            }

            Log.i(Constants.LOG_TAG, "read bezirke from '" + verband.getName() + "'");
            parser.readBezirkeAndLigen(verband, SAISON);
            if (verband.getBezirkList().size() == 0) {
                Log.e(Constants.LOG_TAG, "no bezirk in '" + verband.getName() + "'");
                assertThat(verband.getName(), notNullValue());
            } else {
                for (Bezirk bezirk : verband.getBezirkList()) {
                    Log.i(Constants.LOG_TAG, "read kreis & liga from '" + bezirk.getName() + "'");
                    parser.readKreiseAndLigen(SAISON, bezirk, MyApplication.selectedVerband);
                    int count = 0;
                    assertThat(bezirk.getLigen().size(), greaterThan(0));
                    for (Liga liga : bezirk.getLigen()) {
                        Log.i(Constants.LOG_TAG, "read liga '" + liga + "'");
                        parser.readLiga(SAISON, liga, MyApplication.selectedVerband);
                        readSpieleAndTest(liga);
                        if (count++ > 5) break; //5 are enough: todo get it random
                    }
                    if (bezirk.getKreise().size() == 0) {
                        Log.e(Constants.LOG_TAG, "bezirk don't have kreise " + bezirk.getName() + " - " + bezirk.getUrl());
                    } else {
                        for (Kreis kreis : bezirk.getKreise()) {
                            parser.readLigen(kreis, SAISON, MyApplication.selectedVerband);
                            int kc = 0;
                            for (Liga liga : kreis.getLigen()) {
                                Log.i(Constants.LOG_TAG, "read liga in kreise '" + liga + "'");
                                try {
                                    parser.readLiga(SAISON, liga, MyApplication.selectedVerband);
                                } catch (NullPointerException e) {
                                    Log.e(Constants.LOG_TAG, "NPE in  " + liga);
                                }
                                readSpieleAndTest(liga);
                                if (kc++>2) break;

                            }
                            kreis.addAllLigen(new ArrayList<Liga>()); //clear memory
                        }
                    }
                    bezirk.addAllLigen(new ArrayList<Liga>()); //clear memory
//                    assertTrue(bezirk.getKreise().size() > 0);
                }
            }
        }
    }

    void readSpieleAndTest(Liga liga) throws NetworkException {
        if (liga.getUrlRR() == null && liga.getUrlVR() == null && liga.getUrlGesamt() == null) {
            Log.e(Constants.LOG_TAG, "keine vorrunde / rueckrunde / gesamt fuer  " + liga);
            return;
        }
        try {
            parser.readVR(SAISON, liga, MyApplication.selectedVerband);
            parser.readRR(SAISON, liga, MyApplication.selectedVerband);
            parser.readGesamtSpielplan(SAISON, liga, MyApplication.selectedVerband);
            if (liga.getName().contains("WDM"))
                return;

            softassertTrue("Mannschaften sind 0 " + liga.toString(), liga.getMannschaften().size() > 0);

            for (Mannschaft mannschaft : liga.getMannschaften()) {
                assertNotNull(mannschaft.toString(), mannschaft.getName());
                parser.readMannschaftsInfo(SAISON, mannschaft, MyApplication.selectedVerband);
                softassertTrue(mannschaft.toString(), mannschaft.getKontakt() != null);
            }
            if (liga.getUrlGesamt() == null) {
                softassertTrue(liga.getName(), liga.getSpieleVorrunde().size() > 0);
//                softassertTrue(liga.toString(), liga.getSpieleRueckrunde().size() > 0);
            } else {
                softassertTrue(liga.toString(), liga.getSpieleGesamt().size() > 0);
            }
            for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
                //                Log.i(Constants.LOG_TAG, "mannschaftspiel = " + mannschaftspiel);
                assertNotNull(mannschaftspiel);
                assertNotNull(mannschaftspiel.toString(), mannschaftspiel.getGastMannschaft());
                assertNotNull(mannschaftspiel.toString(), mannschaftspiel.getHeimMannschaft());
            }
            for (Mannschaftspiel mannschaftspiel : liga.getSpieleRueckrunde()) {
                assertNotNull(mannschaftspiel);
                assertNotNull(mannschaftspiel.getGastMannschaft());
                assertNotNull(mannschaftspiel.getHeimMannschaft());
            }
        } catch (NullPointerException e) {
            Log.e(Constants.LOG_TAG, "NPE in  " + liga, e);
        }
    }

    /**
     * sometime no games avaible on click-tt in RR or VR. strange
     * @param s reason
     * @param b to check
     */
    private void softassertTrue(String s, boolean b) {
        if (!b) {
            Log.e(Constants.LOG_TAG, "assert error '" + s + "'");
        }
    }
}
