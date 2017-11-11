package com.jmelzer.myttr.logic;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.jmelzer.myttr.model.LastNotification;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by J. Melzer on 03.09.2017.
 */

@SmallTest
@RunWith(AndroidJUnit4.class)
public class VersionCheckerIntegrationTest extends BaseTestCase {

    @Test
    public void versioncheck() throws Exception {
        assertTrue(new VersionChecker().newVersionAvailable());
    }

    @Test
    public void shallCheck() throws Exception {
        assertTrue(new VersionChecker().shallCheck());
    }

    @Test
    public void workflow() throws Exception {
        VersionChecker versionChecker = new VersionChecker();
        versionChecker.getDBAdapter().deleteAllEntries();

        LastNotification ln = versionChecker.getLastCheck();
        assertNull(ln);
        assertTrue(versionChecker.shallCheck());
        assertTrue(versionChecker.newVersionAvailable());
        assertEquals(VersionChecker.LAST_VERSION, versionChecker.readVersionInfo()[1]);
        ln = versionChecker.getLastCheck();
        assertNotNull(ln);

        assertFalse("check allready done, only once a week", versionChecker.shallCheck());
        assertTrue("still a new version there", versionChecker.newVersionAvailable());
        versionChecker.getDBAdapter().deleteAllEntries();
    }
}
