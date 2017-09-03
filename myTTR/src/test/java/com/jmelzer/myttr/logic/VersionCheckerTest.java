package com.jmelzer.myttr.logic;

import org.junit.Test;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static junit.framework.Assert.assertEquals;

/**
 * Created by cicgfp on 03.09.2017.
 */

public class VersionCheckerTest {
    final String ASSETS_DIR = "assets/github";
    VersionChecker checker = new VersionChecker();

    @Test
    public void testReadTournamentDetail() throws Exception {
        String page = readFile(ASSETS_DIR + "/releases.html");
        String[] nameAndUrl = checker.parseLastVersion(page);

        assertEquals("3.0.0 - Cups integriert", nameAndUrl[1]);
        assertEquals("https://github.com/chokdee/myTTR/releases/tag/3_0_0", nameAndUrl[0]);
    }
}
