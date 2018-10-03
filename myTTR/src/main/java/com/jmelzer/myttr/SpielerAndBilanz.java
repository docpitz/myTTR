package com.jmelzer.myttr;

import com.jmelzer.myttr.model.MyTTPlayerIds;

import java.util.ArrayList;
import java.util.List;

public class SpielerAndBilanz {
    String pos;
    String name;
    String einsaetze;
    //first entry against, seconfd bilanz
    List<String[]> posResults = new ArrayList<>(6);
    String gesamt;
    MyTTPlayerIds ids;

    public SpielerAndBilanz(String pos, String name, String einsaetze) {
        this.pos = pos;
        this.name = name;
        this.einsaetze = einsaetze;
    }

    public SpielerAndBilanz(String pos, String name, String einsaetze, List<String[]> posResults, String gesamt, MyTTPlayerIds ids) {
        this.pos = pos;
        this.name = name;
        this.einsaetze = einsaetze;
        this.posResults = posResults;
        this.gesamt = gesamt;
        this.ids = ids;
    }

    public SpielerAndBilanz(String pos, String name, String einsaetze, List<String[]> posResults, String gesamt) {
        this.pos = pos;
        this.name = name;
        this.einsaetze = einsaetze;
        this.posResults = posResults;
        this.gesamt = gesamt;
    }

    public MyTTPlayerIds getIds() {
        return ids;
    }

    public String getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public String getEinsaetze() {
        return einsaetze;
    }

    public List<String[]> getPosResults() {
        return posResults;
    }

    public String getGesamt() {
        return gesamt;
    }

    @Override
    public String toString() {
        return "SpielerBilanz{" +
                "pos='" + pos + '\'' +
                ", name='" + name + '\'' +
                ", einsaetze='" + einsaetze + '\'' +
                ", gesamt='" + gesamt + '\'' +
                ", ids='" + ids + '\'' +
                '}';
    }
}
