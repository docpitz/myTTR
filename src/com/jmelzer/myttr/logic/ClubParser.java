/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.util.Log;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Parses entries from file. */
public class ClubParser {


    static HashMap<String, Club> clubHashMap = new HashMap<String, Club>();

    static List<String> clubNames = new ArrayList<String>();

    public Club getClubNameBestMatch(String name) {
        List<String> list = getClubNameUnsharp(name);
        if (list.size() > 0) {
            return getClubExact(list.get(0));
        }
        return null;
    }

    public List<String> getClubNameUnsharp(String name) {

        readClubs();

        String[] parts = name.toUpperCase().split(" ");

        List<String> subentries = new ArrayList<String>();

        for (String entry : clubNames) {
            boolean match = true;
            String entryU = entry.toUpperCase();
            for (String part : parts) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                if (!entryU.contains(part)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                subentries.add(entry);
            }
        }
        return subentries;

    }

    private void readClubNames() {
        clubNames = new ArrayList<String>(clubHashMap.size());
        for (Club club : clubHashMap.values()) {
            clubNames.add(club.getName());
        }
    }

    public Club getClubExact(String name) {

        readClubs();
        //todo better parser e.g. matcher
        return clubHashMap.get(name);
    }

    private synchronized void readClubs() {
        if (clubHashMap.isEmpty()) {
            int r = MyApplication.getAppContext().getResources().getIdentifier("raw/vereine", "raw",
                    "com.jmelzer.myttr");
            readFile(r);
            readClubNames();
        }
    }

    private void readFile(int r) {
        LineNumberReader reader = null;
        InputStreamReader isReader = null;
        try {
            InputStream is = MyApplication.getAppContext().getResources().openRawResource(r);
            isReader = new InputStreamReader(is);
            reader = new LineNumberReader(isReader);

            String line = reader.readLine();
            while (line != null) {
                Club v = parseLine(line);
                clubHashMap.put(v.getName(), v);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "", e);
        } finally {
            if (reader != null) {
                try {
                    isReader.close();
                    reader.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    private Club parseLine(String line) {
//        'SV%20Bohlingen','21017,STTB','SV Bohlingen'
        String rest = line.substring(1);
        String webName = rest.substring(0, rest.indexOf("'"));
        rest = rest.substring(webName.length() + 3);
        String id = rest.substring(0, rest.indexOf(","));
        rest = rest.substring(id.length() + 1);
        String verband = rest.substring(0, rest.indexOf("'"));
        rest = rest.substring(verband.length() + 3);
        String name = rest.substring(0, rest.indexOf("'"));
        return new Club(name, id, verband, webName);
    }

    public HashMap<String, Club> getClubHashMap() {
        return clubHashMap;
    }

    public String getClubNameById(String id) {
        readClubs();
        for (String key : clubHashMap.keySet()) {
            Club c = clubHashMap.get(key);
            if (c.getId().equals(id)) {
                return c.getName();
            }
        }
        return null;
    }
}
