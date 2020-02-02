package com.jmelzer.myttr.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    SAISON_2020("19/20"),
    SAISON_2021("20/21");

    public static List<String> saisons = new ArrayList<>();
    static {
        saisons.add(SAISON_2020.getName());
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

    public static Date getVREndDateForSeason(Saison saison) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);

        switch (saison) {
            case SAISON_2020:
                cal.set(Calendar.YEAR, 2019);
                break;
            case SAISON_2021:
                cal.set(Calendar.YEAR, 2020);
                break;
        }
        return cal.getTime();
    }
}
