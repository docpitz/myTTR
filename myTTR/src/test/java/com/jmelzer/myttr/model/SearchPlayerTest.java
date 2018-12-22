package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Club;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by cicgfp on 31.12.2017.
 */
public class SearchPlayerTest {
    @Test
    public void createName() throws Exception {

        SearchPlayer sp = new SearchPlayer();
        assertThat(sp.createName(), is("Alle Spieler"));

        sp.setFirstname("first");
        sp.setLastname("last");
        assertThat(sp.createName(), is("first-last"));

        sp = new SearchPlayer();
        sp.setClub(new Club("club", "", ""));
        assertThat(sp.createName(), is("club"));

        sp = new SearchPlayer();
        sp.setFirstname("first");
        sp.setLastname("last");
        sp.setClub(new Club("club", "", ""));
        assertThat(sp.createName(), is("first-last-club"));

        sp.setTtrFrom(1000);
        assertThat(sp.createName(), is("first-last-club (1000-∞)"));
        sp.setTtrFrom(-1);
        sp.setTtrTo(2000);
        assertThat(sp.createName(), is("first-last-club (0-2000)"));
        sp.setTtrFrom(1111);
        assertThat(sp.createName(), is("first-last-club (1111-2000)"));
    }
}