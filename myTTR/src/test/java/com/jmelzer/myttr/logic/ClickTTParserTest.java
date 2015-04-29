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
import com.jmelzer.myttr.Verband;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class ClickTTParserTest {

    ClickTTParser parser;
    final String ASSETS_DIR = "C:\\ws\\myTTR-master\\myTTR\\src\\androidTest\\assets\\clicktt";

    @Before
    public void setUp() throws Exception {
        parser = new ClickTTParser();
    }



    @Test
    public void testReadTopligen() throws Exception {
        String page = readFile(ASSETS_DIR + "/dttb-click-TT-Ligen.htm", true);
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println( "liga = " + liga);
        }
        assertTrue("must be > 10 not " + ligen.size(), ligen.size() > 10);
    }

    @Test
    public void testParseLigaLinks() throws Exception {
        String page = readFile(ASSETS_DIR + "/dttb-click-TT-Ligen.htm", true);
        assertNotNull(page);
        List<Liga> ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println( "liga = " + liga);
        }
        assertEquals("must be 34 ", 34, ligen.size());
        assertEquals(17, count(ligen, "Herren"));
        assertEquals(17, count(ligen, "Damen"));
        page = readFile(ASSETS_DIR + "/liga-level2-wttv.htm", true);
        assertNotNull(page);
        ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println( "liga = " + liga);
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

        page = readFile(ASSETS_DIR + "/liga-level2-bremen.htm", true);
        assertNotNull(page);
        ligen = parser.parseLigaLinks(page);

        for (Liga liga : ligen) {
            System.out.println( "liga = " + liga);
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
        String page = readFile(ASSETS_DIR + "/dttb-click-TT-Ligen.htm", true);
        assertNotNull(page);
        List<Verband> verbandList = parser.parseLinksSubLigen(page);
        for (Verband verband : verbandList) {
            System.out.println( "verband = " + verband);
        }
        assertEquals("must be 11 ", 11, verbandList.size());
    }


    @Test
    public void testReadStaffel() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel.htm", true);
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);

        for (Mannschaft m : liga.getMannschaften()) {
            System.out.println( "m = " + m);
        }
    }

    @Test
    public void testReadStaffelHttv() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-hl-httv.htm", true);
        assertNotNull(page);
        Liga liga = new Liga();
        liga = parser.parseLiga(liga, page);

        for (Mannschaft m : liga.getMannschaften()) {
            System.out.println( "m = " + m);
        }
    }

    @Test
    public void testReadStaffeErrorIntl() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-2.bl-d.htm", true);
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
        String page = readFile(ASSETS_DIR + "/staffel-gesamt-spiel-plan.htm", true);
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setVerband(new Verband("", "http://wttv.click-tt.de/"));
        liga = parser.parseLiga(liga, page);
        System.out.println("liga.getUrlGesamt() = " + liga.getUrlGesamt());
        assertNotNull(liga.getUrlGesamt());
    }

    @Test
    public void testReadStaffeErrorZ() throws Exception {
        String page = readFile(ASSETS_DIR + "/staffel-zurueckgezogene-mannschaft.htm", true);
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setVerband(new Verband("", "http://wttv.click-tt.de/"));
        liga = parser.parseErgebnisse(liga, page, Liga.Spielplan.VR);

        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel);
            assertNotNull(mannschaftspiel.toString(),mannschaftspiel.getGastMannschaft());
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
            if (mannschaftspiel.getErgebnis() != null)
                assertNotNull(mannschaftspiel.getUrlDetail());
            assertNotNull(mannschaftspiel.getHeimMannschaft());
            assertNotNull(mannschaftspiel.getGastMannschaft());
        }
    }

    Liga ergebnisse() throws IOException {
        String page = readFile(ASSETS_DIR + "/staffel.htm", true);
        assertNotNull(page);
        Liga liga = new Liga();
//        liga.setUrl("http://bla.de");
        liga = parser.parseLiga(liga, page);
        page = readFile(ASSETS_DIR + "/spiele.htm", true);
        parser.parseErgebnisse(liga, page, Liga.Spielplan.VR);
        page = readFile(ASSETS_DIR + "/spiele-rr.htm", true);
        parser.parseErgebnisse(liga, page, Liga.Spielplan.RR);
        return liga;
    }

    @Test
    public void testParseMannschaftspiel() throws Exception {
        Liga liga = ergebnisse();
        String page = readFile(ASSETS_DIR + "/mannschafts-spiel.htm", true);
        assertTrue(liga.getSpieleVorrunde().size() > 0);
        Mannschaftspiel mannschaftspiel = liga.getSpieleVorrunde().get(0);
        parser.parseMannschaftspiel(page, mannschaftspiel);
        System.out.println( "sp = " + mannschaftspiel);
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
        String page = readFile(ASSETS_DIR + "/mannschafts-detail.htm", true);
        Mannschaft mannschaft = new Mannschaft();
        parser.parseDetail(page, mannschaft);
        assertEquals("Hildebrandt, Manfred\nTel.: 02241 314799", mannschaft.getKontakt());
        assertEquals("manfred.und.petra.hildebrandt@t-online.de", mannschaft.getMailTo());

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
        String page = readFile(ASSETS_DIR + "/mannschafts-detail-donau.htm", true);
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
        String page = readFile(ASSETS_DIR + "/mannschafts_detail_niederkassel.htm", true);
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

        String page = readFile(ASSETS_DIR + "/spielerportrait.htm", true);
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
        for (Spieler.LigaErgebnisse ligaErgebnisse : ergebnisse) {
            System.out.println("ligaErgebnisse = " + ligaErgebnisse);
        }
    }
    @Test
    public void testParseLinksBezirke() throws Exception {
        String page = readFile(ASSETS_DIR + "/liga-level2-wttv.htm", true);
        assertNotNull(page);
        List<Bezirk> bezirke = parser.parseLinksBezirke(page);
        assertNotNull(bezirke);
        assertTrue(bezirke.size() > 0);
        for (Bezirk bezirk : bezirke) {
            System.out.println("bezirk = " + bezirk);
        }

        page = readFile(ASSETS_DIR + "/ttvwh-ligen-no-bezirk.htm", true);
        assertNotNull(page);
        bezirke = parser.parseLinksBezirke(page);
        assertNotNull(bezirke);
        assertTrue(bezirke.size() > 0);
        for (Bezirk bezirk : bezirke) {
            System.out.println("bezirk = " + bezirk);
        }
    }

}


