package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 15.12.2014.
 * Used only in mytt.de
 */
public class Game {
    String player;
    String playerWithPoints;
    long playerId;
    String result;
    List<String> sets = new ArrayList<>();

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPlayerWithPoints() {
        return playerWithPoints;
    }

    public void setPlayerWithPoints(String playerWithPoints) {
        this.playerWithPoints = playerWithPoints;
    }

    public void addSet(String result) {
        if (result != null && !result.isEmpty()) {
            sets.add(result);
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "player='" + player + '\'' +
                ", playerId=" + playerId +
                ", result='" + result + '\'' +
                ", sets=" + sets +
                '}';
    }

    public String getSetsInARow() {
        String ret = "";
        for (String set : sets) {
            ret += set + "    ";
        }

        return ret;
    }

    public List<String> getSets() {
        return sets;
    }
}
