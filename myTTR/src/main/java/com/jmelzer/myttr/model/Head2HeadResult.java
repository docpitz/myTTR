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

    List<Game> games = new ArrayList<>();

    public List<Game> getGames() {
        return games;
    }
}
