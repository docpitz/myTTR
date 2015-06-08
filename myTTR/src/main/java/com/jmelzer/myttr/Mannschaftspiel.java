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
    boolean genehmigt;
    String baelle;
    String saetze;
    List<Spielbericht> spiele = new ArrayList<>();

    public Mannschaftspiel(String date, Mannschaft heimMannschaft, Mannschaft gastMannschaft, String ergebnis, String urlDetail, boolean genehmigt) {
        this.date = date;
        this.heimMannschaft = heimMannschaft;
        this.gastMannschaft = gastMannschaft;
        this.ergebnis = ergebnis;
        this.urlDetail = urlDetail;
        this.genehmigt = genehmigt;
    }

    public void setSaetze(String saetze) {
        this.saetze = saetze;
    }

    public void setBaelle(String baelle) {
        this.baelle = baelle;
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
                ", spiele=" + spiele +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
