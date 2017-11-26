package com.jmelzer.myttr.logic.impl;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.logic.TestUtil;

import org.junit.Test;

import java.util.List;

/**
 * Created by cicgfp on 26.11.2017.
 */

public class MyTTClickTTParserTest {
    final String ASSETS_DIR = "assets/mytt/clicktt";

    MyTTClickTTParserImpl parser = new MyTTClickTTParserImpl();

    @Test
    public void testparseLigaLinks() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-ligenplan.html");
        List<Liga> ligen = parser.parseLigaLinks(page);
        for (Liga liga : ligen) {
            System.out.println("liga = " + liga);
        }
    }

    @Test
    public void parseLinksBezirke() throws Exception {
        String page = TestUtil.readFile(ASSETS_DIR + "/wttv-bezirke.html");
        List<Bezirk> bezirkList = parser.parseLinksBezirke(page);
        for (Bezirk bezirk : bezirkList) {
            System.out.println("bezirk = " + bezirk);
        }
    }
}
