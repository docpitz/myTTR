package com.jmelzer.myttr;

import com.jmelzer.myttr.model.Saison;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by J. Melzer on 12.05.2015.
 *
 */
public class VerbandTest {

    @Test
    public void testReplaceYear() throws Exception {
        List<Verband> verbandList = Verband.verbaende;
        for (Verband verband : verbandList) {
            assertEquals(verband.url, verband.replaceYear(verband.url, Saison.SAISON_2015));
            System.out.println(verband.replaceYear(verband.url, Saison.SAISON_2018));
        }

        for (Verband verband : verbandList) {
            String u = verband.replaceYear(verband.url, Saison.SAISON_2016);
            assertFalse(u, u.contains("2014"));
        }
        for (Verband verband : verbandList) {
            String u = verband.replaceYear(verband.url, Saison.SAISON_2017);
            assertFalse(u, u.contains("2014"));
        }
        for (Verband verband : verbandList) {
            String u = verband.replaceYear(verband.url, Saison.SAISON_2018);
            assertFalse(u, u.contains("14"));
            assertFalse(u, u.contains("15"));
            assertFalse(u, u.contains("16"));
        }
        try {
            Verband.dttb.replaceYear(Verband.dttb.url, Saison.SAISON_2019);
            fail("not configured");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
}