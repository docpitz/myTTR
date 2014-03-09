/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

public class Player {
    String firstname;
    String lastname;
    String club;
    int ttrPoints;
    boolean isChecked;

    public Player() {
    }

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String visualize() {
        return firstname + " " + lastname +
               (club ==null ? "" : ("\n" + club)) + "\nTTR: " + ttrPoints;
    }
    public String nameAndClub() {
        return firstname + " " + lastname +
               (club ==null ? "" : ("\n" + club));
    }

    public void copy(Player p) {
        if (p == null) {
            return;
        }
        lastname = p.getLastname();
        firstname = p.getFirstname();
        ttrPoints = p.getTtrPoints();
        club = p.getClub();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Player player = (Player) o;

        if (club != null ? !club.equals(player.club) : player.club != null) {
            return false;
        }
        if (firstname != null ? !firstname.equals(player.firstname) : player.firstname != null) {
            return false;
        }
        if (lastname != null ? !lastname.equals(player.lastname) : player.lastname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstname != null ? firstname.hashCode() : 0;
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (club != null ? club.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Player{");
        sb.append("firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", club='").append(club).append('\'');
        sb.append(", ttrPoints=").append(ttrPoints);
        sb.append(", isChecked=").append(isChecked);
        sb.append('}');
        return sb.toString();
    }
}
