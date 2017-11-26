package com.jmelzer.myttr.logic.impl;

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
        if (verband.getMyTTClickTTUrl() == null || saison != SAISON_2017) {
            clickTTParser.readBezirkeAndLigen(verband, saison);
        } else
            newParser.readBezirkeAndLigen(verband, saison);
    }
}
