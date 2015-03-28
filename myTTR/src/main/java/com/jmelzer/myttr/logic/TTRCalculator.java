/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import java.util.List;

public class TTRCalculator {

    public static class Game {
        public int ttr;
        boolean win;
        double pa;

        public Game(int ttr, boolean win) {
            this.ttr = ttr;
            this.win = win;
        }
    }

    /**
     * calculate the ttr points
     * @param ttrA player a
     * @param ttrB list of player from type Game
     * @param ak factor
     * @return difference from the old ttr, maybe negativ
     */
    public int calcPoints(int ttrA, List<Game> ttrB, int ak) {
        for (Game game : ttrB) {
            game.pa = (1. / (1 + Math.pow(10., (game.ttr - ttrA) / 150.)));
        }
        double result = 0;
        for (Game game : ttrB) {
            result += (game.win ? 1 : 0) - game.pa;
        }
        return (int) Math.round(result * ak);
    }

}
