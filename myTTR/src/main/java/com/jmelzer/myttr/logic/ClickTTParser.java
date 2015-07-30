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
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;
import com.jmelzer.myttr.util.UrlUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Parse the click tt pages
 */
public class ClickTTParser extends AbstractBaseParser {

    private static final String[] retiredStrings = new String[]{"zurückgezogen", "aufgelöst", "Relegationsverzicht"};

    /**
     * parsed die Ergebnisse, und füllt das Liga objekt
     * siehe http://dttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/groupPage?displayTyp=vorrunde&displayDetail=meetings&championship=DTTB+14%2F15&group=223193
     */
    Liga parseErgebnisse(Liga liga, String page, Liga.Spielplan spielplan) {
        ParseResult table = readBetween(page, 0, "<table class=\"result-set\"", "</table>");
        if (table == null) {
            return liga;
        }

        int c = 0;
        int idx = 0;
        String lastDate = null;
        liga.clearSpiele(spielplan);
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
            liga.addSpiel(m, spielplan);


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
                ergebnis, UrlUtil.safeUrl(liga.getHttpAndDomain(), url), genehmigt);
    }

    private Mannschaft findMannschaft(Liga liga, String name) {
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            if (mannschaft.getName().equals(name)) {
                return mannschaft;
            }
        }
        return new Mannschaft(name);
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
        liga.clearMannschaften();
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
        } else {
            int p1 = page.indexOf("Spielplan (Gesamt)");
            if (p1 > 0) {
                //ok, but we have to go back to find the matching href
                int p = page.lastIndexOf("<a", p1);
                if (p > 0) {
                    result = readBetween(page.substring(p, p1), 0, "ref=\"", "\"");
                    liga.setUrlGesamt(result.result);
                }
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
        if (containsRetiredString(resultrow.result)) {
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

    private boolean containsRetiredString(String line) {
        for (String retiredString : retiredStrings) {
            if (line.contains(retiredString)) {
                return true;
            }
        }
        return false;
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
                spielbericht.setSpieler1Url(UrlUtil.safeUrl(mannschaftspiel.getHttpAndDomain(), spielbericht.getSpieler1Url()));
                spielbericht.setSpieler2Url(UrlUtil.safeUrl(mannschaftspiel.getHttpAndDomain(), spielbericht.getSpieler2Url()));
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

    public void readDetail(Mannschaftspiel spiel) throws NetworkException {
        String url = spiel.getUrlDetail();
        String page = Client.getPage(url);
        parseMannschaftspiel(page, spiel);
    }

    public void readVR(Liga liga) throws NetworkException {
        if (liga.getUrlVR() != null) {
            String url = liga.getHttpAndDomain() + liga.getUrlVR();
            String page = Client.getPage(url);
            parseErgebnisse(liga, page, Liga.Spielplan.VR);
        }
    }

    public void readRR(Liga liga) throws NetworkException {
        if (liga.getUrlRR() != null) {
            String url = liga.getHttpAndDomain() + liga.getUrlRR();
            String page = Client.getPage(url);
            parseErgebnisse(liga, page, Liga.Spielplan.RR);
        }
    }

    public void readGesamtSpielplan(Liga liga) throws NetworkException {
        if (liga.getUrlGesamt() != null) {
            String url = liga.getHttpAndDomain() + liga.getUrlGesamt();
            String page = Client.getPage(url);
            parseErgebnisse(liga, page, Liga.Spielplan.GESAMT);
        }
    }

    /**
     * read the ligen from the url inside the verband
     */
    public void readLigen(Verband verband, Saison saison) throws NetworkException {
        String url = "";
        url += verband.getUrlFixed(saison);
        String page = Client.getPage(url);
        List<Liga> ligen = parseLigaLinks(page);
        verband.addAllLigen(ligen);
    }

    /**
     * read the ligen and Bezirke from the url inside the verband
     */
    public void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException {
        String url = "";
        url += verband.getUrlFixed(saison);
        String page = Client.getPage(url);
        List<Liga> ligen = parseLigaLinks(page);
        verband.addAllLigen(ligen);
        List<Bezirk> list = parseLinksBezirke(page);
        verband.setBezirkList(list);
    }

    /**
     * read the ligen from the url inside the verband
     */
    public void readBezirke(Verband verband, Saison saison) throws NetworkException {
        String url = verband.getUrlFixed(saison);
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
        //prevent from parsing wrong tags
        boolean hasRealBezirke = false;
        while (true) {
            ParseResult resultUl = readBetween(result.result, idx, "<h2 class=\"liga-layer-down\">", "</h2>");
            if (!hasRealBezirke && (resultUl == null || resultUl.isEmpty())) {
                //sometime there is no h2, check another one
                resultUl = readBetween(result.result, idx, "<li>", "</li>");
                if (resultUl == null || resultUl.isEmpty()) {
                    break;
                }
            } else {
                hasRealBezirke = true;
            }
            if (resultUl == null) {
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
        if (result == null || result.isEmpty()) {
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

    void parseDetail(String page, Mannschaft mannschaft) {
        ParseResult result = readBetween(page, 0, "Mannschaftskontakt", null);
        if (result != null) {
            result = readBetween(result.result, 0, "<td>", "</td>");
            if (result != null && !result.isEmpty()) {
                String k = cleanKontakt(result);
                mannschaft.setKontakt(k);
                result = readBetween(result.result, 0, "encodeEmail(", ")");
                if (result != null && !result.isEmpty()) {
                    mannschaft.setMailTo(unencodeMail(result.result));
                }
            }
        }
        mannschaft.removeAllSpielLokale();
        result = readBetween(page, 0, "<b>Verein</b>", null);
        String[] aref = readHrefAndATag(result.result);
        mannschaft.setVereinUrl(UrlUtil.safeUrl(mannschaft.getHttpAndDomain(), aref[0]));
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
//        Spielerbilanzen
        result = readBetween(page, 0, "<h2>Spielerbilanzen", null);
        if (result != null) {
            ParseResult resultTable = readBetweenOpenTag(result.result, 0, "<table class=\"result-set\"", "</table>");
            int idx = 0;
            int c = 0;
            List<String> header = null;
            mannschaft.clearBilanzen();
            while (true) {
                //go threw entries in current table
                ParseResult resultrow = readBetweenOpenTag(resultTable.result, idx, "<tr", "</tr>");
                if (isEmpty(resultrow)) {
                    break;
                }
                if (c++ < 1) {
                    header = readHeaderBilanzen(resultrow.result);
                    idx = resultrow.end;
                    continue;//skip first header row
                }
                Mannschaft.SpielerBilanz bilanz = parseBilanzRow(resultrow.result, header);
                if (bilanz == null) {
                    break;
                }
                mannschaft.addBilanz(bilanz);
                idx = resultrow.end;
            }
        }
    }

    private String cleanKontakt(ParseResult result) {
        String k = result.result;
//            remove mail
        k = k.replaceAll("<script.*", "");
        k = k.replaceAll("<br />", "\n");
        k = k.replaceAll("<br/>", "\n");
        if (k.endsWith("\n")) {
            k = k.substring(0, k.length() - 1);
        }
        return k;
    }

    /**
     * parse the header like<br>
     * Rang 	Name, Vorname 	  	Einsätze 	Einzel/Doppel 	1+2 	3+4 	5+6 	gesamt<br>
     * or <br>
     * Rang 	Name, Vorname 	  	Einsätze 	Einzel/Doppel 	1 	2 	3 	4 	5 	6 	gesamt
     *
     * @param header to parse
     * @return list of paarkreuz results 1+2 or 1 2
     */
    private List<String> readHeaderBilanzen(String header) {
        List<String> entries = new ArrayList<>();
        int idx = 0;
        while (true) {
            String entry = null;
            ParseResult result = readBetweenOpenTag(header, idx, "<th", "</th>", true);
            if (result == null || result.result.equals("gesamt")) {
                break;
            }
            try {
                entry = Integer.valueOf(result.result).toString();
            } catch (NumberFormatException e) {
                //ignore here
                if (result.result.contains("+")) {
                    entry = result.result;
                }
            }
            if (entry != null) {
                entries.add(entry);
            }

            idx = result.end - 3;
        }
        return entries;
    }

    private Mannschaft.SpielerBilanz parseBilanzRow(String row, List<String> header) {
        ParseResult result = readBetweenOpenTag(row, 0, "<td", "</td>", true);
        if (isEmpty(result)) {
            return null;
        }
        String pos = safeResult(result);
        result = readBetweenOpenTag(row, result.end - 3, "<td", "</td>", true);
        String name = readHrefAndATag(safeResult(result))[1];
        result = readBetweenOpenTag(row, result.end - 3, "<td", "</td>", true);
        //skip next empty column
        result = readBetweenOpenTag(row, result.end - 3, "<td", "</td>", true);
        String einsaetze = safeResult(result);
        int e = Integer.valueOf(einsaetze);
        if (e > 0) {
            //skip doppel
            result = readBetweenOpenTag(row, result.end - 3, "<td", "</td>", true);

            List<String[]> posResults = new ArrayList<>();

            String saetze = "";
            for (int i = 0; i < header.size(); i++) {
                result = readBetweenOpenTag(row, result.end - 3, "<td", "</td>", true);
                if (result != null && !result.isEmpty()) {
                    posResults.add(new String[]{header.get(i), safeResult(result)});
                }
            }
            result = readBetweenOpenTag(row, result.end - 3, "<td", "</td>", true);
            String gesamt = safeResult(result);
            return new Mannschaft.SpielerBilanz(pos, name, einsaetze, posResults, gesamt);
        } else {
            return new Mannschaft.SpielerBilanz(pos, name, einsaetze);
        }
    }

    String cleanupSpielLokalHtml(String s) {
//        String result = s.replaceAll("</b>\[ ]{2,}", "");
        String result = s.replaceAll("\\s{2,}", " ").replace("</b>", "");
        result = result.replaceAll("<script.*</script>", "");
        result = result.replaceAll("<br />", "\n");
        result = result.replaceAll("<br/>", "\n");
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

    public Spieler readSpielerDetail(String name, String url) throws NetworkException {
        String page = Client.getPage(url);
        return parseSpieler(name, page);
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
        ParseResult ligaResult = readBetween(page, 0, "<h1", "</h1>");
        ligaResult = readBetween(ligaResult.result, 0, "<br />", "<br />");
        spieler.setClubName(ligaResult.result);
        //starting point
        ParseResult result = readBetween(page, 0, "Mannschaftsmeldung", null);
        result = readBetweenOpenTag(result.result, 0, "<td>", "</td>");
        spieler.setMeldung(cleanHtml(result));

        result = readBetween(page, 0, "Mannschaftseinsätze", null);
        result = readBetweenOpenTag(result.result, 0, "<td>", "</td>");
        String einsaetze = result.result;
        while (true) {
            ParseResult r = readBetween(einsaetze, 0, null, ":</b>");
            if (r == null) {
                break;
            }
            String kat = r.result;
            String[] aref = readHrefAndATag(einsaetze);

            spieler.addEinsatz(kat, aref[1], aref[0]);
            int idx = einsaetze.indexOf("<b>");
            if (idx == -1) {
                break;
            }
            einsaetze = einsaetze.substring(idx + 3);
        }
        result = readBetween(page, 0, "Einzelbilanzen", null);
        result = readBetweenOpenTag(result.result, 0, "<td>", "</td>");
        String bilanz = result.result;
        while (true) {
            ParseResult r = readBetween(bilanz, 0, null, ":</b>");
            if (r == null) {
                break;
            }
            String kat = r.result;
            ParseResult ergebnis = readBetween(bilanz, 0, "</b>", "<br />");

            spieler.addBilanz(kat, ergebnis.result);
            int idx = bilanz.indexOf("<b>");
            if (idx == -1) {
                break;
            }
            bilanz = bilanz.substring(idx + 3);
        }
        parseEinzelSpiele(spieler, page);
        return spieler;
    }

    private void parseEinzelSpiele(Spieler spieler, String page) {
        ParseResult resultStart = readBetween(page, 0, "Einzel-Spiele", null);
        if (resultStart == null) {
            return;
        }
        ParseResult resultTable = readBetweenOpenTag(resultStart.result, 0, "<table class=\"result-set\"", "</table>");
        if (resultTable == null) {
            return;
        }

        int idx = 0;
        while (true) {
            //every child table have a header in h2
            ParseResult resultE = readBetweenOpenTag(resultTable.result, idx, "<h2", "</h2>");
            if (resultE == null) {
                break;
            }
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

    /**
     * read the ligen from the url inside the verband
     */
    public Verein readVerein(Mannschaft mannschaft) throws NetworkException {
        String url = mannschaft.getVereinUrl();
        String page = Client.getPage(url);
        return parseVerein(page);
    }

    Verein parseVerein(String page) {
        ParseResult resultStart = readBetween(page, 0, "<h1>", null);
        if (resultStart == null) {
            return null;
        }
        ParseResult result = readBetween(resultStart.result, 0, "<br />", "</h1>");
        Verein verein = new Verein();
        verein.setName(result.result.trim());


        ParseResult resultKontakt = readBetween(resultStart.result, 0, "<h2>Kontaktadresse</h2>", null);
        result = readBetween(resultKontakt.result, 0, "<p>", "encodeEmail");
        String k = cleanKontakt(result);

        result = readBetween(resultKontakt.result, 0, "encodeEmail(", ")");
        String mail = null;
        if (result != null && !result.isEmpty()) {
            mail = (unencodeMail(result.result));
        }
        String[] aref = readHrefAndATag(resultKontakt.result);
        Verein.Kontakt kontakt = new Verein.Kontakt(k, mail, aref[0]);
        verein.setKontakt(kontakt);

        //read spiellokale
        verein.removeAllSpielLokale();
        ParseResult resultLokale = readBetween(page, 0, "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">", "</table>");
        int idx = 0;
        while (true) {

            //block with spiellokale
            result = readBetween(resultLokale.result, idx, "<p>", "</p>");
            if (result != null && !result.isEmpty()) {
                String noa = result.result.contains("<a") ?
                        readBetween(result.result, 0, null, "<a").result : result.result;
                Verein.SpielLokal lokal = new Verein.SpielLokal();
                lokal.text = cleanupSpielLokalHtml(noa).replaceAll("<h2.*<p>", "");
                if (result.result.contains("<a href=\"http://route.web")) {
                    String ref = readBetween(result.result, 0, "<a href=\"http://route.web", "</a>").result;
                    try {
                        lokal.city = readBetween(ref, 0, "tocity=", "&").result;
                        lokal.street = readBetween(ref, 0, "tostreet=", "&").result;
                        lokal.plz = readBetween(ref, 0, "toplz=", "&").result;
                    } catch (NullPointerException e) {
                        //ignore
                    }
                }
                verein.addSpielLokal(lokal);
                idx = result.end;

            } else {
                break;
            }
        }

        //spielbetrieb parsen
        ParseResult table = readBetween(page, 0, "<h2>Spielbetrieb - Rückschau</h2>", "</table>");
        int c = 0;
        idx = 0;
        String lastDate = null;
        while (table.result != null) {
            ParseResult resultrow = readBetweenOpenTag(table.result, idx, "<tr", "</tr>");

            if (isEmpty(resultrow)) {
                break;
            }
            if (c++ == 0) {
                idx = resultrow.end;
                continue;//skip first row

            }
            idx = resultrow.end - 1;

            Mannschaftspiel m = parseVereinSpieleTableRow(resultrow);
            if (m.getDate() == null || m.getDate().equals(" ")) {
                m.setDate(lastDate);
            } else {
                lastDate = m.getDate();
            }
            verein.addLetztesSpiel(m);


        }
        return verein;
    }

    private Mannschaftspiel parseVereinSpieleTableRow(ParseResult resultrow) {
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
        //Liga: we don't use
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
        boolean genehmigt = resultrow.result.contains("genehmigt");
        return new Mannschaftspiel(datum,
                new Mannschaft(heimMannsschaft),
                new Mannschaft(gastMannsschaft),
                ergebnis, url, genehmigt);
    }

    void parseVereinMannschaften(String page, Verein verein) {
        ParseResult resultStart = readBetween(page, 0, "Mannschaften und Ligeneinteilung", null);
        if (resultStart == null) {
            return;
        }
        ParseResult resultHeader = readBetween(resultStart.result, 0, "<h2>", "</h2>");
        while (resultHeader != null) {

            //Ueberschrift
            ParseResult trHeader = readBetween(resultStart.result, resultHeader.end, "<tr>", "</tr>");
            int startIdx = trHeader.end;
            //Zeile mit Mannschaft -- iterieren
            ParseResult tr = readBetween(resultStart.result, startIdx, "<tr>", "</tr>");
            while (true) {
                if (tr == null) {
                    break;
                }
                Verein.Mannschaft mannschaft = new Verein.Mannschaft();
                ParseResult td = readBetween(resultStart.result, startIdx, "<td>", "</td>");
                if (td == null) {
                    break;
                }
                mannschaft.name = td.result;
                td = readBetween(resultStart.result, td.end, "<td>", "</td>");
//                System.out.println("url = " + td.result);
                String aref[] = readHrefAndATag(td.result);
                mannschaft.url = aref[0];
                mannschaft.liga = aref[1];
                //Kontakt
                td = readBetween(resultStart.result, td.end, "<td>", "</td>");
                //Pos
                td = readBetween(resultStart.result, td.end, "<td>", "</td>");
                //Punkte
                td = readBetween(resultStart.result, td.end, "<td>", "</td>");
                //todo add domain to relative url
//                mannschaft.url = (UrlUtil.safeUrl(verein.getHttpAndDomain() , liga.getUrl()));
                verein.addMannschaft(mannschaft);
                ParseResult tr2 = readBetweenOpenTag(resultStart.result, td.end, "<tr", "</tr>");
                if (tr2 == null || tr2.result.contains("<h2")) {
                    //next header
                    break;
                } else {
                    startIdx = td.end;
                }
            }

            resultHeader = readBetween(resultStart.result, resultHeader.end, "<h2>", "</h2>");
        }
    }
}
