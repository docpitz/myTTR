package com.jmelzer.myttr.model;

import android.test.suitebuilder.annotation.SmallTest;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.model.SearchPlayer;

import junit.framework.TestCase;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by J. Melzer on 31.12.2017.
 */
public class SearchPlayerJsonTest extends TestCase  {

    @SmallTest
    public void testJson() throws Exception {
        SearchPlayer sp = new SearchPlayer();
        sp.setFirstname("first");
        sp.setLastname("last");
        sp.setClub(new Club("club", "id", "verband"));
        sp.setTtrFrom(1000);
        sp.setTtrTo(2000);

        String json = sp.convertToJson();
        SearchPlayer sp2 = SearchPlayer.convertFromJson(json);

        assertEquals(sp, sp2);

    }
}