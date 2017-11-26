package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.model.Saison;

/**
 * Created by cicgfp on 26.11.2017.
 */

public interface MyTTClickTTParser {
    void readBezirkeAndLigen(Verband verband, Saison saison) throws NetworkException;
}
