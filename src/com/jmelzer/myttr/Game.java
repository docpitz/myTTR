package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 15.12.2014.
 */
public class Game {
    String player;
    String result;
    List<String> sets = new ArrayList();

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

    public List<String> getSets() {
        return sets;
    }

    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    public void addSet(String result) {
        sets.add(result);
    }

    @Override
    public String toString() {
        return "Game{" +
                "player='" + player + '\'' +
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
}
