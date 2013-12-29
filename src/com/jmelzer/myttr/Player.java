/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 29.12.13 
*
*/


package com.jmelzer.myttr;

public class Player {
    String firstname;
    String lastname;
    String club;
    int ttrPoints;

    public Player(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Player(String firstname, String lastname, String club, int ttrPoints) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.club = club;
        this.ttrPoints = ttrPoints;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public int getTtrPoints() {
        return ttrPoints;
    }

    public void setTtrPoints(int ttrPoints) {
        this.ttrPoints = ttrPoints;
    }

    public String visualize() {
        return firstname + " " + lastname +
               (club ==null ? "" : ("\n" + club)) + "\nTTR: " + ttrPoints;
    }
}
