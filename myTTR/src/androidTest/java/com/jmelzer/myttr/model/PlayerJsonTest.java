package com.jmelzer.myttr.model;

import android.test.suitebuilder.annotation.SmallTest;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Player;

import junit.framework.TestCase;

/**
 * Created by J. Melzer on 31.12.2017.
 */
public class PlayerJsonTest extends TestCase  {

    @SmallTest
    public void testJson() throws Exception {
        Player sp = new Player();
        sp.setFirstname("first");
        sp.setLastname("last");
        sp.setPersonId(1234);

        String json = sp.convertToJson();
        Player sp2 = Player.convertFromJson(json);

        assertEquals(sp, sp2);

    }
}