package com.jmelzer.myttr.logic;


import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TTRCalculatorTest  {

    @Test
    public void testCalc() {

        TTRCalculator calculator = new TTRCalculator();
//        assertEquals(8, calculator.calcPoints(1550, 1550, true, 16));
//        assertEquals(-8, calculator.calcPoints(1550, 1550, false, 16));
//        assertEquals(15, calculator.calcPoints(1555, 1742, true, 16));
//        assertEquals(-16, calculator.calcPoints(2555, 1742, false, 16));

        List<TTRCalculator.Game> list = new ArrayList<>();
        list.add(new TTRCalculator.Game(1668, true));
        list.add(new TTRCalculator.Game(1694, false));
        assertEquals(1, calculator.calcPoints(1669, list, 16));
    }

    @Test
    public void testCalcNuenError() {

        TTRCalculator calculator = new TTRCalculator();

        List<TTRCalculator.Game> list = new ArrayList<>();
        list.add(new TTRCalculator.Game(960, true));
        list.add(new TTRCalculator.Game(1026, false));
        assertEquals(13, calculator.calcPoints(884, list, 20));
    }
}
