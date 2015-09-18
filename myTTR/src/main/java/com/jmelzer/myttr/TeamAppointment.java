package com.jmelzer.myttr;

/**
 */
public class TeamAppointment {
    String team1;
    String team2;

    Boolean playAway;
    boolean foundTeam;

    String id1;
    String id2;

    String date;

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team) {
        this.team1 = team;
    }

    public boolean isFoundTeam() {
        return foundTeam;
    }

    public void setFoundTeam(boolean foundTeam) {
        this.foundTeam = foundTeam;
    }

    public Boolean isPlayAway() {
        return playAway;
    }

    public void setPlayAway(Boolean playAway) {
        this.playAway = playAway;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id) {
        this.id1 = id;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    @Override
    public String toString() {
        return "TeamAppointment{" +
                "team='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", playAway=" + playAway +
                ", foundTeam=" + foundTeam +
                ", id1='" + id1 + '\'' +
                ", id2='" + id2 + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
