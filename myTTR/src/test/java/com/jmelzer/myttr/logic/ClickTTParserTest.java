/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Tournament;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.model.Verein;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class ClickTTParserTest {

    ClickTTParser parser;
    final String ASSETS_DIR = "assets/clicktt";

    @Before
    public void setUp() throws Exception {
        parser = new ClickTTParser();
    }


    @Test
    public void testReadTopligen() throws Exception {
        String page = readFile(ASSETS_DIR + "/dttb-click-TT-Ligen.htm");
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
        }
        assertTrue("must be > 10 not " + ligen.size(), ligen.size() > 10);
    }

    @Test
    public void testBrandenBurg() throws Exception {
        String page = readFile(ASSETS_DIR + "/ttv-brandenburg.htm");
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
        }
        assertEquals("must be 8 not " + ligen.size(),8,  ligen.size());
    }

    @Test
    public void testReadTournaments() throws Exception {
        String page = readFile(ASSETS_DIR + "/turniere.htm");
        assertNotNull(page);
        List<Tournament> tournaments = parser.parseTournamentLinks(page);

        for (Tournament tournament : tournaments) {
            System.out.println("tournament = " + tournament);
        }
        assertEquals("must be 8 not " + tournaments.size(),8,  tournaments.size());
    }

    @Test
    public void testParseLigaLinks() throws Exception {
        String page = readFile(ASSETS_DIR + "/dttb-click-TT-Ligen.htm");
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
        }
        assertEquals("must be 34 ", 34, ligen.size());
        assertEquals(17, count(ligen, "Herren"));
        assertEquals(17, count(ligen, "Damen"));
        page = readFile(ASSETS_DIR + "/liga-level2-wttv.htm");
        assertNotNull(page);
        ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
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

        page = readFile(ASSETS_DIR + "/liga-level2-bremen.htm");
        assertNotNull(page);
        ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
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
        String page = readFile(ASSETS_DIR + "/dttb-click-TT-Ligen.htm");
        assertNotNull(page);
        List<Verband> verbandList = parser.parseLinksSubLigen(page);
        for (Verband verband : verbandList) {
            System.out.println("verband = " + verband);
        }
        assertEquals("must be 11 ", 11, verbandList.size());
    }


    @Test
    public void testReadStaffel() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-after-215.htm");
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);

        assertNotNull(liga.getUrlVR());
        assertNotNull(liga.getUrlRR());

        for (Mannschaft m : liga.getMannschaften()) {
            System.out.println("m = " + m);
        }
    }

    @Test
    public void testReadStaffelHttv() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-hl-httv.htm");
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);

        for (Mannschaft m : liga.getMannschaften()) {
            System.out.println("m = " + m);
        }
    }

    @Test
    public void testReadStaffeErrorIntl() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-2.bl-d.htm");
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setVerband(new Verband("", "http://wttv.click-tt.de/"));
        liga = parser.parseLiga(liga, page);

        for (Mannschaft m : liga.getMannschaften()) {
            System.out.println("m = " + m);
        }
    }

    @Test
    public void testReadStaffelGesamtSpielPlan() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-gesamt-spiel-plan.htm");
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setVerband(new Verband("", "http://wttv.click-tt.de/"));
        liga = parser.parseLiga(liga, page);
        System.out.println("liga.getUrlGesamt() = " + liga.getUrlGesamt());
        assertNotNull(liga.getUrlGesamt());
    }

    @Test
    public void testReadStaffeErrorZ() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-zurueckgezogene-mannschaft.htm");
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setVerband(new Verband("", "http://wttv.click-tt.de/"));
        liga = parser.parseErgebnisse(liga, page, Liga.Spielplan.VR);

        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel);
            assertNotNull(mannschaftspiel.toString(), mannschaftspiel.getGastMannschaft());
            assertNotNull(mannschaftspiel.toString(), mannschaftspiel.getHeimMannschaft());
        }
    }

    @Test
    public void testParseErgebnisse() throws Exception {
        Liga liga = ergebnisse();

        assertEquals(45, liga.getSpieleVorrunde().size());
        assertEquals(45, liga.getSpieleRueckrunde().size());
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            System.out.println("sp = " + mannschaftspiel);
            if (mannschaftspiel.getErgebnis() != null) {
                assertNotNull(mannschaftspiel.getUrlDetail());
            }
            assertNotNull(mannschaftspiel.getHeimMannschaft());
            assertNotNull(mannschaftspiel.getGastMannschaft());
        }
    }

    Liga ergebnisse() throws IOException {
        String page = readFile(ASSETS_DIR + "/staffel-after-215.htm");
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setUrl("http://bla.de");
        liga = parser.parseLiga(liga, page);
        page = readFile(ASSETS_DIR + "/spiele.htm");
        parser.parseErgebnisse(liga, page, Liga.Spielplan.VR);
        page = readFile(ASSETS_DIR + "/spiele-rr.htm");
        parser.parseErgebnisse(liga, page, Liga.Spielplan.RR);
        return liga;
    }

    @Test
    public void testParseMannschaftspiel() throws Exception {
        Liga liga = ergebnisse();
        String page = readFile(ASSETS_DIR + "/mannschafts-spiel.htm");
        assertTrue(liga.getSpieleVorrunde().size() > 0);
        Mannschaftspiel mannschaftspiel = liga.getSpieleVorrunde().get(0);
        parser.parseMannschaftspiel(page, mannschaftspiel);
        System.out.println("sp = " + mannschaftspiel);
        for (Spielbericht spielbericht : mannschaftspiel.getSpiele()) {
            System.out.println("spielbericht = " + spielbericht);
            assertNotEquals(spielbericht.getSpieler1Name(), spielbericht.getSpieler2Name());
        }
    }

    @Test
    public void testUnencodeMail() throws Exception {
        assertEquals("manfred.und.petra.hildebrandt@t-online.de", parser.unencodeMail("'de', 'manfred', 't-online', 'und.petra.hildebrandt'"));
        assertEquals("m.pfender@gmx.de", parser.unencodeMail("'de', 'm', 'gmx', 'pfender'"));
        assertEquals("bundesliga@borussia-duesseldorf.com", parser.unencodeMail("'com', 'bundesliga', 'borussia-duesseldorf', ''"));

    }

    @Test
    public void testMannschaftDetail() throws Exception {
        String page = readFile(ASSETS_DIR + "/mannschafts-detail.htm");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseDetail(page, mannschaft);
        assertEquals("Hildebrandt, Manfred\nTel.: 02241 314799", mannschaft.getKontakt());
        assertEquals("manfred.und.petra.hildebrandt@t-online.de", mannschaft.getMailTo());
        assertNotNull(mannschaft.getVereinUrl());
        List<Mannschaft.SpielerBilanz> bilanzen = mannschaft.getSpielerBilanzen();
        for (Mannschaft.SpielerBilanz spielerBilanz : bilanzen) {
            System.out.println("spielerBilanz = " + spielerBilanz);
            for (String[] strings : spielerBilanz.getPosResults()) {
                System.out.println(strings[0] + " : " + strings[1]);
            }
        }
    }

    @Test
    public void testMannschaftDetailBezirkDonau() throws Exception {
        String page = readFile(ASSETS_DIR + "/mannschafts-detail-donau.htm");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseDetail(page, mannschaft);

        List<Mannschaft.SpielerBilanz> bilanzen = mannschaft.getSpielerBilanzen();
        for (Mannschaft.SpielerBilanz spielerBilanz : bilanzen) {
            System.out.println("spielerBilanz = " + spielerBilanz);
            for (String[] strings : spielerBilanz.getPosResults()) {
                System.out.println(strings[0] + " : " + strings[1]);
            }
        }
    }

    @Test
    public void testMannschaftDetailSpielLokale() throws Exception {
        String page = readFile(ASSETS_DIR + "/mannschafts_detail_niederkassel.htm");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseDetail(page, mannschaft);
        List<String> lokale = mannschaft.getSpielLokale();
        for (String s : lokale) {
            System.out.println("s = " + s);
        }
        assertEquals(3, lokale.size());
        assertEquals("Spiellokal 1: Sporthalle Nord\nEingang vom Parkplatz an der Premnitzer Straße, 53859 Niederkassel-Lülsdorf",
                lokale.get(0));
        assertEquals("Spiellokal 2: Sporthalle Süd\nEifelstr., 53859 Niederkassel-Mondorf",
                lokale.get(1));
        assertEquals("Spiellokal 3: Turnhalle GS Rheidt\nHoher Rain, 53859 Niederkassel-Rheidt",
                lokale.get(2));
    }

    @Test
    public void testcleanupSpielLokalHtml() throws Exception {
        assertEquals("Spiellokal 1: Sporthalle Nord\nBla blubP remnitzer Straße, 53859 Niederkassel-Lülsdorf",
                parser.cleanupSpielLokalHtml("Spiellokal 1:</b>              Sporthalle Nord<br />Bla blubP remnitzer Straße, 53859 Niederkassel-Lülsdorf              <br />"));

    }

    @Test
    public void testParseSpieler() throws Exception {

        String page = readFile(ASSETS_DIR + "/spielerportrait.htm");
        assertNotNull(page);
        Spieler spieler = parser.parseSpieler("Fritz, Heinz", page);
        assertEquals("Fritz, Heinz", spieler.getName());
        assertEquals("SSF Bonn 1905 e.V.", spieler.getClubName());
        assertEquals("Herren: VR 1.2 RR 1.3\n" +
                "Jungen: VR 1.1 RR 1.1", spieler.getMeldung());

        assertEquals(2, spieler.getEinsaetze().size());
        for (Spieler.Einsatz einsatz : spieler.getEinsaetze()) {
            System.out.println("einsatz = " + einsatz);
        }
        Spieler.Einsatz einsatz = spieler.getEinsaetze().get(0);
        assertEquals("Herren", einsatz.getKategorie());
        assertEquals("Herren-Bezirksliga 2", einsatz.getLigaName());
        assertTrue(einsatz.getUrl().startsWith("/cgi-bin"));
        einsatz = spieler.getEinsaetze().get(1);
        assertEquals("Jungen", einsatz.getKategorie());
        assertEquals("Jungen-Verbandsliga 4", einsatz.getLigaName());
        assertTrue(einsatz.getUrl().startsWith("/cgi-bin"));

        assertEquals(2, spieler.getBilanzen().size());
        for (Spieler.Bilanz bilanz : spieler.getBilanzen()) {
            System.out.println("bilanz = " + bilanz);
        }

        List<Spieler.LigaErgebnisse> ergebnisse = spieler.getErgebnisse();
//        for (Spieler.LigaErgebnisse ligaErgebnisse : ergebnisse) {
//            System.out.println("ligaErgebnisse = " + ligaErgebnisse);
//        }
    }

    @Test
    public void testParseLinksBezirke() throws Exception {
        String page = readFile(ASSETS_DIR + "/liga-level2-wttv.htm");
        assertNotNull(page);
        List<Bezirk> bezirke = parser.parseLinksBezirke(page);
        assertNotNull(bezirke);
        assertEquals(5, bezirke.size());
//        for (Bezirk bezirk : bezirke) {
//            System.out.println("bezirk = " + bezirk);
//        }

        page = readFile(ASSETS_DIR + "/ttvwh-ligen-no-bezirk.htm");
        assertNotNull(page);
        bezirke = parser.parseLinksBezirke(page);
        assertNotNull(bezirke);
        assertEquals(15, bezirke.size());
//        for (Bezirk bezirk : bezirke) {
//            System.out.println("bezirk = " + bezirk);
//        }
    }

    @Test
    public void testParseVerein() throws Exception {
        String page = readFile(ASSETS_DIR + "/verein.htm");
        Verein verein = parser.parseVerein("", page);

        assertNotNull(verein);
        assertEquals("TTG St. Augustin", verein.getName());
        System.out.println("verein.getKontakt() = " + verein.getKontakt());
        assertEquals("erwin-franz@t-online.de", verein.getKontakt().getMail());
//        assertEquals("http://www.BorussiaTT.de", verein.getKontakt().getUrl());
        System.out.println("-------------");
        for (Verein.SpielLokal s : verein.getLokale()) {
            System.out.println("s = " + s);
        }
        System.out.println("-------------");
        assertEquals(1, verein.getLokale().size());
        assertTrue(verein.getLokale().get(0).text.contains("Sportzentrum Menden"));
        assertEquals("Sankt Augustin", verein.getLokale().get(0).city);
        assertEquals("53757", verein.getLokale().get(0).plz);
        assertEquals("Siegstraße 119", verein.getLokale().get(0).street);

        for (Mannschaftspiel mannschaftspiel : verein.getLetzteSpiele()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
        }
    }

    @Test
    public void testParseVereinMannschaften() throws Exception {
        String page = readFile(ASSETS_DIR + "/verein-mannschaften.htm");
        Verein verein = new Verein();
        assertEquals(0, verein.getMannschaften().size());

        parser.parseVereinMannschaften(page, verein);
        assertEquals(11, verein.getMannschaften().size());
        for (Verein.Mannschaft mannschaft : verein.getMannschaften()) {
            assertNotNull(mannschaft.liga);
            assertNotNull(mannschaft.name);
            assertNotNull(mannschaft.url);
            System.out.println("mannschaft = " + mannschaft);
        }

        page = readFile(ASSETS_DIR + "/verein-mannschaften-2.htm");
        verein = new Verein();
        assertEquals(0, verein.getMannschaften().size());

        parser.parseVereinMannschaften(page, verein);
        assertEquals(4, verein.getMannschaften().size());
        for (Verein.Mannschaft mannschaft : verein.getMannschaften()) {
            assertNotNull(mannschaft.liga);
            assertNotNull(mannschaft.name);
            assertNotNull(mannschaft.url);
            System.out.println("mannschaft = " + mannschaft);
        }
    }

    @Test
    public void testParseVereinDD() throws Exception {
        String page = readFile(ASSETS_DIR + "/verein-dd.htm");
        Verein verein = parser.parseVerein("", page);

        assertNotNull(verein);
        assertEquals("Borussia Düsseldorf", verein.getName());
        System.out.println("verein.getKontakt() = " + verein.getKontakt());
        assertEquals("info@borussia-duesseldorf.com", verein.getKontakt().getMail());
        assertEquals("http://www.borussia-duesseldorf.com", verein.getKontakt().getUrl());
        System.out.println("-------------");
        for (Verein.SpielLokal s : verein.getLokale()) {
            System.out.println("s = " + s);
            assertFalse(s.text.contains("script"));
        }
        System.out.println("-------------");
        assertEquals(1, verein.getLokale().size());
        assertTrue(verein.getLokale().get(0).text.contains("ARAG "));

        assertEquals(1, verein.getLetzteSpiele().size());
        Mannschaftspiel mannschaftspiel = verein.getLetzteSpiele().get(0);
        assertEquals("TTC RhönSprudel Fulda-Maberzell", mannschaftspiel.getHeimMannschaft().getName());
        assertEquals("Borussia Düsseldorf", mannschaftspiel.getGastMannschaft().getName());
        assertEquals("1:3", mannschaftspiel.getErgebnis());
        assertNotNull(mannschaftspiel.getUrlDetail());

        assertNotNull(verein.getUrlMannschaften());
    }
}


