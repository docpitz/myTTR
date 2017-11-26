package com.jmelzer.myttr.logic;


import android.support.test.filters.SmallTest;
import android.util.Log;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;
import com.jmelzer.myttr.model.Saison;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MyTTClickTTParserIntegrationTest extends BaseTestCase {
    MyTTClickTTParser parser = new MyTTClickTTParserImpl();

    @SmallTest
    public void testReadBezirkeAndLigen() throws Exception {
        login();

        Verband wttv = Verband.verbaende.get(Verband.verbaende.size() - 1);
        parser.readBezirkeAndLigen(wttv, Saison.SAISON_2018);
        assertEquals(5, wttv.getBezirkList().size());
        for (Bezirk bezirk : wttv.getBezirkList()) {
            Log.d(Constants.LOG_TAG, "bezirk = " + bezirk);
        }
        assertEquals(48, wttv.getLigaList().size());
    }

}
