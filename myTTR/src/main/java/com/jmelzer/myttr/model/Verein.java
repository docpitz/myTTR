package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Mannschaftspiel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J. Melzer on 30.05.2015.
 * Represents the club in click tt, maybe connected to mytt
 */
public class Verein {
    String name;
    long nr;
    int gruendungsjahr;
    List<String> lokale = new ArrayList<>();
    List<Mannschaftspiel> letzteSpiele = new ArrayList<>();
    List<Mannschaftspiel> naechsteSpiele = new ArrayList<>();
    Kontakt kontakt;

    public void removeAllSpielLokale() {
        lokale.clear();
    }

    public void addSpielLokal(String lokal) {
        lokale.add(lokal);
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

    public List<String> getLokale() {
        return Collections.unmodifiableList(lokale);
    }

    public void setLokale(List<String> lokale) {
        this.lokale = lokale;
    }

    public List<Mannschaftspiel> getLetzteSpiele() {
        return letzteSpiele;
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
}
