package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Represents a Liga like TTBL, 2. Bundesliga etc.
 */
public class Liga {
    String sex;
    String name;
    String url;

    List<Mannschaft> mannschaften = new ArrayList<>();
    List<Mannschaftspiel> spieleVorrunde = new ArrayList<>();
    List<Mannschaftspiel> spieleRueckrunde = new ArrayList<>();

    public Liga(String name, String url, String sex) {
        this.name = name;
        this.url = url;
        this.sex = sex;
    }

    public Liga() {

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<Mannschaft> getMannschaften() {
        return mannschaften;
    }

    public void addMannschaft(Mannschaft m) {
        mannschaften.add(m);
    }

    public List<Mannschaftspiel> getSpieleVorrunde() {
        return spieleVorrunde;
    }

    public List<Mannschaftspiel> getSpieleRueckrunde() {
        return spieleRueckrunde;
    }

    public void addSpiel(Mannschaftspiel s, boolean vorrunde) {
        if (vorrunde) {
            spieleVorrunde.add(s);
        } else {
            spieleRueckrunde.add(s);
        }
    }

    @Override
    public String toString() {
        return "Liga{" +
                "sex='" + sex + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
