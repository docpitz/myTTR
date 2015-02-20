package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Verband;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Parse the click tt pages
 */
public class ClickTTParser extends AbstractBaseParser {

    /**
     * parsed die Ergebnisse, und füllt das Liga objekt
     * siehe http://dttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/groupPage?displayTyp=vorrunde&displayDetail=meetings&championship=DTTB+14%2F15&group=223193
     */
    Liga parseErgebnisse(Liga liga, String page, boolean vorrunde) {
        ParseResult table = readBetween(page, 0, "<table class=\"result-set\"", "</table>");
        int c = 0;
        int idx = 0;
        String lastDate = null;
        while (true) {
            ParseResult resultrow = readBetween(table.result, idx, "<tr>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row

            }
            Mannschaftspiel m = parseSpieleTableRow(liga, resultrow);
            if (m.getDate() == null || m.getDate().equals(" ")) {
                m.setDate(lastDate);
            } else {
                lastDate = m.getDate();
            }
            liga.addSpiel(m, vorrunde);
            idx = resultrow.end;

        }
        return liga;
    }

    private Mannschaftspiel parseSpieleTableRow(Liga liga, ParseResult resultrow) {
        //tag
        ParseResult result = readBetweenOpenTag(resultrow.result, 0, "<td", "</td>");
        String datum = result.result;
        //datum/zeit
        result = readBetweenOpenTag(resultrow.result, result.end, "<td", "</td>");
        datum += " " + result.result;
        //Uhrzeit we dont use
        result = readBetween(resultrow.result, result.end + 1, "<td", "</td>");
        //halle: we don't use
        result = readBetween(resultrow.result, result.end + 1, "<td", "</td>");
        //nr: we don't use
        result = readBetween(resultrow.result, result.end + 1, "<td", "</td>");
        //Heim
        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        String heimMannsschaft = result.result;
        //Gast
        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        String gastMannsschaft = result.result;
        //Ergebnis
        result = readBetween(resultrow.result, result.end, "<td ", "</td>");
        ParseResult result2 = readBetween(result.result, 0, "href=\"", "\">");
        String url = safeResult(result2);
        result2 = readBetween(result.result, 0, "<span>", "</span>");
        String ergebnis = safeResult(result2);
        //genehmigt
        result = readBetween(resultrow.result, result.end, "<td nowrap=\"nowrap\">", "</td>");
        boolean genehmigt = result.result.contains("genehmigt");
        return new Mannschaftspiel(datum,
                findMannschaft(liga, heimMannsschaft),
                findMannschaft(liga, gastMannsschaft),
                ergebnis, url, genehmigt);
    }

    private String safeResult(ParseResult parseResult) {
        if (parseResult != null) {
            return parseResult.result;
        }
        return null;
    }

    private Mannschaft findMannschaft(Liga liga, String name) {
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            if (mannschaft.getName().equals(name)) {
                return mannschaft;
            }
        }
        return null;
    }

    /**
     * see http://dttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/groupPage?championship=DTTB+14%2F15&group=223193
     */
    Liga parseLiga(Liga liga, String page) {
        ParseResult table = readBetween(page, 0, "<table class=\"result-set\"", "</table>");
        int c = 0;
        int idx = 0;
        parseSpielplanLinks(liga, page);
        while (true) {
            ParseResult resultrow = readBetween(table.result, idx, "<tr>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row
            }
            Mannschaft m = parseLigaTableRow(resultrow);
            liga.addMannschaft(m);
            idx = resultrow.end;

        }
        return liga;

    }

    private void parseSpielplanLinks(Liga liga, String page) {
        ParseResult result = readBetween(page, 0, "<li>Spielplan", "</li>");
        if (result != null) {
            ParseResult result2 = readBetween(result.result, 0, "href=\"", "\">");
            String urlVR = result2.result;
            liga.setUrlVR(urlVR);
            result2 = readBetween(result.result, result2.end, "href=\"", "\">");
            if (result2 != null) {
                liga.setUrlRR(result2.result);
            }
        }
    }

    private Mannschaft parseLigaTableRow(ParseResult resultrow) {
        ParseResult result = readBetween(resultrow.result, 0, "<td", "</td>");
        //skip first
        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        int nPos = readIntFromNextTd(result);

        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        //fix a
        ParseResult result2 = readBetween(result.result, 0, "href=\"", "\"");
        String url = result2.result;

        String name = readBetween(result.result, result2.end, ">", "</a>").result;

        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        int nGamesCount = readIntFromNextTd(result);

        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        int nWin = readIntFromNextTd(result);

        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        int nTied = readIntFromNextTd(result);
        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        int nLose = readIntFromNextTd(result);

        result = readBetween(resultrow.result, result.end, "<td", "</td>");
        result2 = readBetween(result.result, 1, ">", "</span>");
        result2 = readBetween(result2.result, 1, ">", null);
        String gameStat = result2.result;

        result = readBetween(resultrow.result, result.end, "<td align=\"center\">", "</td>");
        String sum = result.result;

        result = readBetween(resultrow.result, result.end, "<td align=\"center\">", "</td>");
        String points = result.result;

        return new Mannschaft(name, nPos, nGamesCount, nWin, nTied, nLose, gameStat, sum, points, url);
    }

    private int readIntFromNextTd(ParseResult result) {
        String win = readBetween(result.result, 0, ">", null).result;
        return Integer.valueOf(win);
    }

    List<Liga> parseUntilOberliga(String page) {
        List<Liga> ligen = new ArrayList<>();

        ParseResult resultHerren = readBetween(page, 0, "<h2>Herren</h2>", "<h2>Damen</h2>");
        if (resultHerren.isEmpty()) {
            return ligen;
        }
        parseLiga(ligen, resultHerren, "Herren");

        ParseResult resultDamen = readBetween(page, 0, "<h2>Damen</h2>", "</tr>");
        if (resultDamen.isEmpty()) {
            return ligen;
        }
        parseLiga(ligen, resultDamen, "Damen");
        return ligen;

    }

    void parseLiga(List<Liga> ligen, ParseResult startResult, String sex) {
        int idx = 0;
        while (true) {
            //one row
            ParseResult resultLiga = readBetween(startResult.result, idx, "<ul", "</ul>");
            if (isEmpty(resultLiga)) {
                break;
            }
            //must be loop here, cause html is bad and forgot the last ul
            int subIdx = 0;
            while (true) {
                ParseResult result = readBetween(resultLiga.result, subIdx, "<a href", "</a>");
                if (!isEmpty(result)) {
                    String url = readBetween(result.result, 0, "=\"", "\">").result;
                    String name = readBetween(result.result, 0, ">", null).result;
                    Liga liga = new Liga(name, url, sex);
                    ligen.add(liga);
                    subIdx = result.end;
                } else {
                    break;
                }

            }
            idx = resultLiga.end - 10;
        }
    }

    void parseMannschaftspiel(String page, Mannschaftspiel mannschaftspiel) {
        ParseResult table = readBetween(page, 0, "<table class=\"result-set\"", "</table>");
        int c = 0;
        int idx = 0;
        String lastDate = null;
        while (true) {
            ParseResult resultrow = readBetween(table.result, idx, "<tr>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row

            }
            if (!safeResult(resultrow).contains("Bälle")) {
                parseMannschaftsspielTableRow(mannschaftspiel, resultrow);
            } else {
                parseMannschaftsspielStatistik(mannschaftspiel, resultrow);
            }
            idx = resultrow.end;

        }
    }

    private void parseMannschaftsspielStatistik(Mannschaftspiel mannschaftspiel, ParseResult resultrow) {
        ParseResult result = readBetweenOpenTag(resultrow.result, 0, "<td", "</td>");
        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        mannschaftspiel.setBaelle(result.result);
        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<b", "</b>");
        mannschaftspiel.setSaetze(result.result);
    }

    private void parseMannschaftsspielTableRow(Mannschaftspiel mannschaftspiel, ParseResult resultrow) {
        ParseResult result = readBetweenOpenTag(resultrow.result, 0, "<td", "</td>");
        String posName = safeResult(result);
        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        if (result == null) { //sometime empty lines in table, we skip
            return;
        }
        Spielbericht spielbericht = new Spielbericht();
        mannschaftspiel.addSpiel(spielbericht);
        spielbericht.setName(posName);

        ParseResult result2 = readBetween(result.result, 0, "href=\"", "\">");
        String url = safeResult(result2);
        spielbericht.setSpieler1Url(url);
        result2 = readBetweenOpenTag(resultrow.result, result.end + 1, "<a", "</a>");
        spielbericht.setSpieler1Name(safeResult(result2));

        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        result2 = readBetween(result.result, 0, "href=\"", "\">");
        url = safeResult(result2);
        spielbericht.setSpieler2Url(url);
        result2 = readBetweenOpenTag(result.result, 0, "<a", "</a>");
        spielbericht.setSpieler2Name(safeResult(result2));

        for (int i = 1; i < 6; i++) {
            result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
            spielbericht.addSet(safeResult(result));
        }


        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        spielbericht.setResult(safeResult(result));
    }


    public List<Verband> readVerbaende() {
        List<Verband> verbaende = new ArrayList<>();
//        verbaende.add(new Verband("Badischer TTV", "http://ttvbw.click-tt.de/"));
        verbaende.add(new Verband("Westdeutscher TTV", "http://wttv.click-tt.de/"));

        return verbaende;
    }

    public List<Liga> readTopLigen() throws NetworkException {
        //todo saison
        String url = "http://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15";
        String page = Client.getPage(url);
        return parseUntilOberliga(page);
    }

    public void readLiga(Liga liga) throws NetworkException {
        String url = "http://dttb.click-tt.de";
        url += liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(liga, page);
    }

    public void readVR(Liga liga) throws NetworkException {
        String url = "http://dttb.click-tt.de";
        url += liga.getUrlVR();
        String page = Client.getPage(url);
        parseErgebnisse(liga, page, true);
    }

    public void readDetail(Mannschaftspiel spiel) throws NetworkException {
        String url = "http://dttb.click-tt.de";
        url += spiel.getUrlDetail();
        String page = Client.getPage(url);
        parseMannschaftspiel(page, spiel);
    }
}
