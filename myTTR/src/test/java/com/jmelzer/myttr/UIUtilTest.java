package com.jmelzer.myttr;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by J. Melzer on 12.05.2015.
 *
 */
public class UIUtilTest {

    @Test
    public void testAbbreviate() throws Exception {
        assertNull(UIUtil.abbreviate(null, 0, 0));

        String longTest = "This is a long text";

        try {
            UIUtil.abbreviate(longTest, 2, 0);
            fail("Minimum abbreviation width is 4");
        } catch (IllegalArgumentException e) {
            //ok
        }

        assertEquals(longTest, UIUtil.abbreviate(longTest, 0, longTest.length()));

        assertEquals("This is...", UIUtil.abbreviate(longTest, 0, 10));
    }
}