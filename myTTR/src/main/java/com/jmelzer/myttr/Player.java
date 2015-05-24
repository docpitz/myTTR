/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player implements Comparable<Player>, Serializable {
    private static final long serialVersionUID = 7149709110710420075L;

    String firstname;
    String lastname;
    String fullName;
    String club;
    String teamName;
    long personId;
    int ttrPoints;

    boolean isChecked;

    int rank = 0;

    List<Event> events = new ArrayList<>(0);

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

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String visualize() {
        return firstname + " " + lastname +
                (club == null ? "" : ("\n" + club)) + "\nTTR: " + ttrPoints;
    }

    public String nameAndClub() {
        return firstname + " " + lastname +
                (club == null ? "" : ("\n" + club));
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
    public String toString() {
        return "Player{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", club='" + club + '\'' +
                ", teamName='" + teamName + '\'' +
                ", personId=" + personId +
                ", ttrPoints=" + ttrPoints +
                ", isChecked=" + isChecked +
                ", rank=" + rank +
                '}';
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
    public int compareTo(Player that) {
        if (this.lastname.compareTo(that.lastname) < 0) {
            return -1;
        } else if (this.lastname.compareTo(that.lastname) > 0) {
            return 1;
        }
        if (this.firstname != null && this.firstname.compareTo(that.firstname) < 0) {
            return -1;
        } else if (this.firstname != null && this.firstname.compareTo(that.firstname) > 0) {
            return 1;
        }


        return 0;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }


    public String getFullName() {
        if (fullName != null) {
            return fullName + " (" + ttrPoints + ")";
        } else {
            return firstname + " " + lastname + " (" + ttrPoints + ")";
        }
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void addEvents(List<Event> events) {
        this.events.addAll(events);
    }
}
