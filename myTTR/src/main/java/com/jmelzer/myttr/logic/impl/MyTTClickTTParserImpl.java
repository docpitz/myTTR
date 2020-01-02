package com.jmelzer.myttr.logic.impl;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.SpielerAndBilanz;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.AbstractBaseParser;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.LigaPosType;
import com.jmelzer.myttr.model.MyTTPlayerIds;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;
import com.jmelzer.myttr.util.UrlUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.jmelzer.myttr.Constants.MYTT;
import static com.jmelzer.myttr.MyApplication.saison;
import static com.jmelzer.myttr.Verband.dttb;
import static com.jmelzer.myttr.util.UrlUtil.getHttpAndDomain;
import static com.jmelzer.myttr.util.UrlUtil.safeUrl;

/**
 * Created by J. Melzer on 26.11.2017.
 * Parse the click tt pages from mytt
 */
public class MyTTClickTTParserImpl extends AbstractBaseParser implements MyTTClickTTParser {

    public static final String CONTAINER_START_TAG = "<div class=\"col-sm-4 col-xs-12\">";
    private static final String[] retiredStrings = new String[]{"zurückgezogen", "aufgelöst", "Relegationsverzicht", "Teilnahmeverzicht"};
    public static final String EMPTY_NAME = "--------";
    final SimpleDateFormat sdf = new SimpleDateFormat("E dd.MM.yy HH:mm", Locale.GERMANY);

    public String parseError(String page) {
        if (page == null) {
            return "unbekannt";
        }

        ParseResult result = readBetweenOpenTag(page, 0, "<p class=\"alert alert-danger\"", "</p>");
        return "Mytischtennis Meldung: " + safeResult(result);
    }

    @Override
    public void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException, LoginExpiredException {
        String url = "";
        url += verband.getUrlFixed(saison);
        String page = Client.getPage(url);
        List<Bezirk> list = parseLinksBezirke(page);
        verband.setBezirkList(list, saison);

        List<Liga> ligen = parseLigaLinks(page);
        verband.addAllLigen(ligen, saison);

        //read link to ligen and load it
        ParseResult result = readBetween(page, 0, "<div class=\"row m-l text-center\">",
                "</div>");
        if (ligen.isEmpty() && !isEmpty(result)) {
            String urlLiga = readHrefAndATag(result.result)[0];
            page = Client.getPage(getHttpAndDomain(url) + urlLiga);

            ligen = parseLigaLinks(page);
            verband.addAllLigen(ligen, saison);
        }

    }

    @Override
    public void readKreiseAndLigen(Bezirk bezirk) throws NetworkException, LoginExpiredException {
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
                if (isEmpty(resultP)) {
                    break;
                }
                start = resultP.end;
                String[] ahref = readHrefAndATag(resultP.result);
                String url = ahref[0];
                if (url == null) {
                    continue;
                }

                String name = readBetween(ahref[1], 0, "<strong>", "</strong>").result;
                Kreis kreis = new Kreis(name, url);
                kreuse.add(kreis);
            }
        }
        return kreuse;
    }

    @Override
    public void readLiga(Liga liga) throws NetworkException, LoginExpiredException, ValidationException, NoClickTTException {
        String url = liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(page, liga);
    }

    @Override
    public void readMannschaftsInfo(Mannschaft mannschaft) throws NetworkException, LoginExpiredException {
        if (mannschaft.getUrl() == null) //zurueckgezogen
        {
            return;
        }

        //https://www.mytischtennis.de/clicktt/xxxxx/gruppe/345988/mannschaft/2190980/TV-Bergheim-IV/spielerbilanzen/vr
        //->
        //https://www.mytischtennis.de/clicktt/xxxxx/gruppe/345988/mannschaft/2190980/TV-Bergheim-IV/infos
        String urlInfo = mannschaft.getUrl().substring(0, mannschaft.getUrl().indexOf("/spielerbilanzen")) + "/infos";
        String urlBilanz = mannschaft.getUrl();

        String page = Client.getPage(urlInfo);
        parseMannschaftsDetail(page, mannschaft);
        page = Client.getPage(urlBilanz);
        parseBilanzen(page, mannschaft);
    }

    @Override
    public void readVR(Liga liga) throws NetworkException, LoginExpiredException {
        if (liga.getUrlVR() != null) {
            String page = Client.getPage(liga.getUrlVR());
            if (page.contains("Vorrunde")) {
                parseErgebnisse(page, liga, Liga.Spielplan.VR);
            }
        }
    }

    @Override
    public void readRR(Liga liga) throws NetworkException, LoginExpiredException {
        if (liga.getUrlRR() != null) {
            String page = Client.getPage(liga.getUrlRR());
            if (page.contains("Rückrunde")) {
                parseErgebnisse(page, liga, Liga.Spielplan.RR);
            }
        }
    }

    @Override
    public void readGesamtSpielplan(Liga liga) throws NetworkException, NoClickTTException, LoginExpiredException {
        if (liga.getUrlGesamt() != null) {
            String url = liga.getUrlGesamt();
            if (!url.contains("http")) {
                url = liga.getHttpAndDomain() + url;
            }
            String page = Client.getPage(url);
            validatePage(page);
            parseErgebnisse(page, liga, Liga.Spielplan.GESAMT);
        }
    }

    @Override
    public void readDetail(Mannschaftspiel spiel) throws NetworkException, NoClickTTException, LoginExpiredException {
        String url = spiel.getUrlDetail();
        String page = Client.getPage(url);
        validatePage(page);
        parseMannschaftspiel(page, spiel);
    }

    @Override
    public void readLigen(Kreis kreis) throws NetworkException, NoClickTTException, LoginExpiredException {
        String page = Client.getPage(kreis.getUrl());
        validatePage(page);
        List<Liga> ligen = parseLigaLinks(page);
        kreis.addAllLigen(ligen);
    }

    /**
     * e.g. https://www.mytischtennis.de/clicktt/WTTV/17-18/verein/156012/TTG-St-Augustin/info
     */
    @Override
    public Verein readVerein(String url) throws NetworkException, NoClickTTException, LoginExpiredException {
        if (url == null) {
            throw new NoClickTTException();
        }

        String page = Client.getPage(url);
        validatePage(page);
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

    void parseVereinSpielplan(Verein v, String page) throws NoClickTTException {
        ParseResult table = readBetween(page, 0, "<tbody>", "</table>");
        if (table == null) {
            throw new NoClickTTException();
        }
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
                ParseResult pr = readBetween(row[0], 0, "<span>", "</span>");
                if (!isEmpty(pr)) {
                    lastdate = pr.result;
                } else {
                    lastdate = "";
                }
            }
            String uhrzeit = row[1];
            uhrzeit = removeHtml(uhrzeit);
            m.setDate(lastdate + " " + uhrzeit);
            String[] ahref = readHrefAndATag(row[7]);
            String ergebnis = ahref[1];
            if (ergebnis == null || ergebnis.isEmpty()) {
                ergebnis = row[7];
            }
            if (ergebnis.startsWith("<")) {
                ergebnis = readBetween(ergebnis, 0, ">", "<").result;
            }
            if (!StringUtils.isEmpty(ahref[0])) {
                m.setUrlDetail(MYTT + ahref[0]);
            }
            m.setErgebnis(ergebnis);

            ahref = readHrefAndATag(row[4]);
            Mannschaft heimMannschaft = new Mannschaft(ahref[1]);
            heimMannschaft.setUrl(MYTT + ahref[0]);
            m.setHeimMannschaft(heimMannschaft);
            ahref = readHrefAndATag(row[5]);
            Mannschaft gastMannschaft = new Mannschaft(ahref[1]);
            gastMannschaft.setUrl(MYTT + ahref[0]);
            m.setGastMannschaft(gastMannschaft);

            parseAndWriteSpielLokalNummer(row[2], m, heimMannschaft);
            v.addSpielPlanSpiel(m);
        }
    }

    private void parseAndWriteSpielLokalNummer(String row, Mannschaftspiel m, Mannschaft heim) {
        String[] ahref = readHrefAndATag(row);
        m.setUrlSpielLokal(MYTT + ahref[0]);
        try {
            m.setNrSpielLokal(Integer.parseInt(ahref[1]));
            heim.setVereinId(readBetween(ahref[0], 0, "/verein/", "/").result);
        } catch (NumberFormatException e) {
            Log.d(Constants.LOG_TAG, "couldn't parse number ahref[1]:" + ahref[1]);
            m.setNrSpielLokal(-1);
        }
    }

    String removeHtml(String string) {
        string = string.replaceAll("\r\n", "");
        string = string.replaceAll("<a.*</a>", "");
        return string.replaceAll(" <script.*", "");
    }

    private void printRows(String[] row) {
        for (int i = 0; i < row.length; i++) {
            Log.d(Constants.LOG_TAG, i + " = " + row[i]);
        }
    }

    @Override
    public Spieler readPopUp(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException, NoClickTTException, LoginExpiredException {
        String page = Client.getPage(myTTPlayerIdsForPlayer.buildPopupUrl());
        validatePage(page);
        return parseLinksForPlayer(page, name);
    }

    void validatePage(String page) throws NoClickTTException {
        if (page.contains("keine Verbindung zur externen Spieler-Datenbank")) {
            throw new NoClickTTException();
        }
    }

    @Override
    public Verband readTopLigen() throws NetworkException, LoginExpiredException {
        if (dttb.getLigaList().size() == 0) {
            String page = Client.getPage(dttb.getMyTTClickTTUrl());
            List<Liga> ligen = parseLigaLinks(page);
            dttb.addAllLigen(ligen, saison);
        }
        return dttb;
    }

    /**
     * see https://www.mytischtennis.de/clicktt/home#tab_verein
     * for example
     *
     * @return Verein or null
     * @throws NetworkException      in case of error
     * @throws LoginExpiredException in case of error
     */
    @Override
    public Verein readOwnVerein() throws NetworkException, LoginExpiredException, NoClickTTException {
        String url = "https://www.mytischtennis.de/clicktt/home-tab?id=verein";
        String page = Client.getPage(url);
        validatePage(page);

        Verein v = new Verein("Dein Verein", url);
        parseVereinMannschaften(v, page);

        return v;
    }

    @Override
    public void readAdressen(Liga liga) throws LoginExpiredException, NetworkException {
        String url = liga.getUrl();
        url = url.substring(0, url.indexOf("/tabelle/gesamt")) + "/adressen";
        String page = Client.getPage(url);
        parseLigaAdressen(page, liga);

    }

    @Override
    public void readOwnAdressen(Liga liga) throws LoginExpiredException, NetworkException, NoClickTTException {
        String page = Client.getPage("https://www.mytischtennis.de/clicktt/home-tab?id=adressen");
        validatePage(page);
        parseLigaAdressen(page, liga);
    }

    @Override
    public Spieler readSpielerDetail(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException, NoClickTTException, LoginExpiredException {
        Spieler spieler = readPopUp(name, myTTPlayerIdsForPlayer);
        String page = Client.getPage(spieler.getMytTTClickTTUrl());
        validatePage(page);
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
        //todo maybe mytt knows this in the future
        spieler.setPosition("");

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
                ParseResult bilanz = readBetween(eResult, kat.end, "<br>", "</p>");
                if (!isEmpty(bilanz)) {
                    String bStr = cleanHtml(bilanz);
                    bStr = bStr.replace("RR", " RR");
                    bStr = bStr.replace("gesamt", " gesamt");
                    spieler.addBilanz(kat.result, bStr);
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

        return new Spieler.EinzelSpiel(datum, pos, gegner, erg, saetze.toString().trim(), gegnerM);
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
        verein.addSpielLokale(parseSpielLokale(resultStart.result));
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
                spielbericht.setSpieler1Name(spielbericht.getSpieler1Name() + " / " + playerName(ahref[1]));

                line = row[4];
                ahref = readHrefAndATag(line);

                spielbericht.setSpieler2Name(ahref[1]);
                line = line.substring(line.indexOf("<br"));
                ahref = readHrefAndATag(line);
                spielbericht.setSpieler2Name(spielbericht.getSpieler2Name() + " / " + playerName(ahref[1]));
            } else {
                String line = row[2];
                if (!unparsablePlayer(line)) {
                    spielbericht.setMyTTPlayerIdsForPlayer1(parsePlayerIds(line));
                    String[] ahref = readHrefAndATag(line);
                    spielbericht.setSpieler1Name(ahref[1]);
                } else {
                    spielbericht.setSpieler1Name(EMPTY_NAME);
                }

                line = row[4];
                if (!unparsablePlayer(line)) {
                    spielbericht.setMyTTPlayerIdsForPlayer2(parsePlayerIds(line));
                    String[] ahref = readHrefAndATag(line);
                    spielbericht.setSpieler2Name(ahref[1]);
                } else {
                    spielbericht.setSpieler2Name(EMPTY_NAME);
                }
            }
            for (int j = 5; j < 10; j++) {
                spielbericht.addSet(row[j]);
            }
            spielbericht.setResult(row[10]);
            idx = resultrow.end;

        }
    }

    private boolean unparsablePlayer(String line) {
        return line.contains("nicht anwesend") || line.contains("sonstiger Spieler");
    }

    private String playerName(String s) {
        if (s == null || s.isEmpty()) {
            return EMPTY_NAME;
        } else {
            return s;
        }
    }

    public void parseErgebnisse(String page, Liga liga, Liga.Spielplan spielplan) {
        int c = 0;
        int idx = 0;
        String lastDate = null;
        liga.clearSpiele(spielplan);

        ParseResult table = readBetweenOpenTag(page, 0, "<table class=\"table table-mytt", "</table>");
        if (isEmpty(table)) {
            return;
        }

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
//            printRows(row);
            String datum = row[0];
            String time = " 24:00";
            if (row[1] != null && !row[1].isEmpty()) {
                time = " " + removeHtml(row[1]);
            }

            if (datum != null && !datum.isEmpty()) {
                ParseResult pr = readBetween(datum, 0, "<span>", "</span>");
                if (!isEmpty(pr)) {
                    datum = pr.result;
                    datum = shortenDate(datum);
                    lastDate = datum;
                    datum += time;
                }
            } else if (lastDate != null) {
                datum = lastDate + time;
            }
            String url = readHrefAndATag(row[6])[0];
            if (url != null && !url.isEmpty()) {
                url = MYTT + url;
            }

            String ergebnis = readHrefAndATag(row[6])[1];
            if (ergebnis == null || ergebnis.isEmpty()) {
                ergebnis = row[6];
                url = null;
            }
            if (ergebnis.startsWith("<")) {
                ergebnis = readBetween(ergebnis, 0, ">", "<").result;
            }
            String[] ahref = readHrefAndATag(row[3]);
            Mannschaft heim = findMannschaft(liga, ahref[1]);
            ahref = readHrefAndATag(row[3]);
            heim.setUrl(MYTT + ahref[0]);
            Mannschaft gast = findMannschaft(liga, readHrefAndATag(row[4])[1]);
            Mannschaftspiel mannschaftspiel = new Mannschaftspiel(datum,
                    heim,
                    gast,
                    ergebnis,
                    url,
                    true);
            parseAndWriteSpielLokalNummer(row[2], mannschaftspiel, heim);
            if (mannschaftspiel.getDate() == null || mannschaftspiel.getDate().isEmpty()) {
                mannschaftspiel.setDate(lastDate);
            }

            try {
                //Unparseable date: "Mo 03.02.20" ??
                Date d = sdf.parse(mannschaftspiel.getDate());
                mannschaftspiel.setDateAsDate(d);
            } catch (ParseException e) {
                Log.e(Constants.LOG_TAG, "", e);
            }
            if (heim.getName() != null) {//sometimes happened
                liga.addMannschaft(heim);
                //https://www.mytischtennis.de/clicktt/DTTB/19-20/ligen/Herren-Landesliga-11/gruppe/
                // 356941/mannschaft/2232050/TTC-BW-Bruehl-Vochem-III/spielerbilanzen/vr/
                //                   ~~~~~~~
                String teamId = readBetween(heim.getUrl(), 0, "mannschaft/", "/").result;
                heim.setTeamId(teamId);
                liga.addSpiel(mannschaftspiel, spielplan);
                heim.addSpiel(mannschaftspiel);
            }
            gast.addSpiel(mannschaftspiel);
            idx = resultrow.end;

        }

    }

    @NonNull
    private String shortenDate(String datum) {
        //todo generic
        if (datum.endsWith("2018")) {
            datum = datum.substring(0, datum.length() - 4) + "18";
        } else if (datum.endsWith("2019")) {
            datum = datum.substring(0, datum.length() - 4) + "19";
        } else if (datum.endsWith("2020")) {
            datum = datum.substring(0, datum.length() - 4) + "20";
        }
        return datum;
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
        String part = "";
        ParseResult result = readBetween(page, 0, "<h3>Verein", "</li>");
        if (!isEmpty(result)) {
            part = result.result;
        }
        mannschaft.clearLokale();
        if (!part.isEmpty()) {

            String[] ahref = readHrefAndATag(part);
            if (ahref != null) {
                mannschaft.setVereinUrl(UrlUtil.safeUrl(mannschaft.getHttpAndDomain(), ahref[0]));
            }
            mannschaft.addSpielLokale(parseSpielLokale(part));
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
                if (!isEmpty(resultNr)) {
                    mannschaft.setKontaktNr2(resultNr.result);
                }
            }
        }
    }

    Map<Integer, String> parseSpielLokale(String part) {
        Map<Integer, String> lokale = new TreeMap<>();
        int idx = 0;
        while (true) {
            ParseResult resultLokal = readBetween(part, idx, "<h4>", "</h4>");
            if (resultLokal == null || resultLokal.isEmpty()) {
                break;
            }
            idx = resultLokal.end;
            if (!resultLokal.result.contains("Spiellokal")) {
                continue;
            }
            String nrS = resultLokal.result.replaceAll("^.* ", "");
            int nr = Integer.valueOf(nrS);
            resultLokal = readBetween(part, idx, null, "</div>");
            String lokal = cleanupSpielLokalHtml(resultLokal.result);
            if (lokal != null && !lokal.isEmpty()) {
                lokale.put(nr, lokal);
            }
        }
        return lokale;
    }

    String cleanupSpielLokalHtml(String s) {
        String result = s.replaceAll("\r\n", " ");
        result = result.replaceAll("<br>", "\n");
        result = result.replaceAll("<br/>", "\n");
        result = result.replaceAll("<br />", "\n");
        result = result.replaceAll("\n\n", "\n");
        result = result.replaceAll("<i class.*", "");
        result = result.replaceAll("</div><div class=\"col-sm-4 col-xs-12\">", "");
        result = result.replaceAll("\n[ \t]", "\n");
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

    void parseLiga(String page, Liga liga) throws LoginExpiredException, ValidationException, NoClickTTException {

        if (page.contains("Nicht eindeutig identifiziert")) {
            throw new LoginExpiredException();
        }
        validatePage(page);

        if (!page.contains("+/-")) {
            throw new ValidationException("Keine Tabellen-Daten vorhanden");
        }
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
//            printRows(row);
            LigaPosType ligaPosTyp = parsePosType(row[0]);
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
                m = new Mannschaft(ligaPosTyp, name,
                        Integer.valueOf(row[1]),
                        Integer.valueOf(row[3]),
                        Integer.valueOf(row[4]),
                        Integer.valueOf(row[5]),
                        Integer.valueOf(row[6]),
                        readHrefAndATag(row[7])[1].replaceAll("<.*", ""),
                        row[8],
                        row[9], href[0]);
                m.setUrl(safeUrl(liga.getHttpAndDomain(), m.getUrl()));
            }
            liga.addMannschaft(m);
            idx = resultrow.end;

        }
    }

    private LigaPosType parsePosType(String s) {
        if (s == null) {
            return LigaPosType.NOTHING;
        }
        if (s.contains("Relegation") && s.contains("green")) {
            return LigaPosType.AUF_RELEGATION;
        }
        if (s.contains("Aufsteiger")) {
            return LigaPosType.AUFSTEIGER;
        }
        if (s.contains("Relegation") && s.contains("red")) {
            return LigaPosType.AB_RELEGATION;
        }
        if (s.contains("Absteiger")) {
            return LigaPosType.ABSTEIGER;
        }

        return LigaPosType.NOTHING;
    }

    public void parseSpielplanLinks(Liga liga, String page) throws ValidationException {
        String url = liga.getUrl();
        if (url.contains("/tabelle")) {
            url = url.substring(0, url.indexOf("/tabelle"));
//            liga.setUrlVR(url + "/spielplan/vr");
//            liga.setUrlRR(url + "/spielplan/rr");
            liga.setUrlGesamt(url + "/spielplan/gesamt");
        } else {
            ParseResult result = readBetween(page, 0, "</h1>", null);
            result = readBetween(result, 0, "<div", "</div");
            //now we have all a tags to read
            ParseResult aresult = readBetween(result, 0, "<a", "</a");
            if (isEmpty(aresult)) {
                throw new ValidationException("myTTR konnte die Links nicht finden");
            }

            String href[] = readHrefAndATag(aresult.result);
            String realUrl = MYTT + href[0];
            realUrl = realUrl.replace("/tabelle/", "/spielplan/");
            realUrl = realUrl.substring(0, realUrl.length() - 3);
            liga.setUrlVR(realUrl + "/vr");
            liga.setUrlRR(realUrl + "/rr");
            liga.setUrlGesamt(null);
        }
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
        if (kategorie == null) {
            return;
        }

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

    Spieler parseLinksForPlayer(String page, String name) throws LoginExpiredException {
        if (page.contains("musst du mit einem myTischtennis.de-Account eingeloggt sein")) {
            throw new LoginExpiredException();
        }
        Spieler spieler = new Spieler(name);
        Map<String, String> links = new HashMap<>();
        int idx = 0;
        while (true) {
            ParseResult pr = readBetween(page, idx, "<a", "</a>");
            if (isEmpty(pr)) {
                break;
            }
            String ahref[] = readHrefAndATag("<a" + pr.result + "</a>");
            if (ahref == null) {
                break;
            }
            idx = pr.end;
            if (ahref[1].contains("click-TT-Spielerportrait")) {
                ahref[1] = "click-TT-Spielerportrait";
            }
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
            mannschaft.url = MYTT + aref[0].replaceAll("/aktuell", "/gesamt");
            mannschaft.liga = aref[1];
            v.addMannschaft(mannschaft);
        }
    }

    void parseBilanzen(String page, Mannschaft mannschaft) {
        if (page.contains("Die Pokalspiele sind noch nicht fertig")) {
            return;
        }
        ParseResult table = readBetween(page, 0, "gamestatsTable", null);
        mannschaft.clearBilanzen();
        int idx = 0;
        while (table != null) {
            //mytt have a bug here: no opening tr element
            ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
            String[] row = tableRowAsArray("<td>" + resultrow.result, 10, false);
//            printRows(row);
            if (row[1].isEmpty()) {
                break;
            }

            List<String[]> posResults = new ArrayList<>();
            //10 entries, but what about 4er?
            for (int i = 3; i < 9; i++) {
                if (!row[i].isEmpty()) {
                    posResults.add(new String[]{"" + (i - 2), row[i]});
                }

            }
            String gesamt = row[9].replace("\r\n", "");
            String[] ahref = readHrefAndATag(row[1]);
            MyTTPlayerIds ids = parsePlayerIds(row[1]);
            SpielerAndBilanz bilanz = new SpielerAndBilanz(row[0],
                    ahref[1],
                    row[2], posResults, gesamt, ids);
            mannschaft.addBilanz(bilanz);
        }
    }

    MyTTPlayerIds parsePlayerIds(String line) {
        ParseResult personId = readBetween(line, 0, "personId: '", "'");
        if (isEmpty(personId)) {
            return null;
        }
        ParseResult clubNr = readBetween(line, 0, "clubNr: '", "'");
        if (isEmpty(clubNr)) {
            return null;
        }
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

    public void parseLigaAdressen(String page, Liga liga) {
        for (Mannschaft mannschaft : liga.getMannschaften()) {
            if (mannschaft.getVereinId() == null) {
                continue;
            }
            ParseResult result = readBetween(page, 0, mannschaft.getVereinId(), "<div class=\"panel-heading\">");
            //last entry
            if (isEmpty(result)) {
                result = readBetween(page, 0, mannschaft.getVereinId(), null);
            }

            mannschaft.clearLokale();
            parseMannschaftsLokale(result.result, mannschaft);

            ParseResult resultUrl = readBetween(page, result.start - 70, "<div class=\"panel-heading\">", "</div>");

            String[] href = readHrefAndATag(resultUrl.result);
            String url = href[0];
            if (url != null && url.length() > 0) {
                mannschaft.setVereinUrl(MYTT + url);
            }
        }
    }

    private void parseMannschaftsLokale(String part, Mannschaft mannschaft) {
        ParseResult resultAll = readBetween(part, 0, "<strong>Spiellokal", "Mannschaftskontakt");
        int idx = 0;
        int nr = 1;

        while (true) {
            ParseResult result = readBetween(resultAll, idx, "</strong>", "<strong>");

            if (isEmpty(result)) {
                break;
            }
//todo nr !!!!!
            String lokal = cleanupSpielLokalHtml(result.result);
            mannschaft.addSpielLokal(nr++, lokal);
//            System.out.println("lokal = " + lokal);
            idx = result.end;

        }

    }

    private String extendWithCrap(Mannschaft mannschaft) {
        return mannschaft.getName().
                replace("III", "Herren III").
                replace("II", "Herren II");
    }

    public void readBilanzen(Mannschaft team) throws LoginExpiredException, NetworkException {
        String urlBilanz = team.getUrl();
        String page = Client.getPage(urlBilanz);
        parseBilanzen(page, team);
    }
}
