package com.jmelzer.myttr.model;

import java.io.Serializable;

/**
 * Created by J. Melzer on 28.09.2017.
 * Object for storing infos about player to search fpr mytt
 */

public class SearchPlayer implements Serializable {
    private static final long serialVersionUID = 3253568223402780761L;
    String lastname;
    String firstname;
    String clubname;
    String gender;
    int yearFrom = -1;
    int yearTo = -1;
    int ttrFrom = -1;
    int ttrTo = -1;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public int getTtrFrom() {
        return ttrFrom;
    }

    public void setTtrFrom(int ttrFrom) {
        this.ttrFrom = ttrFrom;
    }

    public int getTtrTo() {
        return ttrTo;
    }

    public void setTtrTo(int ttrTo) {
        this.ttrTo = ttrTo;
    }
}
