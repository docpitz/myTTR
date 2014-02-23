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
import com.jmelzer.myttr.logic.TTRCalculator;
import junit.framework.TestCase;

public class TTRCalculatorTest extends TestCase {

    @SmallTest
    public void testCalc() {

        TTRCalculator calculator = new TTRCalculator();
        assertEquals(8, calculator.calcPoints(1550, 1550, true));
        assertEquals(-8, calculator.calcPoints(1550, 1550, false));
        assertEquals(15, calculator.calcPoints(1555, 1742, true));
        assertEquals(-16, calculator.calcPoints(2555, 1742, false));

    }
}
