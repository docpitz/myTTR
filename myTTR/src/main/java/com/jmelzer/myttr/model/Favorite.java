package com.jmelzer.myttr.model;

import java.util.Date;

/**
 * Created by J. Melzer on 14.08.2015.
 */
public interface Favorite {
    String getName();

    void setName(String name);

    void setUrl(String url);

    void setChangedAt(Date d);

    String getUrl();
}
