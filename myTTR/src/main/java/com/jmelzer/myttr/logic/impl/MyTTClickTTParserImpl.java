package com.jmelzer.myttr.logic.impl;

import android.text.Html;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Competition;
import com.jmelzer.myttr.Group;
import com.jmelzer.myttr.KoPhase;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Participant;
import com.jmelzer.myttr.Spielbericht;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Tournament;
import com.jmelzer.myttr.TournamentGame;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.AbstractBaseParser;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;
import com.jmelzer.myttr.util.UrlUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by J. Melzer on 26.11.2017.
 * Parse the click tt pages from mytt
 */
public class MyTTClickTTParserImpl extends AbstractBaseParser implements MyTTClickTTParser {

    public static final String CONTAINER_START_TAG = "<div class=\"col-sm-6 col-xs-12\">";

    @Override
    public void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException {
        String url = "";
        url += verband.getUrlFixed(saison);
        String page = Client.getPage(url);
        List<Bezirk> list = parseLinksBezirke(page);
        verband.setBezirkList(list);

        //read link to ligen and load it
        ParseResult result = readBetween(page, 0, "<div class=\"row m-l text-center\">",
                "</div>");
        if (!isEmpty(result)) {
            String urlLiga = readHrefAndATag(result.result)[0];
            page = Client.getPage(UrlUtil.getHttpAndDomain(url) + urlLiga);

            List<Liga> ligen = parseLigaLinks(page);
            verband.addAllLigen(ligen);
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
                Bezirk v = new Bezirk(rn.result, urlref[0]);
                bezirkList.add(v);

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
}
