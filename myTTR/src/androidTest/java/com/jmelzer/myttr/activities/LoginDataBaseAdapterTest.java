/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

/*
* Author: J. Melzer
* Date: 31.12.13 
*
*/


package com.jmelzer.myttr.activities;

import android.test.suitebuilder.annotation.SmallTest;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.User;

import junit.framework.TestCase;

public class LoginDataBaseAdapterTest extends TestCase {

    @SmallTest
    public void testDB() {
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        adapter.deleteAllEntries();
        assertTrue(adapter.insertEntry("real", "testi", "pwww", 4711, "dummyClub") != -1);
        User u = adapter.getSinlgeEntry();
        assertNotNull(u);
        assertEquals("real", u.getRealName());
        assertEquals("testi", u.getUsername());
        assertEquals("pwww", u.getPassword());
        assertEquals("dummyClub", u.getClubName());
        assertEquals(4711, u.getPoints());
        assertTrue(u.getChangedAt() != null);

        adapter.storeClub("changed");
        u = adapter.getSinlgeEntry();
        assertNotNull(u);
        assertEquals("changed", u.getClubName());
    }
}
