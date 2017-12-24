package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Mannschaftspiel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by J. Melzer on 30.05.2015.
 * Represents the club in click tt, maybe connected to mytt
 */
public class Verein implements Favorite {


    public static class SpielLokal {
        public String text;
        public String plz;
        public String city;
        public String street;

        @Override
        public String toString() {
            return "SpielLokal{" +
                    "text='" + text + '\'' +
                    ", plz='" + plz + '\'' +
                    ", city='" + city + '\'' +
                    ", street='" + street + '\'' +
                    '}';
        }

        public String formatted() {
            if (street == null)
                return "-------";
            return street + "\n" + plz + " " + city;
        }
    }

    static public class Mannschaft {
        //e.g. TTG St. AUgustin II
        public String name;
        //e.g. Bezirksliga 3
        public String liga;
        //link to the url of the liga
        public String url;

        @Override
        public String toString() {
            return "Mannschaft{" +
                    "name='" + name + '\'' +
                    ", liga='" + liga + '\'' +
                    '}';
        }
    }

    public Verein() {
    }

    public Verein(String name, String url) {
        this.name = name;
        this.url = url;
    }

    private String name;
    private long nr;
    private int gruendungsjahr;
    private List<SpielLokal> lokale = new ArrayList<>();
    private List<String> lokaleUnformatted = new ArrayList<>();
    private List<Mannschaftspiel> letzteSpiele = new ArrayList<>();
    private List<Mannschaftspiel> naechsteSpiele = new ArrayList<>();
    private List<Mannschaft> mannschaften = new ArrayList<>();
    private Kontakt kontakt;
    private Date changedAt;
    private String urlMannschaften;
    private String url;
    private List<Mannschaftspiel> spielplan = new ArrayList<>();

    public void addSpielPlanSpiel(Mannschaftspiel m) {
        spielplan.add(m);
    }

    public List<Mannschaftspiel> getSpielplan() {
        return Collections.unmodifiableList(spielplan);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getNameForFav() {
        return name;
    }

    public void addSpielLokale(List<String> strings) {
        lokaleUnformatted.addAll(strings);
    }

    public List<String> getLokaleUnformatted() {
        return Collections.unmodifiableList(lokaleUnformatted);
    }

    public Date getChangedAt() {
        return changedAt;
    }

    @Override
    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public void removeAllSpielLokale() {
        lokale.clear();
    }

    public void addLetztesSpiel(Mannschaftspiel mannschaftspiel) {
        letzteSpiele.add(mannschaftspiel);
    }

    public void addSpielLokal(SpielLokal lokal) {
        lokale.add(lokal);
    }

    public void addMannschaft(Mannschaft m) {
        mannschaften.add(m);
    }

    public void removeAllMannschaften() {
        mannschaften.clear();
    }

    static public class Kontakt {
        String nameAddress;
        String mail;
        String url;

        public Kontakt(String nameAddress, String mail, String url) {
            this.nameAddress = nameAddress;
            this.mail = mail;
            this.url = url;
        }

        public String getNameAddress() {
            return nameAddress;
        }

        public String getMail() {
            return mail;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Kontakt{" +
                    "nameAddress='" + nameAddress + '\'' +
                    ", mail='" + mail + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public Kontakt getKontakt() {
        return kontakt;
    }

    public void setKontakt(Kontakt kontakt) {
        this.kontakt = kontakt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNr() {
        return nr;
    }

    public void setNr(long nr) {
        this.nr = nr;
    }

    public int getGruendungsjahr() {
        return gruendungsjahr;
    }

    public void setGruendungsjahr(int gruendungsjahr) {
        this.gruendungsjahr = gruendungsjahr;
    }

    public List<SpielLokal> getLokale() {
        return Collections.unmodifiableList(lokale);
    }

    public List<Mannschaftspiel> getLetzteSpiele() {
        return letzteSpiele;
    }

    public List<Mannschaft> getMannschaften() {
        return Collections.unmodifiableList(mannschaften);
    }

    public void setLetzteSpiele(List<Mannschaftspiel> letzteSpiele) {
        this.letzteSpiele = letzteSpiele;
    }

    public List<Mannschaftspiel> getNaechsteSpiele() {
        return naechsteSpiele;
    }

    public void setNaechsteSpiele(List<Mannschaftspiel> naechsteSpiele) {
        this.naechsteSpiele = naechsteSpiele;
    }

    public void setUrlMannschaften(String urlMannschaften) {
        this.urlMannschaften = urlMannschaften;
    }

    public String getUrlMannschaften() {
        return urlMannschaften;
    }
}
