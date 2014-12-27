/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

public class TTRCalculator {

    public int calcPoints(int ttrA, int ttrB, boolean win) {
        //TTRneu = TTRalt + runden[ { ( Resultat � erwartetes Resultat ) x �nderungskonstante } + Nachwuchsausgleich ]

        double pa = (1. / (1 + Math.pow(10., (ttrB - ttrA) / 150.)));

        return (int) Math.round(((win ? 1 : 0) - pa) * 16);
    }
}
