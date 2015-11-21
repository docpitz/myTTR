package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 20.11.2015.
 */
public class MyTTLiga {
    String ligaName;
    List<Player> ranking = new ArrayList<>(50);

    public String getLigaName() {
        return ligaName;
    }

    public void setLigaName(String ligaName) {
        this.ligaName = ligaName;
    }

    public List<Player> getRanking() {
        return ranking;
    }

    public void addPlayer(Player p) {
        ranking.add(p);
    }
}
