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
    void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException, LoginExpiredException;

    void readKreiseAndLigen(Bezirk bezirk) throws NetworkException, LoginExpiredException;

    void readLiga(Liga liga) throws NetworkException, LoginExpiredException;

    void readMannschaftsInfo(Mannschaft mannschaft) throws NetworkException, LoginExpiredException;

    void readVR(Liga liga) throws NetworkException, LoginExpiredException;

    void readRR(Liga liga) throws NetworkException, LoginExpiredException;

    void readDetail(Mannschaftspiel spiel) throws NetworkException, NoClickTTException, LoginExpiredException;

    void readLigen(Kreis kreis) throws NetworkException, NoClickTTException, LoginExpiredException;

    void readGesamtSpielplan(Liga liga) throws NetworkException, NoClickTTException, LoginExpiredException;

    Verein readVerein(String url) throws NetworkException, NoClickTTException, LoginExpiredException;

    Spieler readSpielerDetail(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException, NoClickTTException, LoginExpiredException;

    Spieler readPopUp(String name, MyTTPlayerIds myTTPlayerIdsForPlayer) throws NetworkException, NoClickTTException, LoginExpiredException;

    Verband readTopLigen() throws NetworkException, LoginExpiredException;
    Verein readOwnVerein() throws NetworkException, LoginExpiredException, NoClickTTException;

    void readAdressen(Liga mannschaft) throws LoginExpiredException, NetworkException;

    void readOwnAdressen(Liga liga) throws LoginExpiredException, NetworkException;
}
