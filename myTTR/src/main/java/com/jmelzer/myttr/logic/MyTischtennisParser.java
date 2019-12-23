/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import android.util.Log;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.MyTTLiga;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.SpielerAndBilanz;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;
import com.jmelzer.myttr.model.Head2HeadResult;
import com.jmelzer.myttr.model.MyTTPlayerIds;
import com.jmelzer.myttr.model.SearchPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("ALL")
public class MyTischtennisParser extends AbstractBaseParser {

    public static final String ZUR_TTR_HISTORIE = "zur TTR-Historie\">TTR ";

    public static int debugCounter = 1;

    ClubParser clubParser = new ClubParser();

    public int parsePoints(String page) throws PlayerNotWellRegistered {
        checkIfPlayerRegisteredWithClub(page);
        try {


            int start = page.indexOf(ZUR_TTR_HISTORIE) + ZUR_TTR_HISTORIE.length();
            if (start < 0) {
                return -1;
            }
            int end = page.indexOf("</a>", start + 1);
            if (end < 0) {
                return -2;
            }
            try {
//                if (true) throw new NumberFormatException();
                return Integer.valueOf(page.substring(start, end));
            } catch (NumberFormatException e) {
                return -3;
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "error parsing page", e);
        }

        return 0;
    }

    public User getPointsAndRealName() throws PlayerNotWellRegistered, NetworkException, LoginExpiredException, ValidationException, LoginException {
        String url = "https://www.mytischtennis.de/community/index";

        String page = Client.getPage(url);
        if (page.contains("Zu viele Fehlversuche")) {
            throw new LoginException("Zu viele Fehlversuche, das Konto wurde gesperrt! Nutze bitte die 'Passwort vergessen'-Funktion um dein Konto wieder freizuschalten.");
        }
        if (page.contains("<title>Login")) {
            throw new LoginException("Benutzername oder Passwort ist falsch");
        }
        if (page.contains("bitte überprüfe deine Angaben")) {
            throw new ValidationException("Du musst dich bitte 1x auf mytischtennis.de einloggen und speichern drücken");
        }
        if (page.contains("Du bist für uns leider nicht eindeutig zu identifizieren!")) {
            throw new PlayerNotWellRegistered();
        }

        return new User(parseLoginName(page), parsePoints(page));
    }

    public int getPoints() throws PlayerNotWellRegistered, NetworkException, LoginExpiredException {

        String url = "https://www.mytischtennis.de/community/index";

        String page = Client.getPage(url);

        return parsePoints(page);

    }

    void writeDebugFile(String page) {
        try {
            String filename = "myttr-debug.html";
            FileOutputStream outputStream;

            File file = new File(getLogDir(), filename);
            outputStream = new FileOutputStream(file);
            outputStream.write(page.getBytes());
            outputStream.close();
            file.setReadable(true);
            Log.i(Constants.LOG_TAG, "wrote log to " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "error writing page", e);
        }

    }

    File getLogDir() {
        File directory = null;
        /*
         * This sections checks the phone to see if there is a SD card. if
         * there is an SD card, a directory is created on the SD card to
         * store the test log results. If there is not a SD card, then the
         * directory is created on the phones internal hard drive
         */
        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            //create new file directory object
            directory = new File(Environment.getDataDirectory()
                    + "/myttrlog/");
            // if no directory exists, create new directory
            if (!directory.exists()) {
                directory.mkdir();
            }

            // if phone DOES have sd card
        } else if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            directory = new File(Environment.getExternalStorageDirectory()
                    + "/myttrlog/");
            // if no directory exists, create new directory to store test
            // results
            if (!directory.exists()) {
                directory.mkdir();
            }
        }// end of SD card checking
        return directory;
    }

    private void checkIfPlayerRegisteredWithClub(String page) throws PlayerNotWellRegistered {
        final String toCheck = "Du bist für uns leider nicht eindeutig";

        if (page.indexOf(toCheck) > 0) {
            throw new PlayerNotWellRegistered();
        }
    }

    public List<Player> findPlayer(String firstName, String lastName, String vereinsName) throws TooManyPlayersFound,
            NetworkException, LoginExpiredException, ValidationException {
        SearchPlayer sp = new SearchPlayer();
        sp.setFirstname(firstName);
        sp.setLastname(lastName);
        sp.setClub(new Club(vereinsName, null, null));
        return findPlayer(sp);
    }

    void validatePage(String page) throws NoDataException, LoginExpiredException {
        if (page.contains("Der Spieler ist noch keinem Team zugeordnet!")) {
            throw new NoDataException("Der Spieler ist noch keinem Team zugeordnet!");
        }
        if (page.contains("Hier einloggen")) {
            throw new LoginExpiredException();
        }
    }

    /**
     * @param firstName
     * @param lastName
     * @return Returns the player id number or 0 if not found
     */
    public List<Player> findPlayer(SearchPlayer sp) throws TooManyPlayersFound,
            NetworkException, LoginExpiredException, ValidationException {

        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("www.mytischtennis.de")
                .path("community/ajax/_rankingList");
        Club v = null;
        String vereinsName = sp.getClubName();
        String firstName = sp.getFirstname();
        String lastName = sp.getLastname();

        if (vereinsName != null && !"".equals(vereinsName)) {
            v = clubParser.getClubExact(vereinsName);
            if (v == null) {
                v = clubParser.getClubNameBestMatch(vereinsName);
            }
            if (v == null) {
                Log.i(Constants.LOG_TAG, "club not found in list:" + vereinsName);
            }
        }

        if (sp.getGender() != null) {
            builder.appendQueryParameter("geschlecht", sp.getGender());
        }
        if (sp.getYearFrom() > 0) {
            builder.appendQueryParameter("geburtsJahrVon", "" + sp.getYearFrom());
        }
        if (sp.getYearTo() > 0) {
            builder.appendQueryParameter("geburtsJahrBis", "" + sp.getYearTo());
        }
        if (sp.getTtrFrom() > 0) {
            builder.appendQueryParameter("ttrVon", "" + sp.getTtrFrom());
        }
        if (!sp.isActual()) {
            builder.appendQueryParameter("ttrQuartalorAktuell", "quartal");
        }
        if (sp.getTtrTo() > 0) {
            builder.appendQueryParameter("ttrBis", "" + sp.getTtrTo());
        }
        if (firstName != null && firstName.length() > 2 && lastName != null && lastName.length() > 2) {
            firstName = (Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1)).trim();
            lastName = (Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1)).trim();
            builder.appendQueryParameter("vorname", firstName)
                    .appendQueryParameter("nachname", lastName);

            if (v != null) {
                builder.appendQueryParameter("vereinPersonenSuche", v.getName());
                builder.appendQueryParameter("vereinIdPersonenSuche", v.getId() + "," + v.getVerband());
            } else {
                builder.appendQueryParameter("vereinIdPersonenSuche", "");
                builder.appendQueryParameter("vereinPersonenSuche", "");
            }

        } else {
            //only club name given, so we have to use other parameters as with username
            //sometimes I hate mytischtennis
            if (v != null) {
                builder.appendQueryParameter("verein", v.getName());
                String cId = v.getId();
                builder.appendQueryParameter("vereinId", cId + "," + v.getVerband());
            }

        }
        builder.appendQueryParameter("alleSpielberechtigen", "yes");

        String url = builder.build().toString();
        //bad trick for the crap from mytischtennis.de
        url = url.replace("%20", "+");


        String page = Client.getPage(url);
        validateStillLoginActive(page);
        if (page.contains("Keine Daten vorhanden!")) {
            throw new ValidationException("Keine Daten vorhanden!");
        }
        List<Player> list = new ArrayList<>();
        return parseForPlayer(firstName, lastName, page, list, 0);

    }

    List<Player> parseForPlayer(String firstName, String lastName, String page, List<Player> list, int start) throws TooManyPlayersFound {
        if (list.size() >= 100) {
            return list;
        }
        if (page.indexOf("Verfeinere deine Suche um ein optimales Ergebnis zu erzielen") > 0) {
            throw new TooManyPlayersFound();
        }
        if (page.indexOf("Keine Person(en) gefunden") > 0) {
            return list;
        }
        int idx = start;
        //let start at table tag
        boolean haveRang = page.indexOf("<th>Rang") > 0;

        if (start == 0) {
            idx = page.indexOf("<table class=\"coolTable\"");
            //skipp the header
            idx = page.indexOf("<tr", idx);
            idx = page.indexOf("<tr", idx + 5);
        }
        if (idx > 0) {
            ParseResult tr = readBetween(page, idx, "<tr", "</tr>");
            if (tr != null) {
                String[] rows = tableRowAsArray(tr.result, 5, false);
                //somteimes there is a rank of the club inside so one more column
                //0 = d-rank
                //1 = name
                //2 = club
                //3=ttr
                Player player = new Player();

                int i = 0;
                if (haveRang) {
                    i++;
                }
                // d-rang - not interested
                i++;

                player.setFirstname(findFirstName(0, rows[i]));
                player.setPersonId(findPlayerId(0, rows[i]));
                player.setLastname(findLastName(0, rows[i]));
                i++;
                player.setClub(readClubFromPage(0, rows[i]));
                list.add(player);
                i++;
                try {
                    player.setTtrPoints(Integer.valueOf(rows[i]));
                } catch (NumberFormatException e) {
                    //ignore
                }
                idx = page.indexOf("</tr>", idx);
                return parseForPlayer(firstName, lastName, page, list, tr.end);
            }
        }
        return list;
    }

    private String findFirstName(int startIdx, String page) {
        ParseResult result = readBetween(page, startIdx, "<span class=\"", "<strong>");
        result = readBetween(result.result, 0, "\">", null);
        if (!result.isEmpty()) {
            return result.result.trim();
        }
        return null;
    }

    private Long findPlayerId(int startIdx, String page) {
        ParseResult result = readBetween(page, startIdx, "data-tooltipdata=\"", ";");
        if (result != null) {
            try {
                return (Long.valueOf(result.result));
            } catch (NumberFormatException e) {
                //ignore
            }
        }
        return 0L;
    }

    private String findLastName(int startIdx, String page) {
        String toFind = "<strong>";
        String toFindClose = "</strong>";
        int idx = page.indexOf(toFind, startIdx) + toFind.length();
        int idx2 = page.indexOf(toFindClose, idx);

        return page.substring(idx, idx2).trim();
//        return null;
    }

    private String readClubFromPage(int startIdx, String page) {
        ParseResult result = readBetweenOpenTag(page, startIdx, "<a ", "</a>");
        if (result != null && !result.isEmpty() && result.result.length() > 0) {
            return result.result;
        }
        return "";
    }

    int findPoints(final int startIdx, String pageU) {
        ParseResult result = readBetween(pageU, startIdx, "<td>", "</td>");
        try {
            return Integer.valueOf(result.result.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * get clublist from mytt
     *
     * @param actual false if qttr
     */
    public List<Player> getClubList(boolean actual) throws NetworkException, LoginExpiredException, NiceGuysException {
        String url = "https://www.mytischtennis.de/community/showclubinfo";
        String page = Client.getPage(url);

        validateStillLoginActive(page);

        int n = page.indexOf("vereinid=");
        if (n > 0) {
            int n2 = page.indexOf("&", n);
            String id = page.substring(n + 9, n2);

            String clubListUrl = "https://www.mytischtennis.de/community/ajax/_rankingList?vereinid=" + id +
                    "&alleSpielberechtigen=yes";
            if (actual == false) {
                clubListUrl += "&ttrQuartalorAktuell=quartal";
            }
            page = Client.getPage(clubListUrl);
            List<Player> list = new ArrayList<>();
            try {
                return parseForPlayer(null, null, page, list, 0);
            } catch (TooManyPlayersFound tooManyPlayersFound) {
                //ignore
            }

        }
        return null;
    }

    private void validateStillLoginActive(String page) throws LoginExpiredException {
        if (page.contains("<title>Login") || page.contains("XML does not fit processing settings")) {
            throw new LoginExpiredException();
        }
    }

    public String getNameOfOwnClub() {
        if (MyApplication.manualClub != null && !"".equals(MyApplication.manualClub)) {
            return MyApplication.manualClub;
        }
        String url = "https://www.mytischtennis.de/community/userMasterPage";
        try {
            String page = Client.getPage(url);
            return readClubName(page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    String readClubName(String page) {

        String marker = "<dt>Verein:</dt>";

        int n = page.indexOf(marker);
        if (n > 0) {
            return readBetween(page, n, "<dd>", "</dd>").result;
        }
        return null;
    }


    private int parsePoints(String page, int nStartIdx) {
        final String tagStart = "<td style=\"text-align:center;\">";
        final String tagEnd = "</td>";
        int n1 = page.indexOf(tagStart, nStartIdx) + tagStart.length();
        int n2 = page.indexOf(tagEnd, n1);
        return Integer.valueOf(page.substring(n1, n2).trim());
    }

    public String getRealName() {
        String url = "https://www.mytischtennis.de/community/index";
        try {
            String page = Client.getPage(url);
            return parseLoginName(page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    private String parseLoginName(String page) {
        ParseResult result = readBetween(page, 0, "<a href=\"/community/personalprofil\" class=\"user-image\">", "</a>");
        result = readBetween(result.result, 0, "<span>", "</span>");
        return result.result.trim();
    }

    public List<Player> readClubPlayers() throws LoginExpiredException, NetworkException, ValidationException, TooManyPlayersFound, NiceGuysException {

        String page = Client.getPage("https://www.mytischtennis.de/community/ranking?showmyclub=1");
        validateStillLoginActive(page);
        if (page.contains("Keine Daten vorhanden!")) {
            throw new ValidationException("Keine Daten vorhanden!");
        }
        validateBadPeople(page);
//        ParseResult result = readBetween(page, 0, "<input type=\"hidden\" name=\"vereinId\"" , ">");
        ParseResult result = readBetween(page, 0, "url: 'ajax/_rankingList", "'");
        if (!isEmpty(result)) {
            String url = "https://www.mytischtennis.de/community/ajax/_rankingList" + result.result;
            page = Client.getPage(url);
        }
        //id=\"vereinId\" value=\"156012,WTTV\">", )
        return parseForPlayer("", "", page, new ArrayList<Player>(), 0);
    }

    /**
     * this is complicated cause the url https://www.mytischtennis.de/community/group doesn't
     * have all informations, sow need https://www.mytischtennis.de/clicktt/home#tab_plan too
     */
    public Mannschaft readOwnTeam() throws NetworkException, LoginExpiredException, NoDataException, ValidationException {
        String url = "https://www.mytischtennis.de/community/team";
        String page = Client.getPage(url);
        Mannschaft mannschaft = parseOwnTeam(page);
        Liga liga = new Liga("", "https://www.mytischtennis.de/clicktt/home");

        page = Client.getPage("https://www.mytischtennis.de/clicktt/home-tab?id=plan");
        Liga.Spielplan spielplan = Liga.Spielplan.RR;
        Liga.Spielplan spielplan2 = Liga.Spielplan.VR;
        if (page.contains("Gruppenspielplan (Vorrunde)")) {
            spielplan = Liga.Spielplan.VR;
            spielplan2 = Liga.Spielplan.RR;
        }
        MyTTClickTTParserImpl parser = new MyTTClickTTParserImpl();
        parser.parseErgebnisse(page, liga, spielplan);
        parser.parseSpielplanLinks(liga, page);


        if (spielplan2 == Liga.Spielplan.RR) {
            page = Client.getPage(liga.getUrlRR());
            parser.parseErgebnisse(page, liga, Liga.Spielplan.RR);
        } else {
            page = Client.getPage(liga.getUrlVR());
            parser.parseErgebnisse(page, liga, Liga.Spielplan.VR);
        }

        if (mannschaft.getName().contains("Augustin")) {
            System.out.println();
        }
        mannschaft = liga.replaceMannschaftInList(mannschaft);

        mannschaft.setSpiele(liga.getSpieleFor(mannschaft.getName(), spielplan));
        mannschaft.getSpiele().addAll(liga.getSpieleFor(mannschaft.getName(), spielplan2));

        mannschaft.setLiga(liga);
        return mannschaft;
    }

    @NonNull
    Mannschaft parseOwnTeam(String page) throws NoDataException, LoginExpiredException {
        Mannschaft mannschaft = new Mannschaft();

        try {
            List<SpielerAndBilanz> players = parsePlayerFromTeamV2(page);
            mannschaft.setSpielerBilanzen(players);
        } catch (NoDataException e) {
            //ok
        }

//        List<Mannschaftspiel> spiele = parseSpiele(page);
//        mannschaft.setSpiele(spiele);

        mannschaft.setName(readBetween(page, 0, "<h2>", " - ").result);
        return mannschaft;
    }

    List<Mannschaftspiel> parseSpiele(String page) throws NoDataException, LoginExpiredException {
        validatePage(page);
        List<Mannschaftspiel> mannschaftspiels = new ArrayList<>();
        ParseResult table = readBetween(page, 0, "<h3 class=\"table-headline\">Spielplan</h3>", "</table>");
        int idx = 0;
        while (true) {
            //mytt have a bug here: no opening tr element
            ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
            String[] row = tableRowAsArray("<td>" + resultrow.result, 11, false);
            String[] ahref = readHrefAndATag(row[1]);
            printRows(row);
            String datetime = "";
            ParseResult dr = readBetween(row[0], 0, "<span class=\"hidden-xs\">", "</span>");
            datetime += dr.result;
            dr = readBetween(row[0], dr.end, "</span>", "<span ");
            datetime += " " + dr.result;

            String heim = readHrefAndATag(row[1])[1];
            String gast = readHrefAndATag(row[2])[1];
            Mannschaftspiel spiel = new Mannschaftspiel();
            spiel.setDate(datetime);
            spiel.setHeimMannschaft(new Mannschaft(heim));
            spiel.setGastMannschaft(new Mannschaft(gast));
            String erg = row[4];
            if (erg != null) {
                erg = readHrefAndATag(erg)[1];
                spiel.setPlayed(!erg.equals("0 : 0"));
            }
            spiel.setErgebnis(erg);
            mannschaftspiels.add(spiel);
        }
        return mannschaftspiels;
    }

    public List<Player> readPlayersFromTeam(String id) throws NetworkException,
            LoginExpiredException, NoDataException, ValidationException, NiceGuysException {

        String url = "https://www.mytischtennis.de/community/teamplayers";
        if (id != null) {
            url += "?teamId=" + id;
        }
        String page = Client.getPage(url);
        try {
            return parsePlayerFromTeam(page);
        } catch (NoDataException e) {
            try {
                return readClubPlayers();
            } catch (TooManyPlayersFound tooManyPlayersFound) {
                //ok
                throw e;
            }
        }

    }

    public void validateBadPeople() throws LoginExpiredException, NetworkException, NiceGuysException {
        String page = Client.getPage("https://www.mytischtennis.de/community/ranking?showmyclub=1");
        validateBadPeople(page);
    }

    public Player readEvents() throws NetworkException, LoginExpiredException, NiceGuysException {
        String url = "https://www.mytischtennis.de/community/events";
        String page = Client.getPage(url);
        validateStillLoginActive(page);
//        validateBadPeople(page);
        return parseEvents(page, true);
    }

    private void validateBadPeople(String page) throws NiceGuysException {
        long start = System.currentTimeMillis();
        validateName(page, "Kaufmann, Patrick");
        validateName(page, "Metzger, David");
        validateName(page, "Kunkel, Werner");
        validateName(page, "Niederweis, Markus");
        validateName(page, "Seidel, Daniel");
        validateName(page, "Hersel");
        validateName(page, "Lohmar");
        validateName(page, "Leuscheid");
        Log.i(Constants.LOG_TAG, "validate time " +
                (System.currentTimeMillis() - start) + " ms");
    }

    private void validateName(String page, String name) throws NiceGuysException {
        //whitelist
        if (MyApplication.getLoginUser().getRealName().contains("Ana Beja-Pütz")) {
            return;
        }

        if (page.contains(name)) {
            throw new NiceGuysException(name);
        }
    }

    Player parseEvents(String page, final boolean own) {
        List<Event> events = new ArrayList<Event>();
        boolean rown = own;
        if (!rown && page.contains("<h3>Dein")) {
            rown = true;
        }
        Player player = parsePlayerFromEventPage(page, rown);

        String startTag = "<tbody>";
        boolean endoflist = false;

        int n = page.indexOf(startTag);
        if (n > 0) {
            while (!endoflist) {
                //find next td
                ParseResult result = readBetweenOpenTag(page, n, "<tr", "</tr>");
                if (result == null) {
                    break;
                }
                n = result.end;
                String[] cells = tableRowAsArray(result.result, 9, false);
                if (cells == null || cells[1].isEmpty()) {
                    continue;
                }
                Event event = new Event();
                events.add(event);
                event.setDate(cells[1]);
                event.setEventId(Long.valueOf(readBetween(result.result, 0, "openmoreinfos(", ",").result));
                event.setEvent(readBetween(result.result, 0, "Details anzeigen\">", "</a>").result);
//
                event.setAk(cells[3]);
                try {
                    event.setTtr(Integer.valueOf(cells[7]));
                } catch (NumberFormatException e) {
                    //ok
                }
                event.setSum(Short.valueOf(readBetweenOpenTag(cells[8], 0, "<span", "</span").result));
//
                event.setBilanz(cells[4]);
//
//                //next 3 td not interesting in
//                for (int i = 0; i < 2; i++) {
//                    result = readBetween(page, n, "<td>", "</td>");
//                    n = result.end;
//                }
//                result = readBetween(page, n, "<td>", "</td>");
//                event.setTtr(Integer.valueOf(result.result));
//                n = result.end;
//                result = readBetweenOpenTag(page, n, "<td ", "</td>");
////                Log.d(Constants.LOG_TAG, "res = " + result.result);
////                n = result.end - 15;
//                ParseResult result2 = readBetweenOpenTag(result.result, 0, "<span", "</span>");
//                event.setSum(Short.valueOf(result2.result));
//                n = result.end;
                endoflist = page.indexOf("</tbody>", n) == -1;
            }

        }
        player.addEvents(events);
        return player;
    }

    private Player parsePlayerFromEventPage(String page, boolean own) {
        Player p = new Player();
        ParseResult result = null;
        try {
            result = readBetween(page, 0, "<strong>TTR: </strong>", "</p>");
            p.setTtrPoints(Integer.valueOf(result.result.trim()));
        } catch (NumberFormatException e) {
            //ok
            try {
                result = readBetween(page, 0, "<h3>", "</h3>");
                if (result != null) {
                    result = readBetween(result, 0, "</span> ", "<span");
                    if (result != null) {
                        p.setTtrPoints(Integer.valueOf(result.result.trim()));
                    }
                }
            } catch (NumberFormatException e1) {
                //ok
            }
        }

        if (!own) {
            result = readBetween(page, 0, "<h3>", "</h3>");
            p.setFirstname(parseFirstnameFromBadName(result.result));
            p.setLastname(parseLastNameFromBadName(result.result));

        } else {
            p.setFullName(parseLoginName(page));

        }
        return p;
    }

    //samples: Michel, Dennis'
//    Lüdinghausen, Jakob vons
    String parseFirstnameFromBadName(String line) {
        String s = line;
        if (line.contains("<")) {
            s = line.substring(0, line.indexOf("<"));
        }

        s = s.substring(s.indexOf(",") + 2);
        s = s.replace("\r\n", "");
        //strip possible s oder '
        return s.substring(0, s.length() - 1);
    }

    String parseLastNameFromBadName(String line) {
        return line.substring(0, line.indexOf(","));
    }

    String stripTags(String s) {
        int start = s.indexOf(">");
        String ret = s;
        while (start >= 0) {
            ret = ret.substring(start + 1);
            start = ret.indexOf(">");
        }
        return ret;
    }


    List<SpielerAndBilanz> parsePlayerFromTeamV2(String page) throws NoDataException, LoginExpiredException {
        validatePage(page);
        List<SpielerAndBilanz> players = new ArrayList<>();
        ParseResult table = readBetween(page, 0, "<h3 class=\"table-headline\">Einzelbilanzen</h3>", "</table>");
        int idx = 0;
        if (isEmpty(table)) {
            throw new NoDataException("myTTR konnte die Einzelbilanzen nicht finden");
        }
        while (true) {
            //mytt have a bug here: no opening tr element
            ParseResult resultrow = readBetween(table.result, idx, "<td>", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
            String[] row = tableRowAsArray("<td>" + resultrow.result, 11, false);
            String[] ahref = readHrefAndATag(row[1]);
//            printRows(row);
            List<String[]> posResults = new ArrayList<>();
            for (int i = 3; i < 8; i++) {
                if (!row[i].isEmpty()) {
                    posResults.add(new String[]{"" + (i - 2), row[i]});
                }

            }
            String gesamt = row[10];
            MyTTPlayerIds ids = parsePlayerIds(row[1]);

            SpielerAndBilanz bilanz = new SpielerAndBilanz(row[0],
                    ahref[1],
                    row[2], posResults, gesamt, ids);
            players.add(bilanz);
        }
        return players;
    }

    MyTTPlayerIds parsePlayerIds(String line) {
        ParseResult personId = readBetween(line, 0, "tooltipdata=\"", ";");
        return new MyTTPlayerIds(personId.result, null);
    }

    List<Player> parsePlayerFromTeam(String page) throws NoDataException, LoginExpiredException {
        validatePage(page);
        Set<Player> set = new TreeSet<Player>(new Comparator<Player>() {
            @Override
            public int compare(Player lhs, Player rhs) {
                return lhs.getRank() - rhs.getRank();
            }
        });
        if (page == null) {
            return new ArrayList<Player>();
        }
        final String teamNameS = "<h2>";
        final String teamNameE = "</h2>";
        int startTeam = page.indexOf(teamNameS);
        int endTeam = page.indexOf(teamNameE);
        String teamName = "";
        if (startTeam > 0 && endTeam > 0) {
            teamName = page.substring(startTeam + teamNameS.length(), endTeam);
        }
        String clubName = getClubNameFromTeamName(teamName);

        final String startTag = "Rückrunde";
        int start = page.indexOf(startTag);
        final String startTag2 = "Vorrunde";
        if (start == -1) {
            start = page.indexOf(startTag2);
        }
        final String toCheck = "<a class=\"person";

        String trClosed = "</td>";

        start = page.indexOf(toCheck, start);
        if (start > 0) {
            int n = start;
            int rank = 1;
            while (n > 0) {
                n += toCheck.length();
                Player p = readPlayer(page, n);
                p.setRank(rank);
                p.setTeamName(teamName);
                p.setClub(clubName);

                rank++;
                set.add(p);
                n = page.indexOf(trClosed, n);
                n = page.indexOf(toCheck, n);
            }
        }
        return new ArrayList<Player>(set);

    }

    String getClubNameFromTeamName(String teamName) {
        String c = removeString(teamName, "II");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "III");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "IV");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "V");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "VI");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "VII");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "VIII");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "IX");
        if (c != null) {
            return c;
        }
        c = removeString(teamName, "X");
        if (c != null) {
            return c;
        }
        return teamName;
    }

    private String removeString(String teamName, String n) {
        if (teamName.endsWith(" " + n)) {
            return teamName.substring(0, teamName.indexOf(" " + n));
        }
        return null;
    }

    private Player readPlayer(String page, int startPoint) {
        String tr = "\">";
        Player player = new Player();
        String div = "</a>";
        ParseResult result = readBetween(page, startPoint - 50, "data-tooltipdata=\"", ";");
        if (result != null) {
            player.setPersonId(Long.valueOf(result.result));
        }
        int n = page.indexOf(tr, startPoint);
        int end = page.indexOf(div, n);
        String name = page.substring(n + tr.length(), end);
        int k = name.indexOf(',');
        player.setLastname(stripNewLine(name.substring(0, k)));
        player.setFirstname(name.substring(k + 2));

//        n = page.indexOf("personId=", n);
//        if (n > 0) {
//            end = page.indexOf("\"", n);
//            String idS = page.substring(n, end);
//            player.setPersonId(Long.valueOf(idS));
//        }

        return player;
    }

    private String stripNewLine(String str) {
        if (str.startsWith("\n")) {
            str = str.substring(str.indexOf("\n") + 1);
        }
        if (str.endsWith("\n")) {
            str = str.substring(0, str.indexOf("\n"));
        }
        return str;
    }

    public EventDetail readEventDetail(Event event) throws NetworkException, LoginExpiredException {
        String url = "https://www.mytischtennis.de/community/eventDetails?eventId=" + event.getEventId();
        String page = Client.getPage(url);
        validateStillLoginActive(page);
        return parseDetail(page);
    }

    EventDetail parseDetail(String page) {
        //                data-tooltipdata="817950;0;Spies von Büllesheim, Alexander;true"
        String startTag = "<table class=\"table table-mini table-mytt table-striped\">";
        int n = page.indexOf(startTag);
        boolean endoflist = false;
        EventDetail eventDetail = new EventDetail();
        int j = 0;
        if (n > 0) {
            while (!endoflist) {
                ParseResult tr = readBetween(page, n, "<tr", "</tr>");
                if (tr == null) {
                    break;
                }
                n = tr.end - 1;

                String cols[] = tableRowAsArray(tr.result, 10, false);
                //description of cols
                //0 = player name etc
                //1 = result e.g. 3:0
                // 2 ...9 sets , can be empty td

                ParseResult tooltip = readBetween(cols[0], 0, "data-tooltipdata=\"", ";");
                if (tooltip == null) {
                    continue;
                }
                Game game = new Game();
                eventDetail.getGames().add(game);

                game.setPlayerId(Long.valueOf(tooltip.result));

                tooltip = readBetween(cols[0], tooltip.end, null, ";");
                //not interersting number, skip
                tooltip = readBetween(cols[0], tooltip.end, null, ";");
                game.setPlayer(tooltip.result);

                game.setPlayerWithPoints(readBetweenOpenTag(cols[0], 0, "<a", "</a>").result);
//
                int i = 1;
                game.setResult(readBetweenOpenTag(cols[1], 0, "<strong", "</strong>").result);
                for (i = 2; i < 9; i++) {
                    if (!cols[i].isEmpty()) {
                        game.addSet(cols[i]);
                    }
                }
                j++;
            }

            endoflist = page.indexOf("</tbody>", n) == -1;
        }


        return eventDetail;
    }

    //getting points and events
    public Player readEventsForForeignPlayer(long playerId) throws NetworkException, LoginExpiredException {

        String url = "https://www.mytischtennis.de/community/events?personId=" + playerId;
        String page = Client.getPage(url);

        validateStillLoginActive(page);

        Player p = parseEvents(page, false);
        p.setPersonId(playerId);
        return p;
    }

    public Player completePlayerWithTTR(Player player) throws LoginExpiredException, NetworkException, ValidationException {
        if (player.getPersonId() == 0) {
            throw new ValidationException("Der Spieler " + player.getFullName() + " hat keine gültige ID");
        }
        String url = "https://www.mytischtennis.de/community/events?personId=" + player.getPersonId();
        String page = Client.getPage(url);
        validateStillLoginActive(page);

        ParseResult result = readBetween(page, 0, "<h3>", "<br class");
        if (!isEmpty(result)) {
            result = readBetween(result.result, 0, "</span>", null);
            player.setTtrPoints(Integer.valueOf(result.result.trim()));
        }
        return player;
    }

    public List<Head2HeadResult> readHead2Head(long id) throws NetworkException, LoginExpiredException {
        String url = "https://www.mytischtennis.de/community/headTohead?gegnerId=" + id;
        String page = Client.getPage(url);
        validateStillLoginActive(page);

        return parseHead2Head(page);
    }

    List<Head2HeadResult> parseHead2Head(String page) {
        List<Head2HeadResult> result = new ArrayList<>();

        ParseResult table = readBetween(page, 0, "<tbody>", "</tbody>");
        if (table == null) {
            return null;
        }
        String playerName = readBetween(page, 0, "Begegnungen zwischen dir und ", "</h2").result;
        int c = 0;
        int idx = 0;
        while (true) {
            ParseResult resultrow = readBetween(table, idx, "<tr", "</tr>");
            if (isEmpty(resultrow)) {
                break;
            }
            idx = resultrow.end;
//            System.out.println("-----------------");
//            System.out.println(resultrow.result);
//            System.out.println("-----------------");

            if (!resultrow.result.contains("class=\"odd\"") && !resultrow.result.contains("class=\"even\"")) {
                continue;
            }

            String[] row = tableRowAsArray(resultrow.result, 8, false);
            printRows(row);
            String date = row[0];
            String type;
            if (row[1].contains("span")) {
                type = readBetween(row[1], 0, ">", "</span>").result;
            } else {
                type = row[1];
            }
            Game game = new Game();
            String erg = readBetweenOpenTag(row[2], 0, "<strong", "</strong>").result;
            game.addSet(row[3]);
            game.addSet(row[4]);
            game.addSet(row[5]);
            game.addSet(row[6]);
            game.addSet(row[7]);
            game.setResult(erg);
            game.setPlayer(playerName);
            result.add(new Head2HeadResult(playerName, type, date, game));
        }
        return result;
    }


    class Helper {
        Player p;

        int idx;

        public Helper(Player player, int idx) {
            p = player;
            this.idx = idx;
        }

    }

    List<String> parseGroupForRanking(String page) {
        final String search = "ranking?showgroupid=";
        List<String> list = new ArrayList<>();
        int idx = page.indexOf(search);
        while (idx > 0) {
            list.add(page.substring(idx + search.length(), page.indexOf("\"", idx)));
            idx = page.indexOf(search, idx + 10);
        }
        return list;
    }

    public MyTTLiga parseGroupRanking(String page) {
        MyTTLiga myTTLiga = new MyTTLiga();
        ParseResult name = readBetweenOpenTag(page, 0, "<h3", "</h3>");
        if (name.result != null) {
            if (name.result.contains(":")) {
                myTTLiga.setLigaName(name.result.substring(name.result.indexOf(':') + 2));
            }
        }
        ParseResult table = readBetweenOpenTag(page, 0, "<table", "</table>");
        if (table == null) {
            return myTTLiga;
        }
        int idx = 0;
        int c = 0;
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

            Player player = parseLigaPlayerRow(resultrow.result);
            myTTLiga.addPlayer(player);
        }
        return myTTLiga;
    }

    private Player parseLigaPlayerRow(String line) {
        String cols[] = tableRowAsArray(line, 5, false);
        Long id = findPlayerId(0, line);
        String name = readBetweenOpenTag(cols[2], 0, "<span class", "</strong>").result;
        String firstname = name.substring(0, name.indexOf(" <"));
        String lastname = name.substring(name.indexOf(">") + 1);
        String club = "";
        try {
            club = readBetweenOpenTag(cols[3], 0, "<a href", "</a>").result;
        } catch (NullPointerException e) {
            //ignore
        }
        int ttr = 0;
        try {
            ttr = Integer.valueOf(cols[4]);
        } catch (NumberFormatException e) {
        }

        Player p = new Player(firstname, lastname, club, ttr);
        p.setPersonId(id);
        return p;
    }

    public List<MyTTLiga> readOwnLigaRanking() throws NetworkException, LoginExpiredException {
        String url = "https://www.mytischtennis.de/community/group";
        String page = Client.getPage(url);
        validateStillLoginActive(page);

        List<String> groupIds = parseGroupForRanking(page);
        List<MyTTLiga> ligen = new ArrayList<>();
        for (String groupId : groupIds) {

            url = "https://www.mytischtennis.de/community/ajax/_rankingList?kontinent=Europa&land=DE&deutschePlusGleichgest=no&alleSpielberechtigen=&verband=&bezirk=&kreis=&regionPattern123=&regionPattern4=&regionPattern5=&geschlecht=&geburtsJahrVon=&geburtsJahrBis=&ttrVon=&ttrBis=&ttrQuartalorAktuell=aktuell&anzahlErgebnisse=100&vorname=&nachname=&verein=&vereinId=&vereinPersonenSuche=&vereinIdPersonenSuche=&ligen=&groupId=%s&showGroupId=%s&deutschePlusGleichgest2=no&ttrQuartalorAktuell2=aktuell";
            url = url.replace("%s", groupId);
            page = Client.getPage(url);
            ligen.add(parseGroupRanking(page));
        }
        return ligen;
    }

    private void printRows(String[] row) {
        for (int i = 0; i < row.length; i++) {
            System.out.println(i + " = " + row[i]);
        }
    }
}
