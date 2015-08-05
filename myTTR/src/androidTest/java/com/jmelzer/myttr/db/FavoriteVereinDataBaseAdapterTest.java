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

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.model.Verein;

import junit.framework.TestCase;

import java.util.List;

public class FavoriteVereinDataBaseAdapterTest extends TestCase {


    @SmallTest
    public void testDB() {
        FavoriteVereinDataBaseAdapter adapter = new FavoriteVereinDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        adapter.deleteAllEntries();
        assertTrue(adapter.insertEntry("verein", "http://bla.de") != -1);
        List<Verein> list = adapter.getAllEntries();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("verein", list.get(0).getName());
        assertEquals("http://bla.de", list.get(0).getUrl());

        assertTrue(adapter.insertEntry("verein2", "http://liga2.de") != -1);
        list = adapter.getAllEntries();
        assertEquals(2, list.size());

        assertFalse(adapter.existsEntry("noone"));
        assertTrue(adapter.existsEntry("verein2"));

        adapter.deleteAllEntries();
        list = adapter.getAllEntries();
        assertEquals(0, list.size());
    }
}
