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
import com.jmelzer.myttr.Competition;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.Participant;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Tournament;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.model.Saison;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jmelzer.myttr.model.Saison.ACTUAL_SAISON;

public class ClickTTParserIntegrationTest extends BaseTestCase {

    ClickTTParser parser;

    @Override
    protected void setUp() throws Exception {
        parser = new ClickTTParser();
        prepareMocks();
    }

    @Override
    protected void prepareMocks() {
        super.prepareMocks();
        MockResponses.forRequestDoAnswer(".*championship=DTTB.14/15", "clicktt/dttb_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*championship=WTTV.*14/15", "clicktt/wttv_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*meeting=7371117.*", "clicktt/mannschafts_detail_kreisliga.htm");
        MockResponses.forRequestDoAnswer(".*championship=B15.14%2F15", "clicktt/mittelrhein_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*displayTyp=vorrunde.*", "clicktt/kreisliga_vorrunde.htm");
        MockResponses.forRequestDoAnswer(".*championship=K156.14%2F15", "clicktt/rhein_sieg_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*championship=K156.14%2F15&group=225345", "clicktt/kreisliga.htm");
        MockResponses.forRequestDoAnswer(".*person=974254.*", "clicktt/player_974254.htm");
        MockResponses.forRequestDoAnswer(".*RL-OL.West.*", "clicktt/regional_west.htm");
        MockResponses.forRequestDoAnswer(".*pttv.click-tt.de.*", "clicktt/pttvmain.htm");
    }

    @SmallTest
    public void testReadIntegration() throws NetworkException {
        List<Verband> verbaende = parser.readVerbaende();
        assertEquals(13, verbaende.size());
        Verband v = verbaende.get(0);
        assertNotNull(v);
        assertSame(Verband.dttb, v);

        //first one is dttp
        Verband verband = parser.readTopLigen(ACTUAL_SAISON);
        assertSame(Verband.dttb, verband);
        assertEquals("must be 62 ", 62, verband.getLigaList().size());

        //read ligen for one verband
        Verband nrw = verbaende.get(verbaende.size() - 1);
        parser.readLigen(nrw, Saison.SAISON_2017);
        assertTrue("must be greater than 20, we don't exactly the size",
                nrw.getLigaList().size() > 20);

        parser.readBezirke(nrw, ACTUAL_SAISON);
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
        //todo better to find by name here
        liga = verband.getLigaList().get(8);
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
        parser.readDetail(spiel);

        assertTrue(spiel.getSpiele().size() > 0);
    }

    //    @MediumTest
    public void testAllVerbaende() throws Exception {
        List<Verband> verbaende = parser.readVerbaende();
        for (Verband verband : verbaende) {
            parser.readLigen(verband, Saison.SAISON_2018);
            assertTrue(verband.toString(), verband.getLigaList().size() > 0);
        }

//        Verband dttb = testDTTB();  //allready ok, uncomment if you want to test it
        Verband dttb = Verband.dttb;

        //bezirke
        for (Verband verband : verbaende) {

//            readLigenAndTest(verband); //allready ok, uncomment if you want to test it
            if (verband.getName().equals("Saarländischer TTB")) {
                continue;
            }
//            if (verband == dttb) {
//                continue;
//            }

            Log.i(Constants.LOG_TAG, "read bezirke from '" + verband.getName() + "'");
            parser.readBezirke(verband, Saison.SAISON_2015);
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
                        readSpieleAndTest(liga);
                    }
                    if (bezirk.getKreise().size() == 0) {
                        Log.e(Constants.LOG_TAG, "bezirk don't have kreise " + bezirk.getName() + " - " + bezirk.getUrl());
                    } else {
                        for (Kreis kreis : bezirk.getKreise()) {
                            parser.readLigen(kreis);
                            for (Liga liga : kreis.getLigen()) {
                                Log.i(Constants.LOG_TAG, "read liga in kreise '" + liga + "'");
                                try {
                                    parser.readLiga(liga);
                                } catch (NullPointerException e) {
                                    Log.e(Constants.LOG_TAG, "NPE in  " + liga);
                                }
                                readSpieleAndTest(liga);

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

    private Verband testDTTB() throws NetworkException {
        Verband dttb = parser.readTopLigen(ACTUAL_SAISON);
        parser.readLigen(dttb, ACTUAL_SAISON);
        for (Liga liga : dttb.getLigaList()) {
            Log.i(Constants.LOG_TAG, "read liga '" + liga.getNameForFav() + "'");
            parser.readLiga(liga);
            Log.i(Constants.LOG_TAG, "liga mannschaften = " + liga.getMannschaften().size());
            if (liga.getMannschaften().size() == 0) {
                Log.e(Constants.LOG_TAG, "no liga mannschaften in " + liga);
            }
            readSpieleAndTest(liga);
        }
        return dttb;
    }

    void readSpieleAndTest(Liga liga) throws NetworkException {
        if (liga.getUrlRR() == null && liga.getUrlVR() == null && liga.getUrlGesamt() == null) {
            Log.e(Constants.LOG_TAG, "keine vorrunde / rueckrunde / gesamt fuer  " + liga);
            return;
        }
        try {
            parser.readVR(liga);
            parser.readRR(liga);
            parser.readGesamtSpielplan(liga);
            assertTrue(liga.toString(), liga.getMannschaften().size() > 0);

            for (Mannschaft mannschaft : liga.getMannschaften()) {
                assertNotNull(mannschaft.getName());
            }
            if (liga.getUrlGesamt() == null) {
                softassertTrue(liga.toString(), liga.getSpieleVorrunde().size() > 0);
                softassertTrue(liga.toString(), liga.getSpieleRueckrunde().size() > 0);
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

    private void softassertTrue(String s, boolean b) {
        if (!b) {
            Log.e(Constants.LOG_TAG, "assert error '" + s + "'");
        }
    }

    void readLigenAndTest(Verband verband) throws NetworkException {
        Log.i(Constants.LOG_TAG, "read ligen from '" + verband.getName() + "'");
        parser.readLigen(verband, Saison.SAISON_2015);
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

    @SmallTest
    public void testSpielerDetail() throws Exception {
        Spieler spieler = parser.readSpielerDetail("Mykietyn, Tom ", "http://wttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/playerPortrait?federation=RL-OL+West&season=2016%2F17&person=1150840&club=8383");
        assertEquals("Herren: VR 1.4 RR 1.2\n" +
                "Jungen: VR 1.1 RR 1.1", spieler.getPosition());
        assertEquals(1, spieler.getEinsaetze().size());
        List<Spieler.LigaErgebnisse> ergebnisse = spieler.getErgebnisse();
        assertTrue(ergebnisse.size() > 0);
//        for (Spieler.LigaErgebnisse ligaErgebnisse : ergebnisse) {
//            Log.i(Constants.LOG_TAG, "ligaErgebnisse = " + ligaErgebnisse);
//        }

    }

    @SmallTest
    public void testTurnier() throws Exception {
        List<Verband> verbaende = Verband.verbaendeWithTournaments();
        for (Verband verband : verbaende) {
            List<Tournament> tournaments = parser.readTournaments(verband, new Date());
            for (Tournament tournament : tournaments) {
                parser.readTournamentDetail(tournament);
                for (Competition competition : tournament.getCompetitions()) {
                    parser.readTournamentParticipants(competition);
                    for (Participant participant : competition.getParticipantList()) {
                        assertNotNull(participant.getName());
                        assertNotNull(participant.getClub());
                        assertNotNull(participant.getQttr());
                    }
                    parser.readTournamentResults(competition);

                }
            }
        }
    }
    @SmallTest
    public void testOneCup() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setCup(true);
        tournament.setUrl("https://ttvn.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendarDetail?circuit=TTVN-Race&federation=TTVN&tournament=293968&date=2017-08-01");
        parser.readTournamentDetail(tournament);
        for (Competition competition : tournament.getCompetitions()) {
            parser.readTournamentResults(competition);
            if (competition.getResults() != null) {
                assertTrue(competition.getGroups().size()>0);
            }
        }
    }
    @SmallTest
    public void testCups() throws Exception {
        List<Verband> verbaende = Verband.cups();
        for (Verband verband : verbaende) {
            List<Tournament> tournaments = parser.readCups(verband, new Date());
            for (Tournament tournament : tournaments) {
                parser.readTournamentDetail(tournament);
                for (Competition competition : tournament.getCompetitions()) {
                    parser.readTournamentParticipants(competition);
                    for (Participant participant : competition.getParticipantList()) {
//                        assertNotNull(participant.getPlace());
//                        assertNotNull(participant.getBilanz());
                        assertNotNull(participant.getName());
                        assertNotNull(participant.getClub());
                        assertNotNull(participant.getQttr());
                    }
                    parser.readTournamentResults(competition);
                    if (competition.getResults() != null) {
                        if(competition.getGroups().size()==0)
                            System.out.println("break");
                        assertTrue(competition.getGroups().size()>0);
                    }
                }
            }
        }
    }
}


