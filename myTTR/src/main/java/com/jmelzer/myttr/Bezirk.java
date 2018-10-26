package com.jmelzer.myttr;

import com.jmelzer.myttr.util.UrlUtil;

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

    public Bezirk(String name) {
        this.name = name;
    }

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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(url);
    }
    public void setKreise(List<Kreis> kreise) {
        this.kreise = kreise;
        for (Kreis kreis : kreise) {
            kreis.setBezirk(this);
            kreis.setUrl(UrlUtil.safeUrl(getHttpAndDomain() , kreis.getUrl()));
        }
    }

    public List<Liga> getLigen() {
        return ligen;
    }

    public void addAllLigen(List<Liga> l) {
        ligen.clear();
        ligen.addAll(l);
        for (Liga liga : ligen) {
            liga.setUrl(UrlUtil.safeUrl(getHttpAndDomain() , liga.getUrl()));
        }
    }

    public List<Kreis> getKreise() {
        return kreise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bezirk bezirk = (Bezirk) o;

        return name.equals(bezirk.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Bezirk{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", kreise=" + kreise +
                ", ligen=" + ligen +
                '}';
    }
}
