package com.jmelzer.myttr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class EventDetail implements Serializable {
    private static final long serialVersionUID = -2717676226478106756L;
    List<Game> games = new ArrayList<>();

    public List<Game> getGames() {
        return games;
    }

    @Override
    public String toString() {
        return "EventDetail{" +
                "games=" + games.toString() +
                '}';
    }
}
