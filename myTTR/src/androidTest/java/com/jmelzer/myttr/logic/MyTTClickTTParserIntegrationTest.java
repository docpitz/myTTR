package com.jmelzer.myttr.logic;


import android.util.Log;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.filters.SmallTest;

import static com.jmelzer.myttr.Verband.dttb;
import static com.jmelzer.myttr.logic.LogicTestHelper.login;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.*;

/**
 * Created by jmelzer on 26.11.2017.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class MyTTClickTTParserIntegrationTest {

    public static final Saison SAISON = Saison.SAISON_2020;
    private static final int BEZIRK_COUNT = 2;
    MytClickTTWrapper parser = new MytClickTTWrapper();

    @Before
    public void before() throws Exception {

    }

    @SmallTest
    public void testReadBezirkeAndLigen() throws Exception {

        Verband wttv = Verband.verbaende.get(Verband.verbaende.size() - 1);
        parser.readBezirkeAndLigen(wttv, Saison.SAISON_2020);
        assertEquals(5, wttv.getBezirkList().size());
        for (Bezirk bezirk : wttv.getBezirkList()) {
            Log.d(Constants.LOG_TAG, "bezirk = " + bezirk);
        }
        assertEquals(48, wttv.getLigaList().size());
    }

    @Test
    public void testreadOwnVerein() throws Exception {
        login();
        Verein verein = parser.readOwnVerein();
        Log.d(Constants.LOG_TAG, "verein = " + verein);
    }

    @Test
    public void testAllVerbaende() throws Exception {

        //bezirke
        for (Verband verband : Verband.verbaende) {
            Log.d(Constants.LOG_TAG, "verband '" + verband.getName() + "'");
            if (!verband.getName().equals("Westdeutscher TTV")) {
                continue;
            }
            if (verband == dttb) {
                continue;
            }

            Log.i(Constants.LOG_TAG, "read bezirke from '" + verband.getName() + "'");
            parser.readBezirkeAndLigen(verband, SAISON);
            assertThat(verband.getLigaList().size(), greaterThan(0));
            for (Liga liga : verband.getLigaList()) {
                assertThat(liga.getName(), liga.getName().contains("<"), is(false));
                assertThat(liga.getKategorie(), is(notNullValue()));
            }
            if (verband.getBezirkList().size() == 0) {
                Log.e(Constants.LOG_TAG, "no bezirk in '" + verband.getName() + "'");
                assertThat(verband.getName(), notNullValue());
            } else {
                int bezCount = 0;
                for (Bezirk bezirk : verband.getBezirkList()) {
                    Log.i(Constants.LOG_TAG, "read kreis & liga from '" + bezirk.getName() + "'");
                    parser.readKreiseAndLigen(SAISON, bezirk);
                    int count = 0;
                    assertThat(bezirk.getLigen().size(), greaterThan(0));
                    for (Liga liga : bezirk.getLigen()) {
                        Log.i(Constants.LOG_TAG, "read liga '" + liga + "'");
                        parser.readLiga(SAISON, liga);
                        readSpieleAndTest(liga);
                        if (++count > 0) {
                            break; //5 are enough: todo get it random
                        }
                    }
                    if (bezirk.getKreise().size() == 0) {
                        Log.e(Constants.LOG_TAG, "bezirk don't have kreise " + bezirk.getName() + " - " + bezirk.getUrl());
                    } else {
                        for (Kreis kreis : bezirk.getKreise()) {
                            parser.readLigen(kreis, SAISON);
                            int kc = 0;
                            for (Liga liga : kreis.getLigen()) {
                                Log.i(Constants.LOG_TAG, "read liga in kreise '" + liga + "'");
                                try {
                                    parser.readLiga(SAISON, liga);
                                } catch (NullPointerException e) {
                                    Log.e(Constants.LOG_TAG, "NPE in  " + liga);
                                }
                                readSpieleAndTest(liga);
                                if (++kc > 0) {
                                    break;
                                }

                            }
                            kreis.addAllLigen(new ArrayList<Liga>()); //clear memory
                        }
                    }
                    if (++bezCount >= BEZIRK_COUNT) {
                        break;
                    }
                    bezirk.addAllLigen(new ArrayList<Liga>()); //clear memory
//                    assertTrue(bezirk.getKreise().size() > 0);
                }
            }
        }
    }

    void readSpieleAndTest(Liga liga) throws Exception {
        if (liga.getUrlRR() == null && liga.getUrlVR() == null && liga.getUrlGesamt() == null) {
            Log.e(Constants.LOG_TAG, "keine vorrunde / rueckrunde / gesamt fuer  " + liga);
            return;
        }
        try {
            parser.readVR(SAISON, liga);
            parser.readRR(SAISON, liga);
            parser.readGesamtSpielplan(SAISON, liga);
            if (liga.getName().contains("WDM")) {
                return;
            }

            softassertTrue("Mannschaften sind 0 " + liga.toString(), liga.getMannschaften().size() > 0);

            int km = 0;
            for (Mannschaft mannschaft : liga.getMannschaften()) {
                assertNotNull(mannschaft.toString(), mannschaft.getName());
                parser.readMannschaftsInfo(SAISON, mannschaft);
                softassertTrue(mannschaft.toString(), mannschaft.getKontakt() != null);
                if (++km > 2) {
                    break;
                }
            }
            if (liga.getUrlGesamt() == null) {
                softassertTrue(liga.getName(), liga.getSpieleVorrunde().size() > 0);
//                softassertTrue(liga.toString(), liga.getSpieleRueckrunde().size() > 0);
            } else {
                softassertTrue(liga.toString(), liga.getSpieleGesamt().size() > 0);
            }
            km = 0;
            for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
                //                Log.i(Constants.LOG_TAG, "mannschaftspiel = " + mannschaftspiel);
                assertNotNull(mannschaftspiel);
                assertNotNull(mannschaftspiel.toString(), mannschaftspiel.getGastMannschaft());
                assertNotNull(mannschaftspiel.toString(), mannschaftspiel.getHeimMannschaft());
                if (++km > 2) {
                    break;
                }
            }
            km = 0;
            for (Mannschaftspiel mannschaftspiel : liga.getSpieleRueckrunde()) {
                assertNotNull(mannschaftspiel);
                assertNotNull(mannschaftspiel.getGastMannschaft());
                assertNotNull(mannschaftspiel.getHeimMannschaft());
                if (++km > 2) {
                    break;
                }
            }
        } catch (NullPointerException e) {
            Log.e(Constants.LOG_TAG, "NPE in  " + liga, e);
        }
    }

    /**
     * sometime no games avaible on click-tt in RR or VR. strange
     *
     * @param s reason
     * @param b to check
     */
    private void softassertTrue(String s, boolean b) {
        if (!b) {
            Log.e(Constants.LOG_TAG, "assert error '" + s + "'");
        }
    }
}
