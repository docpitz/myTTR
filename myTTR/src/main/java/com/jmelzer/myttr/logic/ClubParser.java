/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.content.Context;
import android.util.Log;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.model.ClubSearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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


    protected void readFile(int r) {
        LineNumberReader reader = null;
        InputStreamReader isReader = null;
        try {
            InputStream is = getContext().getResources().openRawResource(r);
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
        int n = line.indexOf(",", 0);
        String name = line.substring(0, n);
        int n2 = line.indexOf(",", n + 1);
        String id = line.substring(n + 1, n2);
        String verband = line.substring(n + id.length() + 2);
        //seems to be this is fixed in mytt -->
        if (!verband.equals("FTTB") && id.length() < 3) {
            id = String.format("%03d", Long.valueOf(id));
        }
        return new Club(name, id, verband);
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
        return getClubNameUnsharp(searchString, minScore, true);
    }

    public List<String> getClubNameUnsharp(String searchString, float minScore, boolean recursiv) {
        List<ClubSearchResult> list = getClubNameUnsharpSorted(searchString, minScore, recursiv);
        List<String> listR = new ArrayList<>();
        for (ClubSearchResult clubSearchResult : list) {
            listR.add(clubSearchResult.getName());
        }
        return listR;
    }
    public List<ClubSearchResult> getClubNameUnsharpSorted(String searchString, float minScore, boolean recursiv) {
        long start = System.currentTimeMillis();
        readClubs();
        String[] toSearchWords = searchString.toUpperCase().split(" |-");

        List<ClubSearchResult> subentries = new ArrayList<>();
        if (clubHashMap.containsKey(searchString)) {
            subentries.add(new ClubSearchResult(clubHashMap.get(searchString).getName(), 1.0f));
            return subentries;
        }


        for (Map.Entry<String, Club> entry : clubHashMap.entrySet()) {

            float score = 0;
            int stringSumLength = 0;

            String[] myClubParts = entry.getValue().getSearchParts();

            int osum = 0;
            for (String myClubPart : myClubParts) {
                stringSumLength += myClubPart.length();
            }
            toSearchWords = removeStopWords(toSearchWords);
            for (String part : toSearchWords) {
                osum += part.length();
            }
            stringSumLength = Math.max(osum, stringSumLength);

            for (String searchWord : toSearchWords) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                for (String myClubPart : myClubParts) {
                    if (!stopWords.contains(myClubPart) && searchWord.equals(myClubPart)) {
                        score += 2; //exact match of a word
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
                        Arrays.toString(toSearchWords) + "', entry='" + entry + "', cleaned=" + Arrays.toString(myClubParts));

                if (score > minScore) {
                    subentries.add(new ClubSearchResult(entry.getValue().getName(), score));
                    Log.d(Constants.LOG_TAG, "added match " + entry);
                } else {
                    Log.d(Constants.LOG_TAG, "score not greate enough: " + score + " < " + minScore);

                }
            }
        }
        Log.i(Constants.LOG_TAG, "club search time " + (System.currentTimeMillis() - start) + " ms");
        if (recursiv && subentries.size() == 0 && minScore > 0.1f) {
            return getClubNameUnsharpSorted(searchString, minScore - 0.2f, recursiv);
        }
        Collections.sort(subentries, new Comparator<ClubSearchResult>() {
            @Override
            public int compare(ClubSearchResult o1, ClubSearchResult o2) {
                return -1*Float.compare(o1.getScore(), o2.getScore());
            }
        });
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

    protected void readClubNames() {
        clubNames = new ArrayList<>(clubHashMap.size());
        for (Club club : clubHashMap.values()) {
            clubNames.add(club.getName());
        }
    }

    public Club getClubExact(String name) {

        readClubs();

        return clubHashMap.get(name);
    }

    synchronized void readClubs() {
        if (clubHashMap.isEmpty()) {
            int r = getContext().getResources().getIdentifier("raw/stopwords",
                    "raw",
                    "com.jmelzer.myttr");
            readStopwords(r);
            r = getContext().getResources().getIdentifier("raw/vereine",
                    "raw",
                    "com.jmelzer.myttr");
            readFile(r);

            readClubNames();
        }
    }

    protected void readStopwords(int r) {
        LineNumberReader reader = null;
        InputStreamReader isReader = null;
        try {
            InputStream is = getContext().getResources().openRawResource(r);
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

    protected Context getContext() {
        return MyApplication.getAppContext();
    }


}
