/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

public class Club {

    private String name;
    private String id;
    private String verband;
    private String webName;

    public Club(String name, String id, String verband, String webName) {
        this.name = name;
        this.id = id;
        this.verband = verband;
        this.webName = webName;
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

    public String getWebName() {
        return webName;
    }
}
