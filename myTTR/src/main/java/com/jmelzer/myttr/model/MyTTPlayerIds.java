package com.jmelzer.myttr.model;

/**
 * Created by cicgfp on 17.12.2017.
 */

public class MyTTPlayerIds {
    String personId;
    String clubNr;

    public MyTTPlayerIds(String personId, String clubNr) {
        this.personId = personId;
        this.clubNr = clubNr;
    }

    public String buildPopupUrl() {
        String url = "https://www.mytischtennis.de/clicktt/WTTV/player/popover?personId=%s&clubNr=%s";
        return String.format(url, personId, clubNr);
    }

    @Override
    public String toString() {
        return "{" +
                "personId='" + personId + '\'' +
                ", clubNr='" + clubNr + '\'' +
                '}';
    }
}
