package com.jmelzer.myttr;

/**
 * Created by J. Melzer on 18.06.2017.
 */

public class Participant {
    private String name;
    private String club;
    private String qttr;
    private String place;
    private String bilanz;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getQttr() {
        return qttr;
    }

    public void setQttr(String qttr) {
        this.qttr = qttr;
    }

    @Override
    public String toString() {
        return "Participant{" +
                " place='" + place + '\'' +
                ", bilanz='" + bilanz + '\'' +
                ", name='" + name + '\'' +
                ", club='" + club + '\'' +
                ", qttr='" + qttr + '\'' +
                '}';
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setBilanz(String bilanz) {
        this.bilanz = bilanz;
    }

    public String getBilanz() {
        return bilanz;
    }
}
