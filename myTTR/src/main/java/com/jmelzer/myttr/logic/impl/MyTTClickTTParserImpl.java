package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.AbstractBaseParser;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
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
        verband.setBezirkList(list, saison);

        //read link to ligen and load it
        ParseResult result = readBetween(page, 0, "<div class=\"row m-l text-center\">",
                "</div>");
        if (!isEmpty(result)) {
            String urlLiga = readHrefAndATag(result.result)[0];
            page = Client.getPage(UrlUtil.getHttpAndDomain(url) + urlLiga);

            List<Liga> ligen = parseLigaLinks(page);
            verband.addAllLigen(ligen, saison);
        }

    }

    @Override
    public void readKreiseAndLigen(Bezirk bezirk) throws NetworkException {
        String page = Client.getPage(bezirk.getUrl());
        List<Liga> listLiga = parseLigaLinks(page);
        bezirk.addAllLigen(listLiga);
    }

    @Override
    public void readLiga(Liga liga) throws NetworkException {
        String url = liga.getUrl();
        String page = Client.getPage(url);
        parseLiga(liga, page);
    }

    void parseLiga(Liga liga, String page) {
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
            String nameWithRef = row[2];
            String[] href = readHrefAndATag(nameWithRef);
            Mannschaft m = new Mannschaft(href[1],
                    Integer.valueOf(row[1]),
                    Integer.valueOf(row[3]),
                    Integer.valueOf(row[4]),
                    Integer.valueOf(row[5]),
                    Integer.valueOf(row[6]),
                    row[7],
                    row[8],
                    row[9], href[0]);
            m.setUrl(UrlUtil.safeUrl(liga.getHttpAndDomain(), m.getUrl()));
            liga.addMannschaft(m);
            idx = resultrow.end;

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
