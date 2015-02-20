package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * e.g. wttv
 */
public class Verband {
    String name;
    String url;

    List<Bezirk> bezirks = new ArrayList<>();

    public Verband(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void addDistrict(Bezirk d) {
        bezirks.add(d);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<Bezirk> getBezirks() {
        return bezirks;
    }
}
