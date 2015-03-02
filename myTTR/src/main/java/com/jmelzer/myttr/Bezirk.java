package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Bezirk, e.g. Mittelrhein
 */
public class Bezirk {
    String name;
    String url;
    List<Kreis> kreise = new ArrayList<>();
    List<Liga> ligen = new ArrayList<>();
    private Verband verband;

    public Bezirk(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setKreise(List<Kreis> kreise) {
        this.kreise = kreise;
        for (Kreis kreis : kreise) {
            kreis.setBezirk(this);
        }
    }

    public List<Liga> getLigen() {
        return ligen;
    }

    public void setLigen(List<Liga> ligen) {
        this.ligen = ligen;
    }

    public List<Kreis> getKreise() {
        return kreise;
    }

    public void setVerband(Verband verband) {
        this.verband = verband;
    }

    public Verband getVerband() {
        return verband;
    }
}
