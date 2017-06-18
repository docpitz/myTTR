package com.jmelzer.myttr;

/**
 * Created by J. Melzer on 17.06.2017.
 * Turnierkonkurrenz
 */

public class Competition {
    String name;
    String qttr;
    String openFor;
    String date;
    String ttrRelevant;
    //string - or url
    String participants;
    //string - or url
    String results;

    public String getTtrRelevant() {
        return ttrRelevant;
    }

    public void setTtrRelevant(String ttrRelevant) {
        this.ttrRelevant = ttrRelevant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQttr() {
        return qttr;
    }

    public void setQttr(String qttr) {
        this.qttr = qttr;
    }

    public String getOpenFor() {
        return openFor;
    }

    public void setOpenFor(String openFor) {
        this.openFor = openFor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "name='" + name + '\'' +
                ", qttr='" + qttr + '\'' +
                ", openFor='" + openFor + '\'' +
                ", date='" + date + '\'' +
                ", ttrRelevant='" + ttrRelevant + '\'' +
                ", participants='" + participants + '\'' +
                ", results='" + results + '\'' +
                '}';
    }
}
