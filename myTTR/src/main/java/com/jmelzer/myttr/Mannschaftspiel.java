package com.jmelzer.myttr;

import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Spielergebnis
 */
public class Mannschaftspiel {
    String date;
    Mannschaft heimMannschaft;
    Mannschaft gastMannschaft;
    String ergebnis;
    String urlDetail;
    String urlSpielLokal;
    int nrSpielLokal;
    boolean genehmigt;
    String baelle;
    String saetze;
    boolean played;
    List<Spielbericht> spiele = new ArrayList<>();

    public Mannschaftspiel() {
        played = false;
    }

    public Mannschaftspiel(String date, Mannschaft heimMannschaft, Mannschaft gastMannschaft, String ergebnis, String urlDetail, boolean genehmigt) {
        this.date = date;
        this.heimMannschaft = heimMannschaft;
        this.gastMannschaft = gastMannschaft;
        this.ergebnis = ergebnis;
        this.urlDetail = urlDetail;
        this.genehmigt = genehmigt;
        played = ergebnis != null;
    }

    public String getUrlSpielLokal() {
        return urlSpielLokal;
    }

    public void setUrlSpielLokal(String urlSpielLokal) {
        this.urlSpielLokal = urlSpielLokal;
    }

    public int getNrSpielLokal() {
        return nrSpielLokal;
    }

    public void setNrSpielLokal(int nrSpielLokal) {
        this.nrSpielLokal = nrSpielLokal;
    }

    public void setErgebnis(String ergebnis) {
        this.ergebnis = ergebnis;
    }

    public void setHeimMannschaft(Mannschaft heimMannschaft) {
        this.heimMannschaft = heimMannschaft;
    }

    public void setGastMannschaft(Mannschaft gastMannschaft) {
        this.gastMannschaft = gastMannschaft;
    }

    public void setSaetze(String saetze) {
        this.saetze = saetze;
    }

    public void setBaelle(String baelle) {
        this.baelle = baelle;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    @Override
    public String toString() {
        return "Mannschaftspiel{" +
                "date='" + date + '\'' +
                ", heimMannschaft=" + heimMannschaft +
                ", gastMannschaft=" + gastMannschaft +
                ", urlDetail='" + urlDetail + '\'' +
                ", genehmigt=" + genehmigt +
                ", ergebnis='" + ergebnis + '\'' +
                ", baelle='" + baelle + '\'' +
                ", saetze='" + saetze + '\'' +
                ", spiele=\n" + printSpielBericht() +
                '}';
    }

    private String printSpielBericht() {
        String result = "";
        for (Spielbericht spielbericht : spiele) {
            result += spielbericht + "\n";
        }
        return result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActualSpellokal() {
        return getHeimMannschaft().getSpielLokal(nrSpielLokal);
    }
    public Mannschaft getHeimMannschaft() {
        return heimMannschaft;
    }

    public Mannschaft getGastMannschaft() {
        return gastMannschaft;
    }

    public String getErgebnis() {
        return ergebnis;
    }

    public String getUrlDetail() {
        return urlDetail;
    }

    public void setUrlDetail(String urlDetail) {
        this.urlDetail = urlDetail;
    }

    public void addSpiel(Spielbericht spielbericht) {
        spiele.add(spielbericht);
    }


    public List<Spielbericht> getSpiele() {
        return spiele;
    }

    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(urlDetail);
    }

    public String oneLine() {
        return date + "  " + heimMannschaft.getName() + " - " + gastMannschaft.getName() + "  " + ergebnis;
    }
}
