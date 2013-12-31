/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.parser;

public class TTRCalculator {

    public int calcPoints(int ttrA, int ttrB, boolean win) {
        //TTRneu = TTRalt + runden[ { ( Resultat – erwartetes Resultat ) x Änderungskonstante } + Nachwuchsausgleich ]

        double pa = (1. / (1 + Math.pow(10., (ttrB - ttrA) / 150.)));
        System.out.println("pa = " + pa);

        return (int) Math.round(((win ? 1 : 0) - pa) * 16);
    }
}
