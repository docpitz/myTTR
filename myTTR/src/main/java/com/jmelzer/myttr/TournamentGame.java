package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 25.06.2017
 * Einzel oder Doppelspiel im Turnier
 */
public class TournamentGame {
    /** e.g. D1-D2 or 1-2 */
    String name;

    String spieler1Name;
    String spieler2Name;

    List<String> sets = new ArrayList<>();

    String result;

    public TournamentGame() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpieler1Name() {
        return spieler1Name;
    }

    public void setSpieler1Name(String spieler1Name) {
        this.spieler1Name = spieler1Name;
    }


    public String getSpieler2Name() {
        return spieler2Name;
    }

    public void setSpieler2Name(String spieler2Name) {
        this.spieler2Name = spieler2Name;
    }

    public List<String> getSets() {
        return sets;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void addSet(String set) {
        if (set != null && !set.isEmpty()) {
            sets.add(set);
        }
    }

    @Override
    public String toString() {
        return "TournamentGame{" +
                "name='" + name + '\'' +
                ", spieler1Name='" + spieler1Name + '\'' +
                ", spieler2Name='" + spieler2Name + '\'' +
                ", sets=" + sets +
                ", result='" + result + '\'' +
                '}';
    }
}
