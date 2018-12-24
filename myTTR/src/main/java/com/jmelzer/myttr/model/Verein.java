package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Mannschaftspiel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by J. Melzer on 30.05.2015.
 * Represents the club in click tt, maybe connected to mytt
 */
public class Verein implements Favorite {


    public static class SpielLokal {
        public Integer nr;
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
    private Map<Integer, SpielLokal> lokale = new TreeMap<>();
    private Map<Integer, String> lokaleUnformatted = new TreeMap<>();
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

    @Override
    public String typeForMenu() {
        return "Verein";
    }

    public String getNameForFav() {
        return name;
    }

    public void addSpielLokale(Map<Integer, String> integerStringMap) {
        lokaleUnformatted.putAll(integerStringMap);
    }

    public void addSpielLokal(int nr, String lokal) {
        lokaleUnformatted.put(nr, lokal);
    }

    public List<String> getLokaleUnformatted() {
        List<String> list = new ArrayList<>();
        for (String s : lokaleUnformatted.values()) {
            if (!s.isEmpty())
                list.add(lokaleUnformatted.get(1));

        }
        return Collections.unmodifiableList(list);
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
        lokale.put(lokal.nr, lokal);
    }

    public void addMannschaft(Mannschaft m) {
        mannschaften.add(m);
    }

    public void removeAllMannschaften() {
        mannschaften.clear();
    }

    @Override
    public String toString() {
        return "Verein{" +
                "name='" + name + '\'' +
                ", nr=" + nr +
                ", gruendungsjahr=" + gruendungsjahr +
                ", lokale=" + lokale +
                ", lokaleUnformatted=" + lokaleUnformatted +
                ", letzteSpiele=" + letzteSpiele +
                ", naechsteSpiele=" + naechsteSpiele +
                ", mannschaften=" + mannschaften +
                ", kontakt=" + kontakt +
                ", changedAt=" + changedAt +
                ", urlMannschaften='" + urlMannschaften + '\'' +
                ", url='" + url + '\'' +
                ", spielplan=" + spielplan +
                '}';
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

    public SpielLokal getLokal(int nr) {
        return lokale.get(nr);
    }

    public List<SpielLokal> getLokale() {
        List<SpielLokal> list = new ArrayList<>();
        for (int i = 1; i < lokale.size(); i++) {
            list.add(lokale.get(i));
        }
        return Collections.unmodifiableList(list);
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
