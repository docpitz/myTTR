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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyTischtennisParser {

    public static final String ZUR_TTR_HISTORIE = "zur TTR-Historie\">TTR ";
    ClubParser clubParser = new ClubParser();

    public int getPoints() throws PlayerNotWellRegistered {
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
//            System.out.println("response.getStatusLine().getStatusCode() = " + response.getStatusLine().getStatusCode());
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            return parseForPlayer(firstName, lastName, page);
//            System.out.println("page = " + page);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "findPlayer", e);
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
}
