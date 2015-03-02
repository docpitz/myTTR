package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
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

    public static final String HTTP_DTTB_CLICK_TT_DE = "http://dttb.click-tt.de";

    /**
     * parsed die Ergebnisse, und füllt das Liga objekt
     * siehe http://dttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/groupPage?displayTyp=vorrunde&displayDetail=meetings&championship=DTTB+14%2F15&group=223193
     */
    Liga parseErgebnisse(Liga liga, String page, boolean vorrunde) {
        ParseResult table = readBetween(page, 0, "<table class=\"result-set\"", "</table>");
        int c = 0;
        int idx = 0;
        String lastDate = null;
        liga.clearSpiele(vorrunde);
        while (true) {
            ParseResult resultrow = readBetweenOpenTag(table.result, idx, "<tr", "</tr>");

            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row

            }
            idx = resultrow.end - 1;

            Mannschaftspiel m = parseSpieleTableRow(liga, resultrow);
            if (m.getDate() == null || m.getDate().equals(" ")) {
                m.setDate(lastDate);
            } else {
                lastDate = m.getDate();
            }
            liga.addSpiel(m, vorrunde);


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

    List<Verband> parseLinksSubLigen(String page) {
        List<Verband> verbandList = new ArrayList<>();
        ParseResult result = readBetweenOpenTag(page, 0, "<div class=\"liga-layer\">", " </div>");
        if (result.isEmpty()) {
            return verbandList;
        }
        int idx = 0;
        while (true) {
            ParseResult resultUl = readBetweenOpenTag(result.result, idx, "<ul class=\"horizontal-menu\">", " </ul>");
            if (resultUl == null || resultUl.isEmpty()) {
                break;
            }
            idx = resultUl.end;
            //collect all href links
            int idxL = 0;
            while (true) {
                ParseResult resultLinks = readBetween(resultUl.result, idxL, "<a", "</a>");
                if (resultLinks == null || resultLinks.isEmpty()) {
                    break;
                }
                idxL = resultLinks.end;
                ParseResult resultLink = readBetween(resultLinks.result, 0, "href=\"", "\">");
                ParseResult resultName = readBetween(resultLinks.result, 0, "\">", null);
                Verband v = new Verband(resultName.result, resultLink.result);
                verbandList.add(v);

            }

        }
        return verbandList;
    }

    List<Liga> parseLigaLinks(String page) {
        List<Liga> ligen = new ArrayList<>();
        for (String kategorie : Liga.alleKategorien) {
            ParseResult result = readBetween(page, 0, "<h2>" + kategorie + "</h2>", "<h2>");
            if (result == null || result.isEmpty()) {
                //try another thing
                result = readBetween(page, 0, "<h2>" + kategorie + "</h2>", "</td>");
            }
            if (result != null && !result.isEmpty()) {
                parseLigaLinks(ligen, result, kategorie);
            }
        }
        return ligen;

    }

    void parseLigaLinks(List<Liga> ligen, ParseResult startResult, String sex) {
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


    public List<Verband> readVerbaende() throws NetworkException {
//        List<Verband> verbaende = new ArrayList<>();
//        String url = "http://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15";
//        String page = Client.getPage(url);
        List<Verband> verbandList = new ArrayList<>(Verband.verbaende);
//                parseLinksSubLigen(page);
//        verbandList.add(0, Verband.dttb);
//
        return verbandList;
    }

    public Verband readTopLigen() throws NetworkException {
        //todo saison
        String url = "http://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15";
        String page = Client.getPage(url);
        List<Liga> ligen = parseLigaLinks(page);
        Verband.dttb.addAllLigen(ligen);
        return Verband.dttb;
    }

    public void readLiga(Liga liga) throws NetworkException {
        String url = HTTP_DTTB_CLICK_TT_DE;
        url += liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(liga, page);
    }

    public void readVR(Liga liga) throws NetworkException {
        String url = HTTP_DTTB_CLICK_TT_DE;
        url += liga.getUrlVR();
        String page = Client.getPage(url);
        parseErgebnisse(liga, page, true);
    }

    public void readDetail(Mannschaftspiel spiel) throws NetworkException {
        String url = HTTP_DTTB_CLICK_TT_DE;
        url += spiel.getUrlDetail();
        String page = Client.getPage(url);
        parseMannschaftspiel(page, spiel);
    }

    public void readRR(Liga liga) throws NetworkException {
        String url = HTTP_DTTB_CLICK_TT_DE;
        url += liga.getUrlRR();
        String page = Client.getPage(url);
        parseErgebnisse(liga, page, false);
    }

    /**
     * read the ligen from the url inside the verband
     */
    public void readLigen(Verband verband) throws NetworkException {
        String url = "";//HTTP_DTTB_CLICK_TT_DE;
        url += verband.getUrl();
        String page = Client.getPage(url);
        List<Liga> ligen = parseLigaLinks(page);
        verband.addAllLigen(ligen);
    }

    /**
     * read the ligen from the url inside the verband
     */
    public void readBezirke(Verband verband) throws NetworkException {
        String url = verband.getUrl();
        String page = Client.getPage(url);
        List<Bezirk> list = parseLinksBezirke(page);
        verband.setBezirkList(list);
    }

    List<Bezirk> parseLinksBezirke(String page) {
        List<Bezirk> bezirkList = new ArrayList<>();
        ParseResult result = readBetween(page, 0, "Untergeordnete Spielklassen", null);
        if (result.isEmpty()) {
            return bezirkList;
        }
        int idx = 0;
        while (true) {
            ParseResult resultUl = readBetween(result.result, idx, "<h2 class=\"liga-layer-down\">", "</h2>");
            if (resultUl == null || resultUl.isEmpty()) {
                break;
            }
            idx = resultUl.end;
            //collect all href links
            int idxL = 0;
            while (true) {
                ParseResult resultLinks = readBetween(resultUl.result, idxL, "<a", "</a>");
                if (resultLinks == null || resultLinks.isEmpty()) {
                    break;
                }
                idxL = resultLinks.end;
                ParseResult resultLink = readBetween(resultLinks.result, 0, "href=\"", "\">");
                ParseResult resultName = readBetween(resultLinks.result, 0, "\">", null);
                Bezirk v = new Bezirk(resultName.result, resultLink.result);
                bezirkList.add(v);

            }

        }
        return bezirkList;
    }

    public void readKreise(Bezirk bezirk) throws NetworkException {
        String url = bezirk.getVerband().getHttpAndDomain() + bezirk.getUrl();
        String page = Client.getPage(url);
        List<Kreis> list = parseLinksKreise(page);
        bezirk.setKreise(list);
        List<Liga> listLiga = parseLigaLinks(page);
        bezirk.setLigen(listLiga);
    }

    private List<Kreis> parseLinksKreise(String page) {
        List<Kreis> kreisList = new ArrayList<>();
        ParseResult result = readBetween(page, 0, "Untergeordnete Spielklassen", "</ul>");
        if (result.isEmpty()) {
            return kreisList;
        }
        int idxL = 0;
        while (true) {
            ParseResult resultLinks = readBetween(result.result, idxL, "<a", "</a>");
            if (resultLinks == null || resultLinks.isEmpty()) {
                break;
            }
            idxL = resultLinks.end;
            ParseResult resultLink = readBetween(resultLinks.result, 0, "href=\"", "\">");
            ParseResult resultName = readBetween(resultLinks.result, 0, "\">", null);
            Kreis v = new Kreis(resultName.result, resultLink.result);
            kreisList.add(v);


        }
        return kreisList;
    }
    /**
     * read the ligen from the url inside the verband
     */
    public void readLigen(Kreis kreis) throws NetworkException {
        String url = kreis.getBezirk().getVerband().getHttpAndDomain() + kreis.getUrl();
        String page = Client.getPage(url);
        List<Liga> ligen = parseLigaLinks(page);
        kreis.setLigen(ligen);
    }
}
