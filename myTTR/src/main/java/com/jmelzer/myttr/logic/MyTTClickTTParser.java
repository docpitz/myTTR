package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.model.MyTTPlayerIds;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;

/**
 * Created by cicgfp on 26.11.2017.
 */

public interface MyTTClickTTParser {
    void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException;

    void readKreiseAndLigen(Bezirk bezirk) throws NetworkException;

    void readLiga(Liga liga) throws NetworkException;

    void readMannschaftsInfo(Mannschaft mannschaft) throws NetworkException;

    void readVR(Liga liga) throws NetworkException;

    void readRR(Liga liga) throws NetworkException;

    void readDetail(Mannschaftspiel spiel) throws NetworkException, NoClickTTException;

    void readLigen(Kreis kreis) throws NetworkException, NoClickTTException;

    void readGesamtSpielplan(Liga liga) throws NetworkException, NoClickTTException;

    Verein readVerein(String url) throws NetworkException, NoClickTTException;

    Spieler readSpielerDetail(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException, NoClickTTException;

    Spieler readPopUp(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException, NoClickTTException;

    Verband readTopLigen() throws NetworkException;

}
