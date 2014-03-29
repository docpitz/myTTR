/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.net.Uri;
import android.util.Log;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Player;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);

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
                return Integer.valueOf(page.substring(start, end));
            } catch (NumberFormatException e) {
//                String filename = "myttr.html";
//                FileOutputStream outputStream;
//
//                File file = new File("/storage/sdcard0/Download", filename);
//                outputStream = new FileOutputStream(file);
//                outputStream.write(page.getBytes());
//                outputStream.close();
//                file.setReadable(true);
                return -3;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void checkIfPlayerRegisteredWithClub(String page) throws PlayerNotWellRegistered {
        final String toCheck = "Du bist für uns leider nicht eindeutig";

        if (page.indexOf(toCheck) > 0) {
            throw new PlayerNotWellRegistered();
        }
    }

    public Player findPlayerWithClubName(String firstName, String lastName, String clubName) throws TooManyPlayersFound {
        List<String> list = clubParser.getClubNameUnsharp(clubName);
        if (list.size() == 0) {
            return null;
        }
        Club club = clubParser.getClubExact(list.get(0));
        return findPlayer(firstName, lastName, club.getName());
    }

    /**
     * @param firstName
     * @param lastName
     * @return Returns the player id number or 0 if not found
     */
    public Player findPlayer(String firstName, String lastName, String vereinsName) throws TooManyPlayersFound {

        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("www.mytischtennis.de")
                .path("community/ranking")
                .appendQueryParameter("vorname", firstName)
                .appendQueryParameter("nachname", lastName);


        if (vereinsName != null) {
            Club v = clubParser.getClubExact(vereinsName);
            if (v != null) {
                builder.appendQueryParameter("vereinPersonenSuche", v.getName());
                builder.appendQueryParameter("vereinIdPersonenSuche", v.getId() + "," + v.getVerband());
            }
        }
        String url = builder.build().toString();
        //bad trick for the crap from mytischtennis.de
        url = url.replace("%20", "+");


        try {
            Log.d(Constants.LOG_TAG, "url = " + url);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            return parseForPlayer(firstName, lastName, page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }

        return null;
    }

    private Player parseForPlayer(String firstName, String lastName, String page) throws TooManyPlayersFound {
        String pageU = page.toUpperCase();
        String name = (firstName + " " + lastName).toUpperCase();
        int idx = pageU.indexOf(name);
        if (idx > 0) {
            //do we have more than one?
            int idx2 = pageU.indexOf(name, idx + 1);
            if (idx2 > 0) {
                throw new TooManyPlayersFound();
            } else {
                Player player = new Player();
                player.setTtrPoints(findPoints(idx, pageU));
                player.setFirstname(findFirstName(idx, page));
                player.setLastname(findLastName(idx, page));
                return player;
            }

        }
        return null;
    }

    private String findFirstName(int startIdx, String page) {
        String toFind = ">";
        String toFindEnd = "<strong>";
        int idx = page.indexOf(toFind, startIdx) + toFind.length();
        int idx2 = page.indexOf(toFindEnd, idx);

        System.out.println(page.substring(idx, idx2));
        System.out.println(page.substring(idx - 10, idx2 - 10));

        return page.substring(idx, idx2).trim();
    }

    private String findLastName(int startIdx, String page) {
        String toFind = "<strong>";
        String toFindClose = "</strong>";
        int idx = page.indexOf(toFind, startIdx) + toFind.length();
        int idx2 = page.indexOf(toFindClose, idx);

        System.out.println(page.substring(idx, idx2));
        System.out.println(page.substring(idx - 10, idx2 - 10));

        return page.substring(idx, idx2).trim();
//        return null;
    }

    int findPoints(final int startIdx, String pageU) {
        final String textBeforePoints = "<TD STYLE=\"TEXT-ALIGN:CENTER;\">";
        int idx = pageU.indexOf(textBeforePoints, startIdx) + textBeforePoints.length();
        int idx2 = pageU.indexOf("</TD>", idx);
        return Integer.valueOf(pageU.substring(idx, idx2).trim());
    }

    public List<Player> getClubList() {
        String firstPage = "http://www.mytischtennis.de/community/showclubinfo";
        HttpGet httpGet = new HttpGet(firstPage);
        try {
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            int n = page.indexOf("vereinid=");
            if (n > 0) {
                int n2 = page.indexOf("&", n);
                String id = page.substring(n + 9, n2);

                String clubListUrl = "http://www.mytischtennis.de/community/ranking?vereinid=" + id +
                                     "&alleSpielberechtigen=yes";
                httpGet = new HttpGet(clubListUrl);
                response = Client.client.execute(httpGet);
                httpEntity = response.getEntity();
                page = EntityUtils.toString(httpEntity);
                int idx = 0;
                List<Player> list = new ArrayList<Player>();
                Helper h = getNextPlayer(page, id, idx);
                while (h != null) {
                    list.add(h.p);
                    h = getNextPlayer(page, id, h.idx);
                }
                return list;

            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    public String getNameOfOwnClub() {
        String firstPage = "http://www.mytischtennis.de/community/userMasterPage";
        HttpGet httpGet = new HttpGet(firstPage);
        try {
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            return readClubName(page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    private String readClubName(String page) {
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
        int nStart = page.indexOf(">", nCssClassBefore + cssBefore.length());
        int nEnd = page.indexOf("<span", nStart + 1);
        String name = page.substring(nStart + 1, nEnd);
//        System.out.println("name = " + name);
        String firstName = findFirstName(nStart, page);
        String lastName = findLastName(nStart, page);
        Player player = new Player(firstName, lastName, id, parsePoints(page, nStart));
//        System.out.println("player = " + player);
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
        String firstPage = "http://www.mytischtennis.de/community/index";
        HttpGet httpGet = new HttpGet(firstPage);
        try {
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
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

    public List<Player> readPlayersFromTeam(String id) {
        String firstPage = "http://www.mytischtennis.de/community/teamplayers?teamId=" + id;
        HttpGet httpGet = new HttpGet(firstPage);
        try {
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            return parsePlayerFromTeam(page);
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
        }
        return null;

    }

    private List<Player> parsePlayerFromTeam(String page) {
        Set<Player> set = new TreeSet<Player>();

        final String toCheck = "<div class=\"openinfos myttFeaturesTooltip\" data-tooltipdata=";

        String trClosed = "</td>";

        int start = page.indexOf(toCheck);
        if (start > 0) {
            int n = start;
            while (n > 0) {
                n += toCheck.length();
                set.add(readPlayer(page, n));
                n = page.indexOf(trClosed, n);
                n = page.indexOf(toCheck, n);
            }
        }
        return new ArrayList<Player>(set);

    }

    private Player readPlayer(String page, int startPoint) {
        String tr = "\">";
        Player player = new Player();
        String div = "</div>";
        int n = page.indexOf(tr, startPoint);
        int end = page.indexOf(div, n);
        String name = page.substring(n + tr.length(), end);
        int k = name.indexOf(',');
        player.setLastname(name.substring(0, k));
        player.setFirstname(name.substring(k + 2));


        return player;
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
