package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.logic.TestUtil;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
            System.out.println("liga = " + liga);
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
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/aktuell");
        parser.parseLiga(page, liga);
        assertEquals(12, liga.getMannschaften().size());
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            System.out.println("mannschaft = " + mannschaft);
        }
    }

    @Test
    public void parseErgebnisse() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-spielplan.html");
        Liga liga = new Liga("Herren-Bezirksliga 2", "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen/Bezirksliga-2/gruppe/305796/tabelle/aktuell");

        parser.parseErgebnisse(page, liga, Liga.Spielplan.VR);
        assertEquals(11, liga.getSpieleVorrunde().size());
        for (Mannschaftspiel mannschaftspiel : liga.getSpieleVorrunde()) {
            System.out.println("mannschaftspiel = " + mannschaftspiel);
            assertNotNull(mannschaftspiel.getDate());
        }
    }

    @Test
    public void parseMannschaftsDetail() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschafts-detail.html");
        Mannschaft mannschaft = new Mannschaft();
        parser.parseMannschaftsDetail(page, mannschaft);
        assertEquals("Lotz, Thorsten", mannschaft.getKontakt());
        assertEquals("lotz@sc-buschhausen.de", mannschaft.getMailTo());
    }

    @Test
    public void parseMannschaftspiel() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-mannschaftsspiel.html");
        Mannschaftspiel spiel = new Mannschaftspiel();
        parser.parseMannschaftspiel(page, spiel);
        System.out.println("spiel = " + spiel);
    }
}
