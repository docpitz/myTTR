/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr;

public class Verein {

    private String name;
    private String id;
    private String verband;
    private String webName;

    public Verein(String name, String id, String verband, String webName) {
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
