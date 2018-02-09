package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicgfp on 27.01.2018.
 */

public class Head2HeadResult {

    /**
     * gegner
     */
    String opponentName;
    String date;
    String type;
    Game game;

    public Head2HeadResult(String opponentName, String type, String date, Game game) {
        this.opponentName = opponentName;
        this.date = date;
        this.type = type;
        this.game = game;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "Head2HeadResult{" +
                "opponentName='" + opponentName + '\'' +
                ", game=" + game +
                '}';
    }
}
