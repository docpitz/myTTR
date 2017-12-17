package com.jmelzer.myttr;

import com.jmelzer.myttr.model.MyTTPlayerIds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 20.02.2015.
 * Einzel oder Doppelspiel
 */
public class Spielbericht {
    /** e.g. D1-D2 or 1-2 */
    String name;

    String spieler1Name;
    String spieler1Url;
    String spieler2Name;
    String spieler2Url;

    List<String> sets = new ArrayList<>();

    String result;

    MyTTPlayerIds myTTPlayerIdsForPlayer1;
    MyTTPlayerIds myTTPlayerIdsForPlayer2;

    public Spielbericht() {

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

    public String getSpieler1Url() {
        return spieler1Url;
    }

    public MyTTPlayerIds getMyTTPlayerIdsForPlayer1() {
        return myTTPlayerIdsForPlayer1;
    }

    public void setMyTTPlayerIdsForPlayer1(MyTTPlayerIds myTTPlayerIdsForPlayer1) {
        this.myTTPlayerIdsForPlayer1 = myTTPlayerIdsForPlayer1;
    }

    public MyTTPlayerIds getMyTTPlayerIdsForPlayer2() {
        return myTTPlayerIdsForPlayer2;
    }

    public void setMyTTPlayerIdsForPlayer2(MyTTPlayerIds myTTPlayerIdsForPlayer2) {
        this.myTTPlayerIdsForPlayer2 = myTTPlayerIdsForPlayer2;
    }

    public void setSpieler1Url(String spieler1Url) {
        this.spieler1Url = spieler1Url;
    }

    public String getSpieler2Name() {
        return spieler2Name;
    }

    public void setSpieler2Name(String spieler2Name) {
        this.spieler2Name = spieler2Name;
    }

    public String getSpieler2Url() {
        return spieler2Url;
    }

    public void setSpieler2Url(String spieler2Url) {
        this.spieler2Url = spieler2Url;
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
        return "Spiel{" +
                "name='" + name + '\'' +
                ", spieler1Name='" + spieler1Name + '\'' +
                ", spieler1Url='" + spieler1Url + '\'' +
                ", myTTPlayerIdsForPlayer1='" + myTTPlayerIdsForPlayer1 + '\'' +
                ", spieler2Name='" + spieler2Name + '\'' +
                ", spieler2Url='" + spieler2Url + '\'' +
                ", myTTPlayerIdsForPlayer1='" + myTTPlayerIdsForPlayer1 + '\'' +
                ", sets=" + sets +
                ", result='" + result + '\'' +
                '}';
    }

}
