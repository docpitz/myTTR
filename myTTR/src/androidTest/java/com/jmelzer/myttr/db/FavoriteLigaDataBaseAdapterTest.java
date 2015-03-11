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


package com.jmelzer.myttr.db;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.User;

import junit.framework.TestCase;

import java.util.List;

public class FavoriteLigaDataBaseAdapterTest extends TestCase {


    @SmallTest
    public void testDB() {
        FavoriteLigaDataBaseAdapter adapter = new FavoriteLigaDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        adapter.deleteAllEntries();
        assertTrue(adapter.insertEntry("real", "http://bla.de") != -1);
        List<Liga> list = adapter.getAllEntries();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("real", list.get(0).getName());
        assertEquals("http://bla.de", list.get(0).getUrl());

        assertTrue(adapter.insertEntry("liga2", "http://liga2.de") != -1);
        list = adapter.getAllEntries();
        assertEquals(2, list.size());

        assertFalse(adapter.existsEntry("noone"));
        assertTrue(adapter.existsEntry("liga2"));

        adapter.deleteAllEntries();
        list = adapter.getAllEntries();
        assertEquals(0, list.size());
    }
}
