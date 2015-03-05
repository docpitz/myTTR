package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Kreis
 */
public class Kreis {
    String name;
    String url;
    Bezirk bezirk;
    List<Liga> ligen = new ArrayList<>();

    public Kreis(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Liga> getLigen() {
        return ligen;
    }

    public void addAllLigen(List<Liga> l) {
        ligen.clear();
        ligen.addAll(l);
        for (Liga liga : l) {
            liga.setVerband(bezirk.getVerband());
        }
    }

    public Bezirk getBezirk() {
        return bezirk;
    }

    public void setBezirk(Bezirk bezirk) {
        this.bezirk = bezirk;
    }
}
