package com.jmelzer.myttr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 12.05.2015.
 * for every year the app can read from, means shifting the old years
 */
public enum Saison {
    SAISON_2014("13/14"),
    SAISON_2015("14/15"),
    SAISON_2016("15/16"),
    SAISON_2017("16/17"),
    SAISON_2018("17/18"),
    SAISON_2019("18/19"),
    SAISON_2020("19/20");

    public static List<String> saisons = new ArrayList<>();
    static {
        saisons.add(SAISON_2019.getName());
        saisons.add(SAISON_2018.getName());
        saisons.add(SAISON_2017.getName());
        saisons.add(SAISON_2016.getName());
        saisons.add(SAISON_2015.getName());
        saisons.add(SAISON_2014.getName());
    }
    String name;
    Saison(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }


    public static Saison parse(String item) {
        for (final Saison type : Saison.values()) {
            if (type.getName().equals(item)) {
                return type;
            }
        }
        throw new IllegalArgumentException(item + " isn't valid");
    }
}
