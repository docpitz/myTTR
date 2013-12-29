/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.parser;


import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.Assert;
import junit.framework.TestCase;

public class VereinParserTest extends TestCase {

    @SmallTest
    public void testGetVerein() {
        VereinParser vereinParser = new VereinParser();
        assertNull(vereinParser.getVerein("Bla"));
        assertNotNull(vereinParser.getVerein("TTG St. Augustin"));
        assertNotNull(vereinParser.getVerein("TV Bergheim/Sieg"));

    }
}
