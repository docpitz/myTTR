package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.SpielerAndBilanz;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.TestUtil;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.Verein;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MyTTClickTTParserTest {
    final String ASSETS_DIR = "assets/mytt/clicktt";

    MyTTClickTTParserImpl parser = new MyTTClickTTParserImpl();

    @Test
    public void testparseLigaLinks() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-ligenplan.html");
        List<Liga> ligen = parser.parseLigaLinks(page);
        for (Liga liga : ligen) {
            assertNotNull(liga.getName());
            assertNotNull(liga.getUrl());
//            System.out.println("liga = " + liga);
        }
    }

    @Test
    public void parseLinksBezirke() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-bezirke.html");
        List<Bezirk> bezirkList = parser.parseLinksBezirke(page);
        for (Bezirk bezirk : bezirkList) {
            System.out.println("bezirk = " + bezirk);
        }
    }

    @Test
    public void parseLiga() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-liga.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/gesamt");
        parser.parseLiga(page, liga);
        assertEquals(12, liga.getMannschaften().size());
        for (Mannschaft mannschaft : liga.getMannschaften()) {
//            System.out.println("mannschaft = " + mannschaft);
            assertNotNull(mannschaft.getName());
            assertNotNull(mannschaft.getUrl());
            assertNotNull(mannschaft.getGameStatistic());
            assertNotNull(mannschaft.getLiga());
        }
    }

    @Test(expected = ValidationException.class)
    public void parseLigaPlayOff() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/playoff.html");
        Liga liga = new Liga("Bundesliga Playoof", "https://www.mytischtennis.de/clicktt/DTTB/18-19/ligen/TTBL-Play-Offs/gruppe/350030/tabelle/gesamt");
        parser.parseLiga(page, liga);
    }

    @Test
    public void parseLigaLiveEdit() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/live-active.overview.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/gesamt");
        parser.parseErgebnisse(page, liga, Liga.Spielplan.RR);
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleRueckrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
        }
        assertEquals("0:0", liga.getSpieleRueckrunde().get(15).getErgebnis());
        assertEquals(11, liga.getMannschaften().size());
    }

    @Test
    public void bugParseDate() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/bug_parse_date.htm");
        Liga liga = new Liga("Kreisoberliga ", "https://www.mytischtennis.de/clicktt/TTVSA/19-20/ligen/Kreisoberliga/gruppe/361123/spielplan/gesamt");
        parser.parseErgebnisse(page, liga, Liga.Spielplan.RR);
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleRueckrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel.getDateAsDate());
        }
    }

    @Test
    public void bugParseDate2() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/spieplan-mit-verlegungen.htm");
        Liga liga = new Liga("Kreisoberliga ", "https://www.mytischtennis.de/clicktt/HeTTV/19-20/ligen/Bezirksklasse-Gr-1/gruppe/359162/spielplan/gesamt");
        parser.parseErgebnisse(page, liga, Liga.Spielplan.RR);
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleRueckrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel.getDateAsDate());
        }
    }
    @Test
    public void parseLigaHome() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/click-tt-home.html");
        Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home");
        parser.parseLiga(page, liga);

        assertNotNull(liga.getUrlVR());
        assertNotNull(liga.getUrlRR());

        assertEquals(12, liga.getMannschaften().size());
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            System.out.println("mannschaft = " + mannschaft);
            assertNotNull(mannschaft.getUrl());
            assertEquals("Can't get spiele from this page", 0, liga.getSpieleFor(mannschaft.getName(), Liga.Spielplan.VR).size());
            assertEquals(0, liga.getSpieleFor(mannschaft.getName(), Liga.Spielplan.RR).size());
        }
    }

    @Test
    public void parseLigaHometab_plan() throws Exception {
        Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home");
        String page = TestUtil.readFile(ASSETS_DIR + "/click-tt-home.html");
        parser.parseLiga(page, liga);

        //https://www.mytischtennis.de/clicktt/home-tab?id=plan
        String page2 = TestUtil.readFile(ASSETS_DIR + "/click-tt-home_tabplan.html");
        Liga.Spielplan spielplan = Liga.Spielplan.RR;
        if (page2.contains("Gruppenspielplan (Vorrunde)")) {
            spielplan = Liga.Spielplan.VR;
        }

        parser.parseErgebnisse(page2, liga, spielplan);

        assertNotNull(liga.getUrlVR());
        assertNotNull(liga.getUrlRR());
        String page3 = TestUtil.readFile(ASSETS_DIR + "/click-tt-home_tabplan_next_is_rr.html");
        parser.parseErgebnisse(page3, liga, Liga.Spielplan.RR);

        assertEquals(12, liga.getMannschaften().size());
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            System.out.println("mannschaft = " + mannschaft);
            assertNotNull(mannschaft.getUrl());
            assertEquals(11, liga.getSpieleFor(mannschaft.getName(), Liga.Spielplan.VR).size());
            assertEquals(11, liga.getSpieleFor(mannschaft.getName(), Liga.Spielplan.RR).size());
        }
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            assertNotEquals(0, mannschaftspiel.getNrSpielLokal());

        }
    }

    @Test
//    @Ignore("need new file")
    public void parseErgebnisseNotComplete() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/result_nicht_komplett_eingegeben.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/aktuell");

        parser.parseErgebnisse(page, liga, Liga.Spielplan.RR);
        assertEquals(66, liga.getSpieleRueckrunde().size());
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            assertNotNull(mannschaftspiel.getDate());
            assertNotNull(mannschaftspiel.getHeimMannschaft());
            assertNotNull(mannschaftspiel.getGastMannschaft());
//            System.out.println("mannschaftspiel = " + mannschaftspiel);
        }
        Mannschaftspiel spiel = liga.getSpieleRueckrunde().get(6);
        assertThat(spiel.toString(), spiel.getHeimMannschaft().getName(), is("TTC Baesweiler"));
        assertThat(spiel.toString(), spiel.getGastMannschaft().getName(), is("TTC Karken"));
        assertThat(spiel.toString(), spiel.getErgebnis(), is("9:0"));
    }

    @Test
    public void parseLigaZurueckgezogen() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/liga-zurueckgezogen.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/gesamt");
        parser.parseLiga(page, liga);
        assertEquals(11, liga.getMannschaften().size());
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            System.out.println("mannschaft = " + mannschaft);
        }
    }

    @Test
    public void parseErgebnisse() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-spielplan.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/aktuell");

        parser.parseErgebnisse(page, liga, Liga.Spielplan.VR);
        assertEquals(66, liga.getSpieleVorrunde().size());
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel.getDate());
            assertFalse(mannschaftspiel.getDate(), mannschaftspiel.getDate().contains("<"));
        }
    }


    /**
     * Gruppenspielplan
     * https://www.mytischtennis.de/clicktt/WTTV/18-19/ligen/Landesliga-12/gruppe/334231/spielplan/gesamt
     */
    @Test
    public void parseErgebnisseGesamt() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-spielplan-gesamt.html");
        Liga liga = new Liga("Herren Landesliga 12",
                "https://www.mytischtennis.de/clicktt/WTTV/18-19/ligen/Landesliga-12/gruppe/334231/tabelle/gesamt");

        parser.parseErgebnisse(page, liga, Liga.Spielplan.GESAMT);
        assertEquals(132, liga.getSpieleGesamt().size());
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleGesamt()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel.getDate());
            assertTrue(mannschaftspiel.toString(), mannschaftspiel.getDate().contains(":"));
            assertFalse(mannschaftspiel.toString(), mannschaftspiel.getDate().contains("<"));
        }
    }

    @Test
    public void parseMannschaftsDetail() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-detail.html");
        Mannschaft mannschaft = new Mannschaft();
        mannschaft.setUrl("https://www.mytischtennis.de/clicktt/");
        parser.parseMannschaftsDetail(page, mannschaft);
        assertEquals("Palapys, Alexander", mannschaft.getKontakt());
        assertEquals("alex@palapys.de", mannschaft.getMailTo());
        assertEquals("017672640971", mannschaft.getKontaktNr());
        assertEquals("Lindnerschule\n" +
                "Lindnerstr. 220\n" +
                "46149 Oberhausen", mannschaft.getSpielLokale().get(0));
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/verein/148033/SC-Buschhausen/info",
                mannschaft.getVereinUrl());
    }

    @Test
    public void parseError() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/error.html");
        assertTrue(parser.parseError(page), parser.parseError(page).startsWith("Mytischtennis Meldung: Es konnte keine Verbindung"));
    }

    @Test
    public void parseBilanzen2() throws Exception {

        String page = TestUtil.readFile(ASSETS_DIR + "/Rosenheim.html");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseBilanzen(page, mannschaft);
    }

    @Test
    public void parseBilanzen() throws Exception {

        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-bilanzen.html");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseBilanzen(page, mannschaft);
        assertEquals(12, mannschaft.getSpielerBilanzen().size());
        assertEquals("2", mannschaft.getSpielerBilanzen().get(0).getEinsaetze());
        assertEquals("Intakoon, Nantawat", mannschaft.getSpielerBilanzen().get(0).getName());
        assertEquals("4:0", mannschaft.getSpielerBilanzen().get(0).getGesamt());
        assertEquals("2.1", mannschaft.getSpielerBilanzen().get(0).getPos());
        assertEquals(2, mannschaft.getSpielerBilanzen().get(0).getPosResults().size());
        assertEquals("1", mannschaft.getSpielerBilanzen().get(0).getPosResults().get(0)[0]);
        assertEquals("2:0", mannschaft.getSpielerBilanzen().get(0).getPosResults().get(0)[1]);
        assertEquals("2", mannschaft.getSpielerBilanzen().get(0).getPosResults().get(1)[0]);
        assertEquals("2:0", mannschaft.getSpielerBilanzen().get(0).getPosResults().get(1)[1]);
        for (SpielerAndBilanz spielerBilanz : mannschaft.getSpielerBilanzen()) {
            System.out.println("spielerBilanz = " + spielerBilanz);
        }
        assertEquals(4, mannschaft.getSpielerBilanzen().get(7).getPosResults().size());
    }

    @Test
    public void parseBilanzen4er() throws Exception {

        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-bilanzen-4er.html");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseBilanzen(page, mannschaft);
        assertEquals(9, mannschaft.getSpielerBilanzen().size());
        assertEquals(4, mannschaft.getSpielerBilanzen().get(5).getPosResults().size());
    }

    @Test
    public void parseMannschaftsDetail2() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-detail2.html");
        Mannschaft mannschaft = new Mannschaft();
        mannschaft.setUrl("https://www.mytischtennis.de/clicktt/");
        parser.parseMannschaftsDetail(page, mannschaft);
        assertEquals("Hersel, Bernd", mannschaft.getKontakt());
        assertEquals("bhersel79@yahoo.de", mannschaft.getMailTo());
        assertEquals("0228-3361263", mannschaft.getKontaktNr());
        assertEquals("0177-2735179", mannschaft.getKontaktNr2());
        assertEquals(3, mannschaft.getSpielLokale().size());
        assertEquals("Turnhalle GS Rheidt\n" +
                "Hoher Rain (neben der Kirche)\n" +
                "53859 Niederkassel-Rheidt", mannschaft.getSpielLokale().get(2));
        assertEquals("Turnhalle der Grundschule\n" +
                "Martin-Buber-Straße\n" +
                "53859 Niederkassel", mannschaft.getSpielLokale().get(1));
        assertEquals("Sporthalle Nord am Kopernikus-Gymnasium Niederkassel\n" +
                "Premnitzerstr. Ecke Kopernikusstr.\n" +
                "53859 Niederkassel-Lülsdorf", mannschaft.getSpielLokale().get(0));
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/verein/156009/TTG-Niederkassel/info",
                mannschaft.getVereinUrl());
    }

    @Test
    public void parseLigaAdressen() throws Exception {

        String page = TestUtil.readFile(ASSETS_DIR + "/click-tt-home.html");
        Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home");
        parser.parseLiga(page, liga);

        assertEquals(12, liga.getMannschaften().size());

        String page2 = TestUtil.readFile(ASSETS_DIR + "/click-tt-home_tabplan.html");
        parser.parseErgebnisse(page2, liga, Liga.Spielplan.RR);

        //https://www.mytischtennis.de/clicktt/home-tab?id=adressen
        page = TestUtil.readFile(ASSETS_DIR + "/home-tab-kontakte.html");
        parser.parseLigaAdressen(page, liga);

        for (Mannschaft mannschaft : liga.getMannschaften()) {
            assertTrue(mannschaft.toString(), mannschaft.getSpielLokale().size() > 0);
            assertNotNull(mannschaft.toString(), mannschaft.getVereinUrl());
            if (mannschaft.getName().contains("Hürth")) {
                assertTrue(mannschaft.getSpielLokal(1).contains("Clementinenschule"));
                assertTrue(mannschaft.getSpielLokal(2).contains("Kendenich"));
                assertTrue(mannschaft.getSpielLokal(3).contains("Friedrich-Ebert-Realschule"));
            }
        }
    }

    @Test
    public void parseLigaAdressen486() throws Exception {

        String page = TestUtil.readFile(ASSETS_DIR + "/bug-486/tabelle.html");
        Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home");
        parser.parseLiga(page, liga);

        assertEquals(10, liga.getMannschaften().size());

        String page2 = TestUtil.readFile(ASSETS_DIR + "/bug-486/spielplan.html");
        parser.parseErgebnisse(page2, liga, Liga.Spielplan.RR);

        //https://www.mytischtennis.de/clicktt/home-tab?id=adressen
        page = TestUtil.readFile(ASSETS_DIR + "/bug-486/kontakte-bug-486.html");
        parser.parseLigaAdressen(page, liga);

        for (Mannschaft mannschaft : liga.getMannschaften()) {
            if (mannschaft.getName().contains("Siegertshofen")) {
                assertEquals(mannschaft.toString(), 0, mannschaft.getSpielLokale().size());
            } else if (mannschaft.getName().contains("Mindelzell")) {
                assertEquals(mannschaft.toString(), 0, mannschaft.getSpielLokale().size());
            } else {
                assertTrue(mannschaft.toString(), mannschaft.getSpielLokale().size() > 0);
                assertNotNull(mannschaft.toString(), mannschaft.getVereinUrl());

            }
        }
    }

    @Test
    public void parseVerein() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-verein.html");
        Verein v = parser.parseVerein(page);
        assertNotNull(v);
        assertEquals("TTG Niederkassel", v.getName());
        assertEquals("Christian Hopp\n" +
                "Arndtstr. 22\n" +
                "53859 Niederkassel", v.getKontakt().getNameAddress());
        assertEquals("chris.hopp@imail.de", v.getKontakt().getMail());
        assertEquals("http://www.ttgniederkassel.de", v.getKontakt().getUrl());
        assertEquals(3, v.getLokaleUnformatted().size());
    }

    @Test
    public void parseVerein2() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-verein2.html");
        Verein v = parser.parseVerein(page);
        assertNotNull(v);
        assertEquals("TTF Bad Honnef", v.getName());
        assertEquals("Schomisch, Stefan\n" +
                "Breitbacher Graben 11\n" +
                "53604 Bad Honnef", v.getKontakt().getNameAddress());
        assertEquals("ttf.badhonnef@web.de", v.getKontakt().getMail());
        assertEquals("http://www.ttfbadhonnef.de", v.getKontakt().getUrl());
        assertEquals(2, v.getLokaleUnformatted().size());
        assertEquals("Sporthalle im Sportzentrum\n" +
                "Menzenberger Straße\n" +
                "53604 Bad Honnef", v.getLokaleUnformatted().get(0));
        assertEquals("Turnhalle der Löwenburgschule\n" +
                "Am Feuerschlößchen\n" +
                "53604 Bad Honnef", v.getLokaleUnformatted().get(1));
    }

    @Test
    public void parseVereinMannschaften() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-verein-mannschaften.html");
        Verein v = new Verein();
        parser.parseVereinMannschaften(v, page);
        assertEquals(24, v.getMannschaften().size());
        assertEquals("Schüler B", v.getMannschaften().get(23).name);
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Schueler-B-1-Kreisklasse-1/gruppe/306911/tabelle/gesamt", v.getMannschaften().get(23).url);
        assertEquals("Schüler B- 1. Kreisklasse 1", v.getMannschaften().get(23).liga);
        assertEquals("Herren", v.getMannschaften().get(0).name);
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Herren-NRW-Liga-3/gruppe/305905/tabelle/gesamt", v.getMannschaften().get(0).url);
        assertEquals("Herren NRW-Liga 3", v.getMannschaften().get(0).liga);
    }

    @Test
    public void parseVereinSpielplan() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-verein-spielplan.html");
        Verein v = new Verein();
        parser.parseVereinSpielplan(v, page);
        assertEquals(143, v.getSpielplan().size());
        assertEquals("8:5", v.getSpielplan().get(8).getErgebnis());
        assertEquals("Sa 02.11.19 16:30", v.getSpielplan().get(0).getDate());
//        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Herren-1-Kreisklasse-1/gruppe/305887/spielbericht/9968514/TTG-St-Augustin-IV-vs-TuS-Birk",
//                v.getSpielplan().get(3).getUrlDetail());
//        assertEquals("9:0", v.getSpielplan().get(3).getErgebnis());
        assertEquals("TTG St. Augustin", v.getSpielplan().get(0).getHeimMannschaft().getName());
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/19-20/ligen/Jungen-18-Kreisliga/gruppe/357048/mannschaft/2253010/TTG-St-Augustin/spielerbilanzen/vr/",
                v.getSpielplan().get(0).getHeimMannschaft().getUrl());
        assertEquals("HSG Siebengebirge-Thomasberg III", v.getSpielplan().get(0).getGastMannschaft().getName());
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/19-20/ligen/Jungen-18-Kreisliga/gruppe/357048/mannschaft/2229482/HSG-Siebengebirge-Thomasberg-III/spielerbilanzen/vr/",
                v.getSpielplan().get(0).getGastMannschaft().getUrl());
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/19-20/verein/156012/TTG-St-Augustin/info/",
                v.getSpielplan().get(0).getUrlSpielLokal());
        assertEquals(1, v.getSpielplan().get(0).getNrSpielLokal());
        assertEquals(1, v.getSpielplan().get(2).getNrSpielLokal());
        assertEquals("9:3", v.getSpielplan().get(10).getErgebnis());

//        assertEquals("Sa. 21.04.2018 18:30", v.getSpielplan().get(59).getDate());
//        assertEquals("", v.getSpielplan().get(59).getErgebnis());
//        assertNull(v.getSpielplan().get(59).getUrlDetail());

    }

    @Test
    public void removeHtml() {
        assertEquals("18:30", parser.removeHtml("18:30\r\n" +
                "<a data-toggle='tooltip' title='Heimrecht in der Begegnung wurde getauscht'>t</a>"));
    }

    @Test
    public void parseMannschaftspiel() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschaftsspiel.html");
        Mannschaftspiel spiel = new Mannschaftspiel();
        spiel.setUrlDetail("http://bla.de" + spiel.getUrlDetail());
        parser.parseMannschaftspiel(page, spiel);
        for (Spielbericht spielbericht : spiel.getSpiele()) {
            System.out.println("spielbericht = " + spielbericht);
            assertNotEquals(spielbericht.getSpieler1Name(), spielbericht.getSpieler2Name());
            if (!spielbericht.getName().contains("D")) { //not for double
                assertThat(spielbericht.getMyTTPlayerIdsForPlayer1().buildPopupUrl(), startsWith("http"));
                assertThat(spielbericht.getMyTTPlayerIdsForPlayer2().buildPopupUrl(), startsWith("http"));
                assertNotEquals(spielbericht.getMyTTPlayerIdsForPlayer1().buildPopupUrl(),
                        spielbericht.getMyTTPlayerIdsForPlayer2().buildPopupUrl());
            }
        }
//        System.out.println("spiel = " + spiel);
    }

    @Test
    public void parseLinksKreise() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-bezirk.html");
        List<Kreis> kreisList = parser.parseLinksKreise(page);
        assertEquals(6, kreisList.size());
        for (Kreis kreis : kreisList) {
            assertNotNull(kreis.getName());
            assertNotNull(kreis.getUrl());
        }
    }

    @Test
    public void parseLinksForPlayer() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/popover.html");
        Spieler spieler = parser.parseLinksForPlayer(page, "dummy");
        assertNotNull(spieler);
        assertEquals("https://www.mytischtennis.de/clicktt/WTTV/17-18/spieler/143001489/spielerportrait", spieler.getMytTTClickTTUrl());
        assertEquals(297020L, (long) spieler.getPersonId());
        assertEquals("https://www.mytischtennis.de/community/headTohead?gegnerId=297020", spieler.getHead2head());
    }

    @Test
    public void parseLinksForPlayerNoLOgin() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/popover-no-login.html");
        try {
            parser.parseLinksForPlayer(page, "dummy");
            fail();
        } catch (LoginExpiredException e) {
            //ok
        }
    }

    @Test
    public void parseSpieler() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/mytt-spieler.html");
        Spieler spieler = new Spieler("dummy");
        spieler = parser.parseSpieler(spieler, page);
        assertNotNull(spieler);
        assertThat(spieler.getClubName(), is("TTC Waldniel"));
        assertThat(spieler.getEinsaetze().get(0).getKategorie(), is("Herren 1"));
        assertThat(spieler.getEinsaetze().get(0).getLigaName(), is("Herren NRW-Liga 3"));
        assertThat(spieler.getEinsaetze().get(0).getUrl(),
                is("https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Herren-NRW-Liga-3/gruppe/305905/tabelle/aktuell"));
        assertThat(spieler.getEinsaetze().get(1).getKategorie(), is("Senioren 50 1"));
        assertThat(spieler.getErgebnisse().size(), is(2));
        assertThat(spieler.getErgebnisse().get(0).getSpiele().size(), is(18));
        assertThat(spieler.getErgebnisse().get(0).getName(), is("Herren NRW-Liga 3 (Vorrunde)"));
        assertThat(spieler.getErgebnisse().get(1).getName(), is("Senioren 50-Bezirksliga (Vorrunde)"));
        assertThat(spieler.getErgebnisse().get(1).getSpiele().size(), is(2));
    }

    @Test
    public void parseSpielerFehler() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/no-data.html");
        try {
            parser.validatePage(page);
            fail();
        } catch (NoClickTTException e) {
            //ok
        }
    }
}
