package com.jmelzer.myttr.logic.impl;

import android.text.Html;
import android.util.Log;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.AbstractBaseParser;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.MyTTPlayerIds;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jmelzer.myttr.Constants.MYTT;
import static com.jmelzer.myttr.MyApplication.saison;
import static com.jmelzer.myttr.util.UrlUtil.getHttpAndDomain;
import static com.jmelzer.myttr.util.UrlUtil.safeUrl;

/**
 * Created by J. Melzer on 26.11.2017.
 * Parse the click tt pages from mytt
 */
public class MyTTClickTTParserImpl extends AbstractBaseParser implements MyTTClickTTParser {

    public static final String CONTAINER_START_TAG = "<div class=\"col-sm-6 col-xs-12\">";
    private static final String[] retiredStrings = new String[]{"zurückgezogen", "aufgelöst", "Relegationsverzicht"};

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
            page = Client.getPage(getHttpAndDomain(url) + urlLiga);

            List<Liga> ligen = parseLigaLinks(page);
            verband.addAllLigen(ligen, saison);
        }

    }

    @Override
    public void readKreiseAndLigen(Bezirk bezirk) throws NetworkException {
        String page = Client.getPage(bezirk.getUrl());
        List<Kreis> list = parseLinksKreise(page);
        for (Kreis kreis : list) {
            kreis.setUrl(safeUrl(getHttpAndDomain(bezirk.getUrl()), kreis.getUrl()));
        }
        bezirk.setKreise(list);
        List<Liga> listLiga = parseLigaLinks(page);
        bezirk.addAllLigen(listLiga);
    }

    List<Kreis> parseLinksKreise(String page) {
        List<Kreis> kreuse = new ArrayList<>();
        ParseResult result = readBetween(page, 0, "Untergeordnete Spielklassen", "</div>");
        int start = 0;
        if (!isEmpty(result)) {
            while (true) {
                ParseResult resultP = readBetween(result.result, start, "<p", "</p>");
                if (isEmpty(resultP))
                    break;
                String ahref[] = readHrefAndATag(resultP.result);
                String url = ahref[0];
                String name = readBetween(ahref[1], 0, "<strong>", "</strong>").result;
                Kreis kreis = new Kreis(name, url);
                kreuse.add(kreis);
                start = resultP.end;
            }
        }
        return kreuse;
    }

    @Override
    public void readLiga(Liga liga) throws NetworkException {
        String url = liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(page, liga);
    }

    @Override
    public void readMannschaftsInfo(Mannschaft mannschaft) throws NetworkException {
        if (mannschaft.getUrl() == null) //zurueckgezogen
            return;
        String url = mannschaft.getUrl().substring(0, mannschaft.getUrl().indexOf("/spielerbilanzen/vr"));
        String page = Client.getPage(url + "/infos");
        parseMannschaftsDetail(page, mannschaft);
        page = Client.getPage(mannschaft.getUrl());
        parseBilanzen(page, mannschaft);
    }

    @Override
    public void readVR(Liga liga) throws NetworkException {
        if (liga.getUrlVR() != null) {
            String page = Client.getPage(liga.getUrlVR());
            parseErgebnisse(page, liga, Liga.Spielplan.VR);
        }
    }

    @Override
    public void readRR(Liga liga) throws NetworkException {
        if (liga.getUrlRR() != null) {
            String page = Client.getPage(liga.getUrlRR());
            parseErgebnisse(page, liga, Liga.Spielplan.RR);
        }
    }

    @Override
    public void readGesamtSpielplan(Liga liga) throws NetworkException {
        if (liga.getUrlGesamt() != null) {
            String url = liga.getHttpAndDomain() + liga.getUrlGesamt();
            String page = Client.getPage(url);
            parseErgebnisse(page, liga, Liga.Spielplan.GESAMT);
        }
    }

    @Override
    public void readDetail(Mannschaftspiel spiel) throws NetworkException {
        String url = spiel.getUrlDetail();
        String page = Client.getPage(url);
        parseMannschaftspiel(page, spiel);
    }

    @Override
    public void readLigen(Kreis kreis) throws NetworkException {
        String page = Client.getPage(kreis.getUrl());
        List<Liga> ligen = parseLigaLinks(page);
        kreis.addAllLigen(ligen);
    }

    @Override
    public Verein readVerein(String url) throws NetworkException {
        String page = Client.getPage(url);
        Verein v = parseVerein(page);
        v.setUrl(url);
        String url2 = url.substring(0, url.indexOf("/info")) + "/mannschaften";
        page = Client.getPage(url2);
        parseVereinMannschaften(v, page);
        url2 = url.substring(0, url.indexOf("/info")) + "/spielplan";
        page = Client.getPage(url2);
        parseVereinSpielplan(v, page);
        return v;
    }

    void parseVereinSpielplan(Verein v, String page) {
        ParseResult table = readBetween(page, 0, "<tbody>", "</table>");
        int idx = 0;
        String lastdate = "";
        while (true) {
            //mytt have a bug here: no opening tr element
            ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
            String[] row = tableRowAsArray("<td>" + resultrow.result, 8, false);
//            printRows(row);
            Mannschaftspiel m = new Mannschaftspiel();
            if (!row[0].isEmpty()) {
                lastdate = row[0];
            }
            String uhrzeit = row[1];
            uhrzeit = removeHtml(uhrzeit);
            m.setDate(lastdate + " " + uhrzeit);
            m.setErgebnis(row[7]);
            String ahref[] = readHrefAndATag(row[4]);
            Mannschaft heimMannschaft = new Mannschaft(ahref[1]);
            heimMannschaft.setUrl(MYTT + ahref[0]);
            m.setHeimMannschaft(heimMannschaft);
            ahref = readHrefAndATag(row[5]);
            Mannschaft gastMannschaft = new Mannschaft(ahref[1]);
            gastMannschaft.setUrl(MYTT + ahref[0]);
            m.setGastMannschaft(gastMannschaft);
            v.addSpielPlanSpiel(m);
        }
    }

    String removeHtml(String string) {
        string = string.replaceAll("\r\n", "");
        return string.replaceAll("<a.*</a>", "");
    }

    private void printRows(String[] row) {
        for (int i = 0; i < row.length; i++) {
            System.out.println(i + " = " + row[i]);
        }
    }

    @Override
    public Spieler readPopUp(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException {
        String page = Client.getPage(myTTPlayerIdsForPlayer.buildPopupUrl());
        return parseLinksForPlayer(page, name);
    }

    @Override
    public Verband readTopLigen() throws NetworkException {
        String page = Client.getPage(Verband.dttb.getMyTTClickTTUrl());
        List<Liga> ligen = parseLigaLinks(page);
        Verband.dttb.addAllLigen(ligen, saison);
        return Verband.dttb;
    }

    @Override
    public Spieler readSpielerDetail(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException {
        Spieler spieler = readPopUp(name, myTTPlayerIdsForPlayer);
        String page = Client.getPage(spieler.getMytTTClickTTUrl());
        return parseSpieler(spieler, page);
    }

    /**
     * parse the result of the click tt detail
     * e.g. https://www.mytischtennis.de/clicktt/WTTV/17-18/spieler/143001491/spielerportrait
     *
     * @param page to be parsed
     * @return spieler never null, maybe empty
     */
    Spieler parseSpieler(Spieler spieler, String page) {
        ParseResult pr = readBetween(page, 0, "<div class=\"panel-body\">", "</div>");
        ParseResult linkResult = readBetween(pr.result, 0, "<a", "</a>");
        linkResult = readBetween(pr.result, linkResult.end, "<a", "</a>");
        if (!isEmpty(linkResult)) {
            String ahref[] = readHrefAndATag("<a" + linkResult.result + "</a>");
            if (ahref != null) {
                spieler.setClubName(ahref[1]);
                spieler.setClubUrl(ahref[0]);
            }

        }
        spieler.setPosition("unbekannt");

        ParseResult eResult = readBetween(page, 0, "<h3>Mannschafts", null);
        int idx = 0;
        while (true) {
            ParseResult kat = readBetween(eResult.result, idx, "<strong>", "</strong>");
            linkResult = readBetween(eResult.result, idx, "<a", "</a>");
            if (!isEmpty(linkResult) && !isEmpty(kat)) {
                String ahref[] = readHrefAndATag("<a" + linkResult.result + "</a>");
                if (ahref != null) {
                    spieler.addEinsatz(kat.result.substring(0, kat.result.length() - 1), ahref[1], MYTT + ahref[0]);
                }
                idx = linkResult.end;

            } else {
                break;
            }
        }

        ParseResult start = readBetween(page, 0, "<div role=\"tabpanel\" class=\"tab-pane active\" id=\"single\">",
                "<div role=\"tabpanel\" class=\"tab-pane\" id=\"double\">");
        int h3Idx = 0;
        while (true) {
            ParseResult h3 = readBetween(start.result, h3Idx, "<h3>", "</h3>");
            if (isEmpty(h3)) {
                break;
            }
            h3Idx = h3.end;

            idx = 0;
            Spieler.LigaErgebnisse ergebnisse = new Spieler.LigaErgebnisse(h3.result);
            spieler.addLigaErgebnisse(ergebnisse);
            String lastDate = "";
            String lastTeam = "";
            ParseResult table = readBetween(start.result, h3Idx, "<tbody>", "</table>");
            while (true) {
                //mytt have a bug here: no opening tr element
                ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
                if (isEmpty(resultrow)) {
                    break;
                }
                String[] row = tableRowAsArray("<td>" + resultrow.result, 9, false);

                Spieler.EinzelSpiel einzelSpiel = parseEinzelspielTableRow(row);
                if (einzelSpiel.getDatum() == null) {
                    einzelSpiel.setDatum(lastDate);
                } else {
                    lastDate = einzelSpiel.getDatum();
                }
                if (einzelSpiel.getGegnerMannschaft() == null) {
                    einzelSpiel.setGegnerMannschaft(lastTeam);
                } else {
                    lastTeam = einzelSpiel.getGegnerMannschaft();
                }
                ergebnisse.addSpiel(einzelSpiel);
                idx = resultrow.end;
            }
        }
        return spieler;
    }

    private Spieler.EinzelSpiel parseEinzelspielTableRow(String[] row) {
        String datum = safeResult(readBetween(row[0], 0, null, "<br>"));
        String pos = row[1].replace("&minus;", "-");
        String gegner = readHrefAndATag(row[2])[1];
        String erg = safeResult(readBetweenOpenTag(row[8], 0, "<a", " <i"));
        StringBuilder saetze = new StringBuilder();
        for (int i = 3; i < 8; i++) {
            saetze.append(row[i]);
            saetze.append(" ");
        }
        String gegnerM = safeResult(readBetween(row[0], 0, "<small>", "</small>"));
        Spieler.EinzelSpiel spiel = new Spieler.EinzelSpiel(datum, pos, gegner, erg, saetze.toString().trim(), gegnerM);

        return spiel;
    }

    Verein parseVerein(String page) {
        ParseResult resultStart = readBetween(page, 0, "<div class=\"panel-body\">", null);
        if (resultStart == null) {
            return null;
        }
        ParseResult result = readBetween(resultStart.result, 0, "<h1>", "<small>");
        Verein verein = new Verein();
        verein.setName(result.result.trim());

        result = readBetween(resultStart.result, 0, "<h4>Kontaktadresse", "</div>");
        if (!isEmpty(result)) {
            ParseResult resultK = readBetween(resultStart.result, 0, "/h4>", "<i");
            ParseResult resultM = readBetween(resultStart.result, 0, "<i class=\"icon-envelope\">", null);
            String mail = null;
            if (!isEmpty(resultM)) {
                String[] ahref = readHrefAndATag(resultM.result);
                mail = ahref[1];
            }
            ParseResult resultU = readBetween(resultStart.result, 0, "<i class=\"icon-home\">", null);
            String url = null;
            if (!isEmpty(resultU)) {
                String[] ahref = readHrefAndATag(resultU.result);
                url = ahref[0];
            }
            Verein.Kontakt kontakt = new Verein.Kontakt(cleanHtml(resultK), mail, url);
            verein.setKontakt(kontakt);
        }
        verein.addSpielLokale(parseSpielLokale(resultStart));
        //todo letzte spiele
        //todo nächste spiele
        //todo mannschaften

        return verein;
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
            Spielbericht spielbericht = new Spielbericht();
            spiel.addSpiel(spielbericht);
            spielbericht.setName(row[0]);
            if (row[0].startsWith("D")) {
                //no link for double player
                String line = row[2];
                String[] ahref = readHrefAndATag(line);

                spielbericht.setSpieler1Name(ahref[1]);
                line = line.substring(line.indexOf("<br"));
                ahref = readHrefAndATag(line);
                spielbericht.setSpieler1Name(spielbericht.getSpieler1Name() + " / " + ahref[1]);

                line = row[4];
                ahref = readHrefAndATag(line);

                spielbericht.setSpieler2Name(ahref[1]);
                line = line.substring(line.indexOf("<br"));
                ahref = readHrefAndATag(line);
                spielbericht.setSpieler2Name(spielbericht.getSpieler2Name() + " / " + ahref[1]);
            } else {
                String line = row[2];
                spielbericht.setMyTTPlayerIdsForPlayer1(parsePlayerIds(line));
                String[] ahref = readHrefAndATag(line);
                spielbericht.setSpieler1Name(ahref[1]);

                line = row[4];
                spielbericht.setMyTTPlayerIdsForPlayer2(parsePlayerIds(line));

                ahref = readHrefAndATag(line);
                spielbericht.setSpieler2Name(ahref[1]);
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
            String datum = row[0];
            String url = readHrefAndATag(row[5])[0];
            if (url != null && !url.isEmpty())
                url = MYTT + url;
//            String datum = row[0].substring(0,row[0].length()-2);
            Mannschaftspiel mannschaftspiel = new Mannschaftspiel(datum,
                    findMannschaft(liga, readHrefAndATag(row[3])[1]),
                    findMannschaft(liga, readHrefAndATag(row[4])[1]),
                    readHrefAndATag(row[5])[1],
                    url,
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
        Log.d(Constants.LOG_TAG, "parseMannschaftsDetail ... ");
        ParseResult result = readBetween(page, 0, "<h3>Verein", "</li>");
        mannschaft.clearLokale();
        if (!isEmpty(result)) {

            String[] ahref = readHrefAndATag(result.result);
            if (ahref != null) {
                mannschaft.setVereinUrl(UrlUtil.safeUrl(mannschaft.getHttpAndDomain(), ahref[0]));
            }
            mannschaft.addSpielLokale(parseSpielLokale(result));
        }
        result = readBetween(page, 0, "Mannschaftskontakt", "</li>");
        if (!isEmpty(result)) {
            ParseResult resultK = readBetween(result.result, 0, "<strong>", "</strong>");
            if (!isEmpty(resultK)) {
                mannschaft.setKontakt(resultK.result);
            }
            String[] ahref = readHrefAndATag(result.result);
            if (ahref != null) {
                mannschaft.setMailTo(cleanMail(ahref[0]));
            }

            ParseResult resultNr = readBetween(result.result, 0, "<i class=\"icon-phone\"></i>", "<");
            if (!isEmpty(resultNr)) {
                mannschaft.setKontaktNr(resultNr.result);
                resultNr = readBetween(result.result, resultNr.end, "<i class=\"icon-phone\"></i>", "<");
                if (!isEmpty(resultNr))
                    mannschaft.setKontaktNr2(resultNr.result);
            }
        }
    }

    List<String> parseSpielLokale(ParseResult result) {
        List<String> lokale = new ArrayList<>();
        int idx = 0;
        while (true) {
            ParseResult resultLokal = readBetween(result.result, idx, "<h4>", "</h4>");
            if (resultLokal == null || resultLokal.isEmpty()) {
                break;
            }
            idx = resultLokal.end;
            if (!resultLokal.result.contains("Spiellokal")) {
                continue;
            }
            resultLokal = readBetween(result.result, idx, null, "</div>");
            String lokal = cleanupSpielLokalHtml(resultLokal.result);
            lokale.add(lokal);
        }
        return lokale;
    }

    String cleanupSpielLokalHtml(String s) {
        String result = s.replaceAll("\r\n", "");
        result = result.replaceAll("<br>", "\n");
        result = result.replaceAll("<br/>", "\n");
        result = result.replaceAll("<br />", "\n");
        result = result.replaceAll("\n\n", "\n");
        result = result.replaceAll("<i class.*", "");
        //remove the last \n
        result = removeLastNewLine(result);
        return result.trim();
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
            String name = href[1];

            if (name == null) {
                ParseResult rn = readBetweenOpenTag(nameWithRef, 0, "<span class=\"red",
                        "</span>");
                if (!isEmpty(rn)) {
                    name = readBetween(rn.result, 0, null, "<i").result;
                    name += " --- zurückgezogen";
                }

            }
            Mannschaft m;
            if (containsRetiredString(resultrow.result)) {
                m = new Mannschaft(name);
            } else {
                m = new Mannschaft(name,
                        Integer.valueOf(row[1]),
                        Integer.valueOf(row[3]),
                        Integer.valueOf(row[4]),
                        Integer.valueOf(row[5]),
                        Integer.valueOf(row[6]),
                        row[7],
                        row[8],
                        row[9], href[0]);
                m.setUrl(safeUrl(liga.getHttpAndDomain(), m.getUrl()));
            }
            liga.addMannschaft(m);
            idx = resultrow.end;

        }
    }

    private void parseSpielplanLinks(Liga liga, String page) {
        String url = liga.getUrl();
        url = url.substring(0, url.indexOf("/tabelle"));
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
                if (!isEmpty(rn)) {
                    Bezirk v = new Bezirk(rn.result, urlref[0]);
                    bezirkList.add(v);
                }

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

    Spieler parseLinksForPlayer(String page, String name) {
        Spieler spieler = new Spieler(name);
        Map<String, String> links = new HashMap<>();
        int idx = 0;
        while (true) {
            ParseResult pr = readBetween(page, idx, "<a", "</a>");
            if (isEmpty(pr))
                break;
            String ahref[] = readHrefAndATag("<a" + pr.result + "</a>");
            if (ahref == null) break;
            idx = pr.end;
            if (ahref[1].contains("click-TT-Spielerportrait"))
                ahref[1] = "click-TT-Spielerportrait";
            links.put(ahref[1], MYTT + ahref[0]);
        }
        spieler.setHead2head(links.get("Head to Head Ergebnisse"));
        spieler.setMytTTClickTTUrl(links.get("click-TT-Spielerportrait"));
        if (links.get("TTR-Historie").equals("https://www.mytischtennis.de/community/events")) {
            spieler.setIsOwnPlayer(true);
        } else {
            spieler.setPersonId(Long.valueOf(readBetween(links.get("TTR-Historie"), 0, "personId=", null).result));
        }
        return spieler;
    }

    void parseVereinMannschaften(Verein v, String page) {
        ParseResult table = readBetween(page, 0, "<tbody>", "</table>");
        int idx = 0;
        while (true) {
            //mytt have a bug here: no opening tr element
            ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
            String[] row = tableRowAsArray("<td>" + resultrow.result, 9, false);
            Verein.Mannschaft mannschaft = new Verein.Mannschaft();
            String aref[] = readHrefAndATag(row[0]);
            mannschaft.name = aref[1];
            aref = readHrefAndATag(row[1]);
            mannschaft.url = MYTT + aref[0];
            mannschaft.liga = aref[1];
            v.addMannschaft(mannschaft);
        }
    }

    void parseBilanzen(String page, Mannschaft mannschaft) {
        ParseResult table = readBetween(page, 0, "gamestatsTable", null);
        mannschaft.clearBilanzen();
        int idx = 0;
        while (true) {
            //mytt have a bug here: no opening tr element
            ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
            String[] row = tableRowAsArray("<td>" + resultrow.result, 10, false);
//            printRows(row);
            if (row[1].isEmpty())
                break;

            List<String[]> posResults = new ArrayList<>();
            for (int i = 3; i < 8; i++) {
                if (!row[i].isEmpty())
                    posResults.add(new String[]{"" + (i - 2), row[i]});

            }
            String gesamt = row[9].replace("\r\n", "");
            String[] ahref = readHrefAndATag(row[1]);
            MyTTPlayerIds ids = parsePlayerIds(row[1]);
            Mannschaft.SpielerBilanz bilanz = new Mannschaft.SpielerBilanz(row[0],
                    ahref[1],
                    row[2], posResults, gesamt, ids);
            mannschaft.addBilanz(bilanz);
        }
    }

    MyTTPlayerIds parsePlayerIds(String line) {
        ParseResult personId = readBetween(line, 0, "personId: '", "'");
        ParseResult clubNr = readBetween(line, 0, "clubNr: '", "'");
        return new MyTTPlayerIds(personId.result, clubNr.result);
    }

    private boolean containsRetiredString(String line) {
        for (String retiredString : retiredStrings) {
            if (line.contains(retiredString)) {
                return true;
            }
        }
        return false;
    }
}
