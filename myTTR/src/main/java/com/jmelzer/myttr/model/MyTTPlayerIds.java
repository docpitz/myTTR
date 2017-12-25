package com.jmelzer.myttr.model;

/**
 * Created by J. Melzer on 17.12.2017.
 * With this id the popover can be called
 */
public class MyTTPlayerIds {
    //e.g. personId: 'NU224714'
    String personId;
    //clubNr: '156035'
    String clubNr;

    public MyTTPlayerIds(String personId, String clubNr) {
        this.personId = personId;
        this.clubNr = clubNr;
    }

    public String buildPopupUrl() {
        String url = "https://www.mytischtennis.de/clicktt/WTTV/player/popover?personId=%s&clubNr=%s";
        return String.format(url, personId, clubNr);
    }

    public String getPersonId() {
        return personId;
    }

    public String getClubNr() {
        return clubNr;
    }

    @Override
    public String toString() {
        return "{" +
                "personId='" + personId + '\'' +
                ", clubNr='" + clubNr + '\'' +
                '}';
    }
}
