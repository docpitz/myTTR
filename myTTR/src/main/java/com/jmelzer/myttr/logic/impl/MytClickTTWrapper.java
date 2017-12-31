package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.model.MyTTPlayerIds;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MytClickTTWrapper {
    MyTTClickTTParser newParser = new MyTTClickTTParserImpl();
    ClickTTParser clickTTParser = new ClickTTParser();

    public void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readBezirkeAndLigen(verband, saison);
        } else {
            newParser.readBezirkeAndLigen(verband, saison);
        }
    }

    public void readKreiseAndLigen(Saison saison, Bezirk bezirk, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readKreiseAndLigen(bezirk);
        } else {
            newParser.readKreiseAndLigen(bezirk);
        }
    }

    public void readLiga(Saison saison, Liga liga, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readLiga(liga);
        } else {
            newParser.readLiga(liga);
        }
    }

    boolean isClickTT(Saison saison) {
        return !(saison == Saison.SAISON_2018 );
//        return false;
    }

    public void readMannschaftsInfo(Saison saison, Mannschaft mannschaft) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readMannschaftsInfo(mannschaft);
        } else {
            newParser.readMannschaftsInfo(mannschaft);
        }
    }

    public void readVR(Saison saison, Liga liga, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readVR(liga);
        } else {
            newParser.readVR(liga);
        }
    }


    public void readRR(Saison saison, Liga liga, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readRR(liga);
        } else {
            newParser.readRR(liga);
        }
    }

    public void readGesamtSpielplan(Saison saison, Liga liga, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readGesamtSpielplan(liga);
        } else {
            newParser.readGesamtSpielplan(liga);
        }
    }

    public void readDetail(Saison saison, Mannschaftspiel spiel, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readDetail(spiel);
        } else {
            newParser.readDetail(spiel);
        }
    }

    public void readLigen(Kreis kreis, Saison saison, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            clickTTParser.readLigen(kreis);
        } else {
            newParser.readLigen(kreis);
        }
    }

    public Verein readVerein(String url, Saison saison, Verband verband) throws NetworkException {
        if (isClickTT(saison)) {
            return clickTTParser.readVerein(url);
        } else {
            return newParser.readVerein(url);
        }
    }

    public Spieler readSpielerDetail(Saison saison, Verband verband, String name, String url, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException {
        if (isClickTT(saison)) {
            return clickTTParser.readSpielerDetail(name, url);
        } else {
            return newParser.readSpielerDetail(name, myTTPlayerIdsForPlayer);
        }
    }

    public Spieler readPopUp(Saison saison, Verband verband, String name, String url, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException {
        if (isClickTT(saison)) {
            return null;
        } else {
            return newParser.readPopUp(name, myTTPlayerIdsForPlayer);
        }
    }

    public Verband readTopLigen(Saison saison) throws NetworkException {
        if (isClickTT(saison)) {
            return clickTTParser.readTopLigen(saison);
        } else {
            return newParser.readTopLigen();
        }
    }
}
