package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.Saison;

import static com.jmelzer.myttr.model.Saison.SAISON_2017;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MytClickTTWrapper {
    MyTTClickTTParser newParser = new MyTTClickTTParserImpl();
    ClickTTParser clickTTParser = new ClickTTParser();

    public void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException {
        if (verband.getMyTTClickTTUrl() == null || saison != Saison.SAISON_2018) {
            clickTTParser.readBezirkeAndLigen(verband, saison);
        } else {
            newParser.readBezirkeAndLigen(verband, saison);
        }
    }

    public void readKreiseAndLigen(Saison saison, Bezirk bezirk) throws NetworkException {
        if (saison != Saison.SAISON_2018) {
            clickTTParser.readKreiseAndLigen(bezirk);
        } else {
            newParser.readKreiseAndLigen(bezirk);
        }
    }

    public void readLiga(Saison saison, Liga liga) throws NetworkException {
        if (saison != Saison.SAISON_2018) {
            clickTTParser.readLiga(liga);
        } else {
            newParser.readLiga(liga);
        }
    }
}
