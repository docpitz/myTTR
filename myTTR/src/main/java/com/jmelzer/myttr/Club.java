/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

import java.io.Serializable;
import java.util.Arrays;

public class Club implements Serializable {

    private static final long serialVersionUID = -400728295651195914L;
    private String name;
    private String id;
    private String verband;
    private String[] searchParts;

    public Club(String name, String id, String verband) {
        this.name = name;
        this.id = id;
        this.verband = verband;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getVerband() {
        return verband;
    }

    public void setSearchParts(String[] searchParts) {
        this.searchParts = searchParts;
    }

    public String[] getSearchParts() {
        return searchParts;
    }

    @Override
    public String toString() {
        return "Club{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", verband='" + verband + '\'' +
                ", searchParts=" + Arrays.toString(searchParts) +
                '}';
    }
}
