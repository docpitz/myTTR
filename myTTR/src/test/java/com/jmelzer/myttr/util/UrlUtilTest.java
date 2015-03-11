package com.jmelzer.myttr.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by J. Melzer on 11.03.2015.
 * Tests the class UrlUtil
 */
public class UrlUtilTest {

    @Test
    public void testGeoFormatForMaps() throws Exception {
        String s = UrlUtil.formatAddressToGoogleMaps("Spiellokal 2: Sporthalle Süd\n" +
                "Eifelstr., 53859 Niederkassel-Mondorf");
        assertEquals("geo:0,0?q=Sporthalle%20Süd+Eifelstr.+53859%20Niederkassel-Mondorf", s);
    }
}
