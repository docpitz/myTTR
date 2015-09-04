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

import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.model.Favorite;
import com.jmelzer.myttr.model.Verein;

import junit.framework.TestCase;

import java.util.List;

public class FavoriteDataBaseAdapterTest extends ApplicationTestCase<MyApplication> {


    public FavoriteDataBaseAdapterTest() {
        super(MyApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testDB() {
        createApplication();
//        SQLiteDatabase db = DataBaseHelper.getInstance(getContext()).getWritableDatabase();
//        DataBaseHelper.getInstance(getContext()).onUpgrade(db, 1, 16);

        FavoriteDataBaseAdapter adapter = new FavoriteDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        adapter.deleteAllEntries();
        assertTrue(adapter.insertEntry("verein", "http://bla.de", Liga.class.getName()) != -1);
        List<Favorite> list = adapter.getAllEntries();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("verein", list.get(0).getName());
        assertEquals("http://bla.de", list.get(0).getUrl());

        assertTrue(adapter.insertEntry("verein2", "http://liga2.de", Verein.class.getName()) != -1);
        list = adapter.getAllEntries();
        assertEquals(2, list.size());

        assertFalse(adapter.existsEntry("noone"));
        assertTrue(adapter.existsEntry("verein2"));

        adapter.deleteAllEntries();
        list = adapter.getAllEntries();
        assertEquals(0, list.size());
    }
}
