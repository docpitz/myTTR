/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("ALL")
public class MyTischtennisParser {

    public static final String ZUR_TTR_HISTORIE = "zur TTR-Historie\">TTR ";

    public static int debugCounter = 1;

    ClubParser clubParser = new ClubParser();

    public int getPoints() throws PlayerNotWellRegistered {
//        if (debugCounter++ % 10 == 0) {
//            Log.i(Constants.LOG_TAG, "getPoints() = " + (1600 + debugCounter));
//            return 1600 + debugCounter;
//        } else {
//            Log.i(Constants.LOG_TAG, "getPoints() = " + 1565);
//            return 1565;
//        }
//
        String url = "http://www.mytischtennis.de/community/index";

        try {
            String page = Client.getPage(url);

            checkIfPlayerRegisteredWithClub(page);

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
//                String filename = "myttr-debug.html";
//                FileOutputStream outputStream;
//
//                File file = new File(getLogDir(), filename);
//                outputStream = new FileOutputStream(file);
//                outputStream.write(page.getBytes());
//                outputStream.close();
//                file.setReadable(true);
//                Log.i(Constants.LOG_TAG, "wrote log to " + file.getAbsolutePath());
                return -3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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

    /**
     * @param firstName
     * @param lastName
     * @return Returns the player id number or 0 if not found
     */
    public List<Player> findPlayer(String firstName, String lastName, String vereinsName) throws TooManyPlayersFound,
            NetworkException {

        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("www.mytischtennis.de")
                .path("community/ranking");
        Club v = null;
        if (vereinsName != null && !"".equals(vereinsName)) {
            v = clubParser.getClubExact(vereinsName);
            if (v == null) {
                v = clubParser.getClubNameBestMatch(vereinsName);
            }
            if (v == null) {
                Log.i(Constants.LOG_TAG, "club not found in list:" + vereinsName);
            }
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
                builder.appendQueryParameter("vereinId", v.getId() + "," + v.getVerband());
            }

        }

        String url = builder.build().toString();
        //bad trick for the crap from mytischtennis.de
        url = url.replace("%20", "+");


        String page = Client.getPage(url);
        List<Player> list = new ArrayList<>();
        return parseForPlayer(firstName, lastName, page, list, 0);

    }

    private List<Player> parseForPlayer(String firstName, String lastName, String page, List<Player> list, int start) throws TooManyPlayersFound {
        if (list.size() >= 100) {
            return list;
        }
        int idx = start;
        //let start at table tag
        if (start == 0) {
            idx = page.indexOf("<table class=\"coolTable\"");
            //skipp the header
            idx = page.indexOf("<tr>", idx);
            idx = page.indexOf("<tr>", idx + 5);
        }
        //Indicator if we we have next rows
        int idx2 = page.indexOf("<tr class=\"even", idx);
        if (idx2 < 0) {
            idx2 = page.indexOf("<tr>", idx);
        }
        if (idx2 > 0) {
            //go back to the last row "<tr
            idx = page.indexOf("<tr", idx);
            Player player = new Player();
            player.setTtrPoints(findPoints(idx, page));
            player.setFirstname(findFirstName(idx, page));
            player.setLastname(findLastName(idx, page));
            player.setClub(readClubFromPage(idx, page));
            player.setPersonId(findPlayerId(idx, page));
            list.add(player);
            idx = page.indexOf("</tr>", idx);
            return parseForPlayer(firstName, lastName, page, list, idx);
        }
        return list;
    }

    private String findFirstName(int startIdx, String page) {
        ParseResult result = readBetween(page, startIdx, "<span class=\"", "<strong>");
        result = readBetween(result.result, 0, ">", " ");
        return result.result;
//        String toFind = ">";
//        String toFindEnd = "<strong>";
//        int idx = page.indexOf(toFind, startIdx) + toFind.length();
//        int idx2 = page.indexOf(toFindEnd, idx);
//
//        return page.substring(idx, idx2).trim();
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
        ParseResult result = readBetween(page, startIdx, "<a href=\"showclubinfo", "</a>");
        if (result.result.length() > 0) {
            return result.result.substring(result.result.indexOf("\">") + 2);
        }
        return "";
    }

    int findPoints(final int startIdx, String pageU) {
        ParseResult result = readBetween(pageU, startIdx, "<td style=\"text-align:center;\">", "</td>");
        try {
            return Integer.valueOf(result.result.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public List<Player> getClubList() throws NetworkException, LoginExpiredException {
        String url = "http://www.mytischtennis.de/community/showclubinfo";
        String page = Client.getPage(url);
        if (redirectedToLogin(page)) {
            throw new LoginExpiredException();
        }
        int n = page.indexOf("vereinid=");
        if (n > 0) {
            int n2 = page.indexOf("&", n);
            String id = page.substring(n + 9, n2);

            String clubListUrl = "http://www.mytischtennis.de/community/ranking?vereinid=" + id +
                    "&alleSpielberechtigen=yes";
            page = Client.getPage(clubListUrl);
            List<Player> list = new ArrayList<>();
            try {
                return parseForPlayer(null, null, page, list, 0);
            } catch (TooManyPlayersFound tooManyPlayersFound) {
                //ignore
            }

//            int idx = 0;
//            List<Player> list = new ArrayList<Player>();
//            Helper h = getNextPlayer(page, id, idx);
//            while (h != null) {
//                list.add(h.p);
//                h = getNextPlayer(page, id, h.idx);
//            }
//            return list;

        }
        return null;
    }

    private boolean redirectedToLogin(String page) {
        return page.contains("<title>Login");
    }

    public String getNameOfOwnClub() {
        String url = "http://www.mytischtennis.de/community/userMasterPage";
        try {
            String page = Client.getPage(url);
            return readClubName(page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    private String readClubName(String page) {
        if (MyApplication.manualClub != null && !"".equals(MyApplication.manualClub)) {
            return MyApplication.manualClub;
        }
        String marker = "<strong>Verein:</strong>";
        String div = "<div class=\"col_3 mb_5\">";

        int n = page.indexOf(marker);
        if (n > 0) {
            n = page.indexOf(div, n + marker.length());
            int end = page.indexOf("</div>", n);
            return page.substring(n + div.length(), end);
        }
        return null;
    }

    private Helper getNextPlayer(String page, String id, int idx) {
        final String cssBefore = "openinfos myttFeaturesTooltip";
        int nCssClassBefore = page.indexOf(cssBefore, idx);
        if (nCssClassBefore <= 0) {
            return null;
        }
        ParseResult result = readBetween(page, nCssClassBefore, "data-tooltipdata=\"", ";");

        int nStart = page.indexOf(">", nCssClassBefore + cssBefore.length());
        int nEnd = page.indexOf("<span", nStart + 1);
        String name = page.substring(nStart + 1, nEnd);
        String firstName = findFirstName(nStart, page);
        String lastName = findLastName(nStart, page);
        Player player = new Player(firstName, lastName, id, parsePoints(page, nStart));
        player.setPersonId(Long.valueOf(result.result));
        return new Helper(player, nEnd);
    }

    private int parsePoints(String page, int nStartIdx) {
        final String tagStart = "<td style=\"text-align:center;\">";
        final String tagEnd = "</td>";
        int n1 = page.indexOf(tagStart, nStartIdx) + tagStart.length();
        int n2 = page.indexOf(tagEnd, n1);
        return Integer.valueOf(page.substring(n1, n2).trim());
    }

    public String getRealName() {
        String url = "http://www.mytischtennis.de/community/index";
        try {
            String page = Client.getPage(url);
            return parseRealName(page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    private String parseRealName(String page) {
        final String tagStart = "<span class=\"usertext_ontopbar\">";
        final String tagEnd = "</span>";
        int n1 = page.indexOf(tagStart) + tagStart.length();
        int n2 = page.indexOf(tagEnd, n1);
        return page.substring(n1, n2).trim();
    }

    public List<Player> readPlayersFromTeam(String id) throws NetworkException {

        String url = "http://www.mytischtennis.de/community/teamplayers";
        if (id != null) {
            url += "?teamId=" + id;
        }
        String page = Client.getPage(url);
        return parsePlayerFromTeam(page);

    }

    public List<Event> readEvents() throws NetworkException, LoginExpiredException {
        String url = "http://www.mytischtennis.de/community/events";
        String page = Client.getPage(url);
        if (redirectedToLogin(page)) {
            throw new LoginExpiredException();
        }
        return parseEvents(page);
    }

    private List<Event> parseEvents(String page) {
        List<Event> events = new ArrayList<Event>();
        String startTag = "coolTable";
        boolean endoflist = false;

        int n = page.indexOf(startTag);
        if (n > 0) {
            while (!endoflist) {
                //find next td
                ParseResult result = readBetween(page, n, "<td>", "</td>");
                if (result == null) {
                    break;
                }
                Event event = new Event();
                events.add(event);
                event.setDate(result.result);
                n = result.end;
                result = readBetween(page, n, "openmoreinfos(", ",");
                event.setEventId(Long.valueOf(result.result));
                result = readBetween(page, n, "Details anzeigen\">", "</a>");
                event.setEvent(result.result);
                n = result.end;

                result = readBetween(page, n, "<td>", "</td>");
                n = result.end;
                event.setAk(result.result);

                result = readBetween(page, n, "<td>", "</td>");
                n = result.end;
                event.setPlayCount(result.result);
                result = readBetween(page, n, "<td>", "</td>");
                n = result.end;
                event.setWon(Short.valueOf(result.result));

                //next 3 td not interesting in
                for (int i = 0; i < 2; i++) {
                    result = readBetween(page, n, "<td>", "</td>");
                    n = result.end;
                }
                result = readBetween(page, n, "<td>", "</td>");
                event.setTtr(Integer.valueOf(result.result));
                n = result.end;
                result = readBetween(page, n, "<td ", "</td>");
                n = result.end - 15;
                result = readBetween(page, n, ">", "</span>");
                event.setSum(Short.valueOf(result.result));
                n = result.end;
                endoflist = page.indexOf("</table>", n) == -1;
            }

        }

        return events;
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

    ParseResult readBetween(String page, int start, String tagStart, String tagEnd) {
        int s = page.indexOf(tagStart, start);
        if (s == -1) {
            return null;
        }
        int e = page.indexOf(tagEnd, s + tagStart.length());
        return new ParseResult(page.substring(s + tagStart.length(), e), e);
    }

    List<Player> parsePlayerFromTeam(String page) {
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
        final String toCheck = "<div class=\"openinfos myttFeaturesTooltip\" data-tooltipdata=";

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
        String div = "</div>";
        ParseResult result = readBetween(page, startPoint - 50, "data-tooltipdata=\"", ";");
        if (result != null) {
            player.setPersonId(Long.valueOf(result.result));
        }
        if (player.getPersonId() == 0) {
            System.out.println("break");
        }

        int n = page.indexOf(tr, startPoint);
        int end = page.indexOf(div, n);
        String name = page.substring(n + tr.length(), end);
        int k = name.indexOf(',');
        player.setLastname(name.substring(0, k));
        player.setFirstname(name.substring(k + 2));

//        n = page.indexOf("personId=", n);
//        if (n > 0) {
//            end = page.indexOf("\"", n);
//            String idS = page.substring(n, end);
//            player.setPersonId(Long.valueOf(idS));
//        }

        return player;
    }

    public EventDetail readEventDetail(Event event) throws NetworkException {
        String url = "http://www.mytischtennis.de/community/eventDetails?eventId=" + event.getEventId();
        String page = Client.getPage(url);
        String startTag = "data-tooltipdata=\"";
        int n = page.indexOf(startTag);
        boolean endoflist = false;
        EventDetail eventDetail = new EventDetail();
        int j = 0;
        if (n > 0) {
            while (!endoflist) {
                ParseResult result = readBetween(page, n, startTag, ";");
                if (result == null) {
                    break;
                }
                Game game = new Game();
                eventDetail.getGames().add(game);
                game.setPlayerId(Long.valueOf(result.result));

                n = result.end;
                result = readBetween(page, n, ";", ";");
                n = result.end;
                result = readBetween(page, n, ";", ";");
                game.setPlayer(result.result);
                n = result.end;

                result = readBetween(page, n, "bigtooltip'});\">", "</span>");
                game.setPlayerWithPoints(result.result);
//
                int i = 1;
                result = readBetween(page, n, "<td>", "</td>");
                n = result.end;
                game.setResult(result.result.trim());
                while (true) {
                    result = readBetween(page, n, "<td>", "</td>");
                    if (result == null || result.result.startsWith("&nbsp;")) {
                        break;
                    }
                    game.addSet(result.result);
                    n = result.end;
                    i++;

                }
                j++;

                endoflist = page.indexOf("</table>", n) == -1;
            }

        }
        return eventDetail;
    }

    public List<Event> readEventsForForeignPlayer(long playerId) throws NetworkException, LoginExpiredException {

        String url = "http://www.mytischtennis.de/community/events?personId=" + playerId;
        String page = Client.getPage(url);
        if (redirectedToLogin(page)) {
            throw new LoginExpiredException();
        }
        return parseEvents(page);
    }

    public Player completePlayerWithTTR(Player player) throws LoginExpiredException, NetworkException {
        if (player.getPersonId() == 0) {
            System.out.println();
        }
        String url = "http://www.mytischtennis.de/community/events?personId=" + player.getPersonId();
        String page = Client.getPage(url);
        if (redirectedToLogin(page)) {
            throw new LoginExpiredException();
        }
        ParseResult result = readBetween(page, 0, "<h3 class=\"white\">", "<span");
        result = readBetween(page, result.end, "</span>", "<div");
        player.setTtrPoints(Integer.valueOf(result.result.trim()));
        return player;
    }

    List<Player> search(String firstname, String lastname, String clubName) throws NetworkException {
        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("www.mytischtennis.de")
                .path("community/ranking");


        if (clubName != null) {
            Club v = clubParser.getClubExact(clubName);
            if (v == null) {
                v = clubParser.getClubNameBestMatch(clubName);
            }
            if (v != null) {
                builder.appendQueryParameter("verein", v.getName());
                builder.appendQueryParameter("vereinId", v.getId() + "," + v.getVerband());
            }
            if (v == null) {
                Log.i(Constants.LOG_TAG, "club not found in list:" + clubName);
            }
        }
        String url = builder.build().toString();
        //bad trick for the crap from mytischtennis.de
        url = url.replace("%20", "+");


        String page = Client.getPage(url);
//        return parseForPlayer(firstName, lastName, page);
        return null;
    }

    class ParseResult {
        String result;

        int end;

        ParseResult(String result, int end) {
            this.result = result;
            this.end = end;
        }
    }

    class Helper {
        Player p;

        int idx;

        public Helper(Player player, int idx) {
            p = player;
            this.idx = idx;
        }
    }
}
