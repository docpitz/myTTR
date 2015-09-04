package com.jmelzer.myttr;

import com.jmelzer.myttr.model.Favorite;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Represents a Liga like TTBL, 2. Bundesliga etc.
 */
public class Liga implements Favorite {


    public enum Spielplan {
        VR,
        RR,
        GESAMT
    }

    public static List<String> alleKategorien = Arrays.asList("Herren", "Damen", "Jungen", "Mädchen", "Schüler", "B-Schüler",
            "Senioren 40", "Seniorinnen 40", "Senioren 50", "Seniorinnen 50",
            "Senioren 60", "Seniorinnen 60", "Senioren 70", "Senioren");

    /**
     * Herren , Damen Jungen etc.
     */
    String kategorie;
    String name;
    String url;
    List<Mannschaft> mannschaften = new ArrayList<>();
    List<Mannschaftspiel> spieleVorrunde = new ArrayList<>();
    List<Mannschaftspiel> spieleRueckrunde = new ArrayList<>();
    //in manchen Ligen gibt es kein VR & RR
    List<Mannschaftspiel> spieleGesamt = new ArrayList<>();
    private String urlRR;
    private String urlVR;
    private String urlGesamt;
    private Date changedAt;

    public Liga(String name, String url, String kategorie) {
        this.name = name;
        this.url = url;
        this.kategorie = kategorie;
    }

    public Liga() {

    }

    public Liga(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    @Override
    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public String getName() {
        return name;
    }

    public String getNameForFav() {
        return name + " - " + getKategorie();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKategorie() {
        return kategorie;
    }

    public List<Mannschaft> getMannschaften() {
        return mannschaften;
    }

    public void addMannschaft(Mannschaft m) {
        mannschaften.add(m);
    }

    public void clearMannschaften() {
        mannschaften.clear();
    }

    public List<Mannschaftspiel> getSpieleVorrunde() {
        return Collections.unmodifiableList(spieleVorrunde);
    }

    public List<Mannschaftspiel> getSpieleRueckrunde() {
        return Collections.unmodifiableList(spieleRueckrunde);
    }

    public void addSpiel(Mannschaftspiel s, Spielplan spielplan) {
        switch (spielplan) {
            case VR:
                spieleVorrunde.add(s);
                break;
            case RR:
                spieleRueckrunde.add(s);
                break;
            case GESAMT:
                spieleGesamt.add(s);
        }
    }

    public String getUrlGesamt() {
        return urlGesamt;
    }

    public void setUrlGesamt(String urlGesamt) {
        this.urlGesamt = urlGesamt;
    }

    @Override
    public String toString() {
        return "Liga{" +
                "kategorie='" + kategorie + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", spieleVorrunde=" + spieleVorrunde +
                ", spieleRueckrunde=" + spieleRueckrunde +
                ", urlRR='" + urlRR + '\'' +
                ", urlVR='" + urlVR + '\'' +
                ", mannschaften=" + mannschaften +
                '}';
    }

    public void setUrlRR(String urlRR) {
        this.urlRR = urlRR;
    }

    public List<Mannschaftspiel> getSpieleGesamt() {
        return Collections.unmodifiableList(spieleGesamt);
    }

    public String getUrlRR() {
        return urlRR;
    }

    public void setUrlVR(String urlVR) {
        this.urlVR = urlVR;
    }

    public String getUrlVR() {
        return urlVR;
    }

    public List<Mannschaftspiel> getSpieleFor(String mannschaft, Spielplan spielplan) {
        if (spielplan == Spielplan.VR) {
            if (mannschaft != null) {
                return filterSpiele(spieleVorrunde, mannschaft);
            } else {
                return getSpieleVorrunde();
            }
        } else if (spielplan == Spielplan.RR) {
            if (mannschaft != null) {
                return filterSpiele(spieleRueckrunde, mannschaft);
            } else {
                return getSpieleRueckrunde();
            }
        } else {
            if (mannschaft != null) {
                return filterSpiele(spieleGesamt, mannschaft);
            } else {
                return getSpieleGesamt();
            }
        }

    }


    private List<Mannschaftspiel> filterSpiele(List<Mannschaftspiel> spiele, String mannschaft) {
        List<Mannschaftspiel> retList = new ArrayList<>();
        for (Mannschaftspiel mannschaftspiel : spiele) {
            if ((mannschaftspiel.getGastMannschaft() != null &&
                    mannschaft.equals(mannschaftspiel.getGastMannschaft().getName())) ||
                    (mannschaftspiel.getHeimMannschaft() != null &&
                            mannschaft.equals(mannschaftspiel.getHeimMannschaft().getName()))) {
                retList.add(mannschaftspiel);
            }
        }
        return retList;
    }

    public void clearSpiele(Spielplan spielplan) {
        switch (spielplan) {
            case VR:
                spieleVorrunde.clear();
                break;
            case RR:
                spieleRueckrunde.clear();
                break;
            case GESAMT:
                spieleGesamt.clear();
        }
    }

    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(url);
    }

}
