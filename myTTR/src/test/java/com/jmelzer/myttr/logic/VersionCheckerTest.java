package com.jmelzer.myttr.logic;


import com.jmelzer.myttr.db.NotificationDataBaseAdapter;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Created by J. Melzer on 03.09.2017.
 */

public class VersionCheckerTest   {
    final String ASSETS_DIR = "assets/github";
    VersionChecker checker = new VersionChecker();

    @Test
    public void testReadTournamentDetail() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/releases.html");
        String[] nameAndUrl = checker.parseLastVersion(page);

        assertEquals("3.0.0 - Cups integriert", nameAndUrl[1]);
        assertEquals("https://github.com/chokdee/myTTR/releases/tag/3_0_0", nameAndUrl[0]);
    }

    @Test
    public void newVersionAvailable() throws Exception {
        checker.adapter = Mockito.mock(NotificationDataBaseAdapter.class);
        String page = TestUtil.readFile(ASSETS_DIR + "/releases-301.html");
        boolean b = checker.compareVersions(page, "3.0.1 Suche verbessert");

        assertEquals(false, b);

        b = checker.compareVersions(page, "3.0.1 - Suche verbessert");
        assertEquals(true, b);
    }
}
