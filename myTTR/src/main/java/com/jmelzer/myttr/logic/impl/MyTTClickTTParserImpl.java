package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.AbstractBaseParser;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 26.11.2017.
 * Parse the click tt pages from mytt
 */
public class MyTTClickTTParserImpl extends AbstractBaseParser implements MyTTClickTTParser {

    public static final String CONTAINER_START_TAG = "<div class=\"col-sm-6 col-xs-12\">";

    @Override
    public void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException {
        String url = "";
        url += verband.getUrlFixed(saison);
        String page = Client.getPage(url);
        List<Bezirk> list = parseLinksBezirke(page);
        verband.setBezirkList(list, saison);

        //read link to ligen and load it
        ParseResult result = readBetween(page, 0, "<div class=\"row m-l text-center\">",
                "</div>");
        if (!isEmpty(result)) {
            String urlLiga = readHrefAndATag(result.result)[0];
            page = Client.getPage(UrlUtil.getHttpAndDomain(url) + urlLiga);

            List<Liga> ligen = parseLigaLinks(page);
            verband.addAllLigen(ligen, saison);
        }

    }

    @Override
    public void readKreiseAndLigen(Bezirk bezirk) throws NetworkException {
        String page = Client.getPage(bezirk.getUrl());
        List<Liga> listLiga = parseLigaLinks(page);
        bezirk.addAllLigen(listLiga);
    }

    @Override
    public void readLiga(Liga liga) throws NetworkException {
        String url = liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(page, liga);
    }

    @Override
    public void readMannschaftsInfo(Mannschaft mannschaft) throws NetworkException {
        String page = Client.getPage(mannschaft.getUrl());
        parseMannschaftsDetail(page, mannschaft);
    }

    @Override
    public void readVR(Liga liga) throws NetworkException {
        if (liga.getUrlVR() != null) {
            String page = Client.getPage(liga.getUrlVR());
            parseErgebnisse(page, liga, Liga.Spielplan.VR);
        }
    }

    @Override
    public void readDetail(Mannschaftspiel spiel) throws NetworkException {
        String url = spiel.getUrlDetail();
        String page = Client.getPage(url);
        parseMannschaftspiel(page, spiel);
    }

    void parseMannschaftspiel(String page, Mannschaftspiel spiel) {
        ParseResult table = readBetween(page, 0, "<table class=\"hidden-xs", "</table>");
        int c = 0;
        int idx = 0;
        while (true) {
            ParseResult resultrow = readBetween(table.result, idx, "<tr>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row

            }
            String[] row = tableRowAsArray(resultrow.result, 12, false);
            int i = 0;
            for (String s : row) {
                System.out.println("s[" + i + "] = " + s);
                i++;
            }
            Spielbericht spielbericht = new Spielbericht();
            spiel.addSpiel(spielbericht);
            spielbericht.setName(row[0]);
            if (row[0].startsWith("D")) {
                String line = row[2];
                String[] ahref = readHrefAndATag(line);
                //https://www.mytischtennis.de/clicktt/WTTV/player/popover?personId=NU423987&clubNr=156012&_=1511709681311

                spielbericht.setSpieler1Url(ahref[0]);
                spielbericht.setSpieler1Name(ahref[1]);
                line = line.substring(line.indexOf("<br"));
                ahref = readHrefAndATag(line);
                spielbericht.setSpieler1Name(spielbericht.getSpieler1Name() + " / " + ahref[1]);

                line = row[4];
                ahref = readHrefAndATag(line);
                spielbericht.setSpieler2Url(ahref[0]);
                spielbericht.setSpieler2Name(ahref[1]);
                line = line.substring(line.indexOf("<br"));
                ahref = readHrefAndATag(line);
                spielbericht.setSpieler2Name(spielbericht.getSpieler2Name() + " / " + ahref[1]);
            } else {
                String[] ahref = readHrefAndATag(row[2]);
                spielbericht.setSpieler1Url(ahref[0]);
                spielbericht.setSpieler1Name(ahref[1]);
                ParseResult idResult = readBetween(row[2], 0, "personId: '", "'");
                spielbericht.setSpieler1PersonId(idResult.result);

                ahref = readHrefAndATag(row[4]);
                spielbericht.setSpieler2Url(ahref[0]);
                spielbericht.setSpieler2Name(ahref[1]);
                idResult = readBetween(row[4], 0, "personId: '", "'");
                spielbericht.setSpieler2PersonId(idResult.result);
            }
            for (int j = 5; j < 10; j++) {
                spielbericht.addSet(row[j]);
            }
            spielbericht.setResult(row[10]);
            idx = resultrow.end;

        }
    }

    void parseErgebnisse(String page, Liga liga, Liga.Spielplan spielplan) {
        int c = 0;
        int idx = 0;
        String lastDate = null;
        liga.clearSpiele(spielplan);

        ParseResult table = readBetweenOpenTag(page, 0, "<table class=\"table table-mytt", "</table>");
        while (true) {
            ParseResult resultrow = readBetween(table.result, idx, "<tr>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row
            }
            String[] row = tableRowAsArray(resultrow.result, 10, false);
//            for (String s : row) {
//                System.out.println("s = " + s);
//            }
            Mannschaftspiel mannschaftspiel = new Mannschaftspiel(row[0],
                    findMannschaft(liga, readHrefAndATag(row[3])[1]),
                    findMannschaft(liga, readHrefAndATag(row[4])[1]),
                    readHrefAndATag(row[5])[1],
                    UrlUtil.safeUrl(liga.getHttpAndDomain(), readHrefAndATag(row[5])[0]),
                    true);
            if (mannschaftspiel.getDate() == null || mannschaftspiel.getDate().isEmpty()) {
                mannschaftspiel.setDate(lastDate);
            } else {
                lastDate = mannschaftspiel.getDate();
            }
            liga.addSpiel(mannschaftspiel, spielplan);
            idx = resultrow.end;

        }

    }

    private Mannschaft findMannschaft(Liga liga, String name) {
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            if (mannschaft.getName().equals(name)) {
                return mannschaft;
            }
        }
        return new Mannschaft(name);
    }

    void parseMannschaftsDetail(String page, Mannschaft mannschaft) {
        ParseResult result = readBetween(page, 0, "Mannschaftskontakt", "</li>");
        if (!isEmpty(result)) {
            ParseResult resultK = readBetween(result.result, 0, "<strong>", "</strong>");
            if (!isEmpty(resultK)) {
                mannschaft.setKontakt(resultK.result);
            }
            String[] ahref = readHrefAndATag(result.result);
            if (ahref != null) {
                mannschaft.setMailTo(cleanMail(ahref[0]));
            }
        }
    }

    private String cleanMail(String s) {
        if (s != null) {
            int i = s.indexOf("mailto:");
            if (i > -1) {
                return s.substring(i + "mailto:".length());
            }
        }
        return s;
    }

    void parseLiga(String page, Liga liga) {

        parseSpielplanLinks(liga, page);

        liga.clearMannschaften();
        int idx = 0;
        int c = 0;
        ParseResult table = readBetweenOpenTag(page, 0, "<table class=\"table table-mytt", "</table>");
        while (true) {
            ParseResult resultrow = readBetween(table.result, idx, "<tr>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row
            }
            String[] row = tableRowAsArray(resultrow.result, 10, false);
            String nameWithRef = row[2];
            String[] href = readHrefAndATag(nameWithRef);
            Mannschaft m = new Mannschaft(href[1],
                    Integer.valueOf(row[1]),
                    Integer.valueOf(row[3]),
                    Integer.valueOf(row[4]),
                    Integer.valueOf(row[5]),
                    Integer.valueOf(row[6]),
                    row[7],
                    row[8],
                    row[9], href[0]);
            m.setUrl(UrlUtil.safeUrl(liga.getHttpAndDomain(), m.getUrl()));
            liga.addMannschaft(m);
            idx = resultrow.end;

        }
    }

    private void parseSpielplanLinks(Liga liga, String page) {
        String url = liga.getUrl();
        url = url.substring(0, url.indexOf("/tabelle/aktuell"));
        liga.setUrlVR(url + "/spielplan/vr");
        liga.setUrlRR(url + "/spielplan/rr");
        liga.setUrlGesamt(null);
    }

    List<Bezirk> parseLinksBezirke(String page) {
        int start = 0;
        List<Bezirk> bezirkList = new ArrayList<>();

        while (true) {
            ParseResult result = readBetween(page, start, CONTAINER_START_TAG, "</div>");

            if (!isEmpty(result)) {
                String[] urlref = readHrefAndATag(result.result);
                ParseResult rn = readBetween(urlref[1], 0, "<h4>", "<");
                Bezirk v = new Bezirk(rn.result, urlref[0]);
                bezirkList.add(v);

            } else {
                break;
            }
            start = result.end;
        }
        return bezirkList;
    }

    List<Liga> parseLigaLinks(String page) {
        List<Liga> ligen = new ArrayList<>();
        int start = 0;
        while (true) {
            ParseResult result = readBetween(page, start, CONTAINER_START_TAG, "</div>");

            if (!isEmpty(result)) {
                parseLigaLinks(ligen, result);
            } else {
                break;
            }
            start = result.end;
        }
        return ligen;
    }

    void parseLigaLinks(List<Liga> ligen, ParseResult startResult) {
        String kategorie = readKategorie(startResult);
        ParseResult result;
        int idx = 0;

        while (true) {
            result = readBetween(startResult.result, idx, "<a href", "</a>");
            if (!isEmpty(result)) {
                String url = readBetween(result.result, 0, "=\"", "\">").result;
                String name = readBetween(result.result, 0, ">", null).result;
                Liga liga = new Liga(name, url, kategorie);
                ligen.add(liga);
                idx = result.end;
            } else {
                break;
            }

        }
    }

    private String readKategorie(ParseResult startResult) {
        for (String s : Liga.alleKategorien) {
            if (startResult.result.contains(s)) {
                return s;
            }
        }
        return null;
    }
}
