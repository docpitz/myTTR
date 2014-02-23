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
        adapter.deleteEntry("testi");
        assertTrue(adapter.insertEntry("testi", "pwww", 4711) != -1);
        User u = adapter.getSinlgeEntry();
        assertNotNull(u);
        assertEquals("testi", u.getUsername());
        assertEquals("pwww", u.getPassword());
        assertEquals(4711, u.getPoints());
    }
}
