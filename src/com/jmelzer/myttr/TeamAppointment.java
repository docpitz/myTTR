package com.jmelzer.myttr;

/**
 */
public class TeamAppointment {
    String team;

    boolean playAway;

    String id;

    String date;

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public boolean isPlayAway() {
        return playAway;
    }

    public void setPlayAway(boolean playAway) {
        this.playAway = playAway;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TeamAppointment{");
        sb.append("team='").append(team).append('\'');
        sb.append(", playAway=").append(playAway);
        sb.append(", id='").append(id).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
