package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 25.06.2017.
 */

public class KoPhase {
    String name;

    List<TournamentGame> games = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TournamentGame> getGames() {
        return games;
    }

    public void addGame(TournamentGame game) {
        games.add(game);
    }

    @Override
    public String toString() {
        return "KoPhase{" +
                "name='" + name + '\'' +
                ", games=" + games +
                '}';
    }
}
