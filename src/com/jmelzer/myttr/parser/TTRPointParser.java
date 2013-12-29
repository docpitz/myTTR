/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.parser;

import android.net.Uri;
import android.util.Log;
import com.jmelzer.myttr.Verein;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class TTRPointParser {

    public static final String ZUR_TTR_HISTORIE = "zur TTR-Historie\">TTR ";

    VereinParser vereinParser = new VereinParser();

    public int getPoints() {
        String url = "http://mytischtennis.de/community/index";

        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = Client.client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            int start = page.indexOf(ZUR_TTR_HISTORIE) + ZUR_TTR_HISTORIE.length();
            int end = page.indexOf("</a>", start + 1);
            return Integer.valueOf(page.substring(start, end));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * @param firstName
     * @param lastName
     * @return Returns the player id number or 0 if not found
     */
    public int findPlayer(String firstName, String lastName, String vereinsName) {

        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("mytischtennis.de")
                .path("community/ranking")
                .appendQueryParameter("vorname", firstName)
                .appendQueryParameter("nachname", lastName);
//                .build().toString();


        if (vereinsName != null) {
            Verein v = vereinParser.getVerein(vereinsName);
            if (v != null) {
                builder.appendQueryParameter("vereinPersonenSuche", v.getName());
                builder.appendQueryParameter("vereinIdPersonenSuche", v.getId() + "," + v.getVerband());
            }
        }
        String url = builder.build().toString();
        //bad trick for the crap from mytischtennis.de
        url = url.replace("%20", "+");



        try {
            System.out.println("url = " + url);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = Client.client.execute(httpGet);
//            System.out.println("response.getStatusLine().getStatusCode() = " + response.getStatusLine().getStatusCode());
            HttpEntity httpEntity = response.getEntity();
            String page = EntityUtils.toString(httpEntity);
            return parseTTRForPlayer(firstName, lastName, page);
//            System.out.println("page = " + page);
        } catch (IOException e) {
            Log.e("TTRParser", "findPlayer", e);
        }

        return 0;
    }

    private int parseTTRForPlayer(String firstName, String lastName, String page) {
        int idx = page.indexOf(firstName + " " + lastName);
        if (idx > 0) {
            //do we have more than one?
            int idx2 = page.indexOf(firstName + " " + lastName, idx + 1);
            if (idx2 > 0) {
                return -1;
            } else {
                final String textBeforePoints = "<td style=\"text-align:center;\">";
                idx = page.indexOf(textBeforePoints, idx) + textBeforePoints.length();
                idx2 = page.indexOf("</td>", idx);
                return Integer.valueOf(page.substring(idx, idx2).trim());
            }

        }
        return 0;
    }
}
