package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.Saison;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MytClickTTWrapper {
    MyTTClickTTParser newParser = new MyTTClickTTParserImpl();
    ClickTTParser clickTTParser = new ClickTTParser();

    public void readBezirkeAndLigen(Saison saison, Verband verband) throws NetworkException {
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

    public void readMannschaftsInfo(Saison saison, Mannschaft mannschaft) throws NetworkException {
        if (saison != Saison.SAISON_2018) {
            clickTTParser.readMannschaftsInfo(mannschaft);
        } else {
            newParser.readMannschaftsInfo(mannschaft);
        }
    }

    public void readVR(Saison saison, Liga liga) throws NetworkException {
        if (saison != Saison.SAISON_2018) {
            clickTTParser.readVR(liga);
        } else {
            newParser.readVR(liga);
        }
    }



    public void readRR(Saison saison, Liga liga) {


    }

    public void readGesamtSpielplan(Saison saison, Liga liga) {

    }

    public void readDetail(Saison saison, Mannschaftspiel spiel) throws NetworkException {
        if (saison != Saison.SAISON_2018) {
            clickTTParser.readDetail(spiel);
        } else {
            newParser.readDetail(spiel);
        }
    }
}
