package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.model.Saison;

import org.junit.Test;

import static com.jmelzer.myttr.Verband.WTTV;
import static com.jmelzer.myttr.Verband.dttb;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by cicgfp on 17.12.2017.
 */

public class MytClickTTWrapperTest {
    MytClickTTWrapper wrapper = new MytClickTTWrapper();
    @Test
    public void isClickTT() {
        assertTrue(wrapper.isClickTT(Saison.SAISON_2018, dttb));
        assertFalse(wrapper.isClickTT(Saison.SAISON_2018, WTTV));
        assertTrue(wrapper.isClickTT(Saison.SAISON_2017, WTTV));

    }
}
