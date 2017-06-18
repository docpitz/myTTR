package com.jmelzer.myttr;

/**
 * Created by J. Melzer on 18.06.2017.
 */

public class Participant {
    private String name;
    private String club;
    private String qttr;

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
                "name='" + name + '\'' +
                ", club='" + club + '\'' +
                ", qttr='" + qttr + '\'' +
                '}';
    }
}
