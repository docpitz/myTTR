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
import com.jmelzer.myttr.model.LastNotification;

import junit.framework.TestCase;

import java.util.List;

public class NotificationDataBaseAdapterTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testDB() {
        NotificationDataBaseAdapter adapter = new NotificationDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        adapter.deleteAllEntries();
        assertTrue(adapter.insertEntry("typi", "jsondata bla bla") != -1);
        assertTrue(adapter.existsEntry("typi"));
        LastNotification lastNotification = adapter.getEntryByType("typi");
        assertNotNull(lastNotification);
        assertEquals("typi", lastNotification.getType());
        assertEquals("jsondata bla bla", lastNotification.getJsonData());

        assertFalse(adapter.existsEntry("noone"));

        adapter.deleteAllEntries();
    }
}
