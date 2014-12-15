package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class EventDetail {
    List<Game> games = new ArrayList();

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
