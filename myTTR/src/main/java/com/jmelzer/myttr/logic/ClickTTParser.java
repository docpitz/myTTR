package com.jmelzer.myttr.logic;

import android.text.Html;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.util.UrlUtil;

import org.apache.commons.lang3.StringUtils;

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
        liga.clearSpiele(vorrunde);
        boolean hasNrInHeader = true;
        while (true) {
            ParseResult resultrow = readBetweenOpenTag(table.result, idx, "<tr", "</tr>");

            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                hasNrInHeader = resultrow.result.contains("Nr");
                continue;//skip first row

            }
            idx = resultrow.end - 1;

            Mannschaftspiel m = parseSpieleTableRow(liga, resultrow, hasNrInHeader);
            if (m.getDate() == null || m.getDate().equals(" ")) {
                m.setDate(lastDate);
            } else {
                lastDate = m.getDate();
            }
            liga.addSpiel(m, vorrunde);


        }
        return liga;
    }

    private Mannschaftspiel parseSpieleTableRow(Liga liga, ParseResult resultrow, boolean hasNrInHeaser) {
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
        if (hasNrInHeaser) {
            //nr: we don't use
            result = readBetween(resultrow.result, result.end + 1, "<td", "</td>");
        }
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
                ergebnis, liga.getHttpAndDomain() + url, genehmigt);
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
        if (table == null) {
            return liga;
        }
        if (!page.contains("Begegnungen")) {
            return liga;
        }

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
            m.setUrl(UrlUtil.safeUrl(liga.getHttpAndDomain(), m.getUrl()));
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
        String url = null;
        String name = null;
        if (result2 != null) {
            url = result2.result;

            name = readBetween(result.result, result2.end, ">", "</a>").result;
        } else {
            name = result.result;
        }
        if (resultrow.result.contains("zurückgezogen") || resultrow.result.contains("aufgelöst")) {
            //todo make it better
            return new Mannschaft(name + " --- zurückgezogen");
        }

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
        try {
            return Integer.valueOf(win);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
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
            for (Spielbericht spielbericht : mannschaftspiel.getSpiele()) {
                spielbericht.setSpieler1Url(UrlUtil.safeUrl(mannschaftspiel.getHttpAndDomain() , spielbericht.getSpieler1Url()));
                spielbericht.setSpieler2Url(UrlUtil.safeUrl(mannschaftspiel.getHttpAndDomain() , spielbericht.getSpieler2Url()));
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

        //todo read doppel correctly
        if (posName.startsWith("D")) {
            String line = result.result;
            String[] ahref = readHrefAndATag(line);
            spielbericht.setSpieler1Url(ahref[0]);
            spielbericht.setSpieler1Name(ahref[1]);
            line = line.substring(line.indexOf("<br"));
            ahref = readHrefAndATag(line);
            spielbericht.setSpieler1Name(spielbericht.getSpieler1Name() + " / " + ahref[1]);
        } else {
            String[] ahref = readHrefAndATag(result.result);
            spielbericht.setSpieler1Url(ahref[0]);
            spielbericht.setSpieler1Name(ahref[1]);
        }
        result = readBetweenOpenTag(resultrow.result, result.end + 1, "<td", "</td>");
        if (posName.startsWith("D")) {
            String line = result.result;
            String[] ahref = readHrefAndATag(line);
            spielbericht.setSpieler2Url(ahref[0]);
            spielbericht.setSpieler2Name(ahref[1]);
            line = line.substring(line.indexOf("<br"));
            ahref = readHrefAndATag(line);
            spielbericht.setSpieler2Name(spielbericht.getSpieler2Name() + " / " + ahref[1]);
        } else {
            String[] ahref = readHrefAndATag(result.result);
            spielbericht.setSpieler2Url(ahref[0]);
            spielbericht.setSpieler2Name(ahref[1]);
        }


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

    /**
     * reads the table (mannschaften and results) of the liga and some urls.
     *
     * @param liga to read
     * @throws NetworkException
     */
    public void readLiga(Liga liga) throws NetworkException {
        String url = liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(liga, page);
    }

    public void readVR(Liga liga) throws NetworkException {
        String url = liga.getHttpAndDomain() + liga.getUrlVR();
        String page = Client.getPage(url);
        parseErgebnisse(liga, page, true);
    }

    public void readDetail(Liga liga, Mannschaftspiel spiel) throws NetworkException {
        String url = spiel.getUrlDetail();
        String page = Client.getPage(url);
        parseMannschaftspiel(page, spiel);
    }

    public void readRR(Liga liga) throws NetworkException {
        String url = "";//HTTP_DTTB_CLICK_TT_DE;
        url += liga.getHttpAndDomain() + liga.getUrlRR();
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

    public void readKreiseAndLigen(Bezirk bezirk) throws NetworkException {
        if (bezirk == null || bezirk.getUrl() == null) {
            return;
        }
        String url = bezirk.getUrl();
        String page = Client.getPage(url);
        List<Kreis> list = parseLinksKreise(page);
        bezirk.setKreise(list);
        List<Liga> listLiga = parseLigaLinks(page);
        bezirk.addAllLigen(listLiga);
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
        String url = kreis.getUrl();
        String page = Client.getPage(url);
        List<Liga> ligen = parseLigaLinks(page);
        kreis.addAllLigen(ligen);
    }

    public void readMannschaftsInfo(Mannschaft mannschaft) throws NetworkException {
        String page = Client.getPage(mannschaft.getUrl());
        parseDetail(page, mannschaft);
    }

    public void parseDetail(String page, Mannschaft mannschaft) {
        ParseResult result = readBetween(page, 0, "Mannschaftskontakt", null);
        result = readBetween(result.result, 0, "<td>", "</td>");
        if (result != null && !result.isEmpty()) {
            String k = result.result;
//            remove mail
            k = k.replaceAll("<script.*", "");
            k = k.replaceAll("<br />", "\n");
            if (k.endsWith("\n")) {
                k = k.substring(0, k.length() - 1);
            }
            mannschaft.setKontakt(k);
            result = readBetween(result.result, 0, "encodeEmail(", ")");
            if (result != null && !result.isEmpty()) {
                mannschaft.setMailTo(unencodeMail(result.result));
            }
        }
        mannschaft.removeAllSpielLokale();
        result = readBetween(page, 0, "<b>Verein</b>", null);
        //block with spiellokale
        result = readBetween(result.result, 0, "<td>", "</td>");
        if (result != null && !result.isEmpty()) {
            int idx = 0;
            while (true) {

                ParseResult resultLokal = readBetween(result.result, idx, "<b>", "<b>");
                if (resultLokal == null || resultLokal.isEmpty()) {
                    //last entry
                    resultLokal = readBetween(result.result, idx, "<b>", null);
                    if (resultLokal == null || resultLokal.isEmpty()) {
                        break;
                    }
                }
                idx = resultLokal.end - 3;
                String lokal = cleanupSpielLokalHtml(resultLokal.result);
//                resultLokal = readBetween(result.result, idx, "</b>", "<br />");
                mannschaft.addSpielLokal(lokal);
            }
        }
    }

    String cleanupSpielLokalHtml(String s) {
//        String result = s.replaceAll("</b>\[ ]{2,}", "");
        String result = s.replaceAll("\\s{2,}", " ").replace("</b>", "");
        result = result.replaceAll("<br />", "\n");
        //remove the last \n
        result = removeLastNewLine(result);
        return result.trim();
    }

    private String removeLastNewLine(String result) {
        if (result.lastIndexOf('\n') == result.length() - 1) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    String unencodeMail(String s) {
        //var concatString = m1+delimiter2+m2+delimiter1+domain+delimiter2+topLevelDomain;
        String result = "";
        String[] parts = StringUtils.split(s, ",");
        if (parts.length == 4) {
            result += parts[1];
            if (!parts[3].equals(" ''")) {
                result += ".";
                result += parts[3];
            }
            result += "@";
            result += parts[2];
            result += ".";
            result += parts[0];
            result = result.replaceAll("'", "").replaceAll(" ", "");
        }
        return result;

    }
    public Spieler readSpielerDetail(String name, String url) throws NetworkException{
        String page = Client.getPage(url);
        return parseSpieler(name , page);
    }
    /**
     * parse the result of the click tt detail
     * e.g. http://wttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/playerPortrait?federation=WTTV&season=2014%2F15&person=974254&club=7425
     *
     * @param page to be parsed
     * @return spieler never null, maybe empty
     */
    public Spieler parseSpieler(String name, String page) {
        Spieler spieler = new Spieler(name);
        //starting point
        ParseResult result = readBetween(page, 0, "Mannschaftsmeldung", null);
        result = readBetweenOpenTag(result.result, 0, "<td>", "</td>");
        spieler.setMeldung(cleanHtml(result));

        result = readBetween(page, 0, "Mannschaftseinsätze", null);
        result = readBetweenOpenTag(result.result, 0, "<td>", "</td>");
        String einsaetze = result.result;
        while (true) {
            ParseResult r = readBetween(einsaetze, 0, null, ":</b>");
            if (r == null) break;
            String kat = r.result;
            String[] aref = readHrefAndATag(einsaetze);

            spieler.addEinsatz(kat, aref[1], aref[0]);
            int idx = einsaetze.indexOf("<b>");
            if (idx == -1) break;
            einsaetze = einsaetze.substring(idx + 3);
        }
        result = readBetween(page, 0, "Einzelbilanzen", null);
        result = readBetweenOpenTag(result.result, 0, "<td>", "</td>");
        String bilanz = result.result;
        while (true) {
            ParseResult r = readBetween(bilanz, 0, null, ":</b>");
            if (r == null) break;
            String kat = r.result;
            ParseResult ergebnis = readBetween(bilanz, 0, "</b>", "<br />");

            spieler.addBilanz(kat, ergebnis.result);
            int idx = bilanz.indexOf("<b>");
            if (idx == -1) break;
            bilanz = bilanz.substring(idx + 3);
        }
        parseEinzelSpiele(spieler, page);
        return spieler;
    }

    private void parseEinzelSpiele(Spieler spieler, String page) {
        ParseResult resultStart = readBetween(page, 0, "Einzel-Spiele", null);
        if (resultStart == null) return;
        ParseResult resultTable = readBetweenOpenTag(resultStart.result, 0, "<table class=\"result-set\"", "</table>");
        if (resultTable == null) return;

        int idx = 0;
        while (true) {
            //every child table have a header in h2
            ParseResult resultE = readBetweenOpenTag(resultTable.result, idx, "<h2", "</h2>");
            if (resultE == null) break;
            System.out.println("Next header " + resultE.result);
            Spieler.LigaErgebnisse ergebnisse = new Spieler.LigaErgebnisse(replaceMultipleSpaces(resultE.result));
            spieler.addLigaErgebnisse(ergebnisse);
            idx = resultE.end;

            int idxT = idx + 1;
            int c = 0;
            String lastDatum = "";
            while (true) {
                //go threw einspiele in current table
                ParseResult resultrow = readBetweenOpenTag(resultTable.result, idxT, "<tr", "</tr>");
//                ParseResult resultrow = readBetween(resultTable.result, idxT, "<tr>", "</tr>");
                if (isEmpty(resultrow) || resultrow.result.contains("<h2>")) {
                    break;
                }
                if (c++ < 1) {
                    idxT = resultrow.end;
                    continue;//skip first 1 row
                }
//                System.out.println("resultrow = " + safeResult(resultrow));
                Spieler.EinzelSpiel einzelSpiel = parseEinzelspielTableRow(resultrow);
                if (einzelSpiel.getDatum().isEmpty()) {
                    einzelSpiel.setDatum(lastDatum);
                } else {
                    lastDatum = einzelSpiel.getDatum();
                }
                ergebnisse.addSpiel(einzelSpiel);
                idxT = resultrow.end;
            }
        }
    }

    private Spieler.EinzelSpiel parseEinzelspielTableRow(ParseResult resultrow) {
        ParseResult result = readBetweenOpenTag(resultrow.result, 0, "<td", "</td>", true);
        String datum = safeResult(result);
        result = readBetweenOpenTag(resultrow.result, result.end - 3, "<td", "</td>", true);
        String pos = safeResult(result);
        result = readBetweenOpenTag(resultrow.result, result.end - 3, "<td", "</td>", true);
        String gegner = readHrefAndATag(safeResult(result))[1];
        result = readBetweenOpenTag(resultrow.result, result.end - 3, "<td", "</td>", true);
        String erg = safeResult(result);
        erg = Html.fromHtml(erg).toString();
        String saetze = "";
        for (int i = 1; i < 6; i++) {
            result = readBetweenOpenTag(resultrow.result, result.end - 3, "<td", "</td>", true);
            saetze += safeResult(result);
            saetze += " ";
        }
        result = readBetweenOpenTag(resultrow.result, result.end - 3, "<td", "</td>", true);
        String gegnerM = safeResult(result);
        Spieler.EinzelSpiel spiel = new Spieler.EinzelSpiel(datum, pos, gegner, erg, saetze.trim(), gegnerM);
        return spiel;
    }

    private String cleanHtml(ParseResult result) {
        if (result == null) {
            return null;
        }
        String ret = result.result;
        ret = ret.replaceAll("<b>", " ");
        ret = ret.replaceAll("</b>", " ");
        ret = replaceMultipleSpaces(ret);
        ret = ret.replaceAll("<br />", "\n");
        ret = ret.replaceAll("\n ", "\n");
        ret = ret.replaceAll(" \n", "\n");
        ret = removeLastNewLine(ret);
        return ret;
    }

    protected String replaceMultipleSpaces(String s) {
        s = s.replaceAll("\\s{2,}", " ");
        return s;
    }
}
