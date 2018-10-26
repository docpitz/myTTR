package com.jmelzer.myttr;

import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Kreis(String name) {
        this.name = name;
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
            liga.setUrl(UrlUtil.safeUrl(getHttpAndDomain() , liga.getUrl()));
        }
    }
    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(url);
    }
    public Bezirk getBezirk() {
        return bezirk;
    }

    public void setBezirk(Bezirk bezirk) {
        this.bezirk = bezirk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kreis kreis = (Kreis) o;
        return Objects.equals(name, kreis.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
