package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Bezirk, e.g. Mittelrhein
 */
public class Bezirk {
    String name;
    List<Kreis> kreise = new ArrayList<>();

    public Bezirk(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Kreis> getKreise() {
        return kreise;
    }
}
