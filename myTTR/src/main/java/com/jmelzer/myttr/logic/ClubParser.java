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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Parses entries from file.
 */
public class ClubParser {

    static HashMap<String, Club> clubHashMap = new HashMap<>();
    static Set<String> stopWords = new TreeSet<>();

    static List<String> clubNames = new ArrayList<>();


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
                String entryU = v.getName().toUpperCase();
                String[] searchParts = entryU.split(" |-");
                searchParts = removeStopWords(searchParts);
                v.setSearchParts(searchParts);
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

    public Club getClubNameBestMatch(String name) {
        List<String> list = getClubNameUnsharp(name);
        if (list.size() > 0) {
            return getClubExact(list.get(0));
        }
        return null;
    }

    public List<String> getClubNameUnsharp(String name) {
        return getClubNameUnsharp(name, 0.49f);
    }

    public List<String> getClubNameUnsharp(String searchString, float minScore) {
        long start = System.currentTimeMillis();
        readClubs();
        String[] searchWords = searchString.toUpperCase().split(" |-");

        List<String> subentries = new ArrayList<>();

        for (Map.Entry<String, Club> entry : clubHashMap.entrySet()) {

            float score = 0;
            int stringSumLength = 0;

            String[] myClubParts = entry.getValue().getSearchParts();

            int osum = 0;
            for (String myClubPart : myClubParts) {
                stringSumLength += myClubPart.length();
            }
            searchWords = removeStopWords(searchWords);
            for (String part : searchWords) {
                osum += part.length();
            }
            stringSumLength = Math.max(osum, stringSumLength);

            for (String searchWord : searchWords) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                for (String myClubPart : myClubParts) {
                    if (myClubPart.equals(searchWord)) {
                        score += 0.5f;
                    } else if (myClubPart.startsWith(searchWord)) {
                        score += calcScore(stringSumLength, searchWord, 1.f);
                        break;
                    } else if (myClubPart.contains(searchWord)) {
                        score += calcScore(stringSumLength, searchWord, 0.8f);
                        break;
                    }
                }
            }
            if (score > 0) {
                Log.d(Constants.LOG_TAG, "match found score=" + score + ", searchwords='" +
                        Arrays.toString(searchWords) + "', entry='" + entry + "', cleaned=" + Arrays.toString(myClubParts));

                if (score > minScore) {
                    subentries.add(entry.getValue().getName());
                    Log.d(Constants.LOG_TAG, "added match " + entry);
                } else {
                    Log.d(Constants.LOG_TAG, "score not greate enough: " + score + " < " + minScore);

                }
            }
        }
        Log.i(Constants.LOG_TAG, "club search time " + (System.currentTimeMillis() - start) + " ms");
        return subentries;

    }

    private float calcScore(float sum, String searchWord, float factor) {
        float score = 0;
        if (searchWord.length() > 5) {
            score += Math.max(0.5f, (searchWord.length() / sum) * factor);
        } else
            score += (searchWord.length() / sum) * factor;
        return score;
    }

    private String[] removeStopWords(String[] myClubParts) {
        List<String> list = new ArrayList<>();
        for (String part : myClubParts) {
            if (!stopWords.contains(part)) {
                list.add(part);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private void readClubNames() {
        clubNames = new ArrayList<>(clubHashMap.size());
        for (Club club : clubHashMap.values()) {
            clubNames.add(club.getName());
        }
    }

    public Club getClubExact(String name) {

        readClubs();

        return clubHashMap.get(name);
    }

    private synchronized void readClubs() {
        if (clubHashMap.isEmpty()) {
            int r = MyApplication.getAppContext().getResources().getIdentifier("raw/vereine",
                    "raw",
                    "com.jmelzer.myttr");
            readFile(r);
            r = MyApplication.getAppContext().getResources().getIdentifier("raw/stopwords",
                    "raw",
                    "com.jmelzer.myttr");
            readStopwords(r);
            readClubNames();
        }
    }

    private void readStopwords(int r) {
        LineNumberReader reader = null;
        InputStreamReader isReader = null;
        try {
            InputStream is = MyApplication.getAppContext().getResources().openRawResource(r);
            isReader = new InputStreamReader(is);
            reader = new LineNumberReader(isReader);

            String line = reader.readLine();
            while (line != null) {
                stopWords.add(line);
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

}
