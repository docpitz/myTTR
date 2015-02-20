package com.jmelzer.myttr;

import java.util.Date;

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

    public Mannschaftspiel() {
    }

    public Mannschaftspiel(String date, Mannschaft heimMannschaft, Mannschaft gastMannschaft, String ergebnis, String urlDetail, boolean genehmigt) {
        this.date = date;
        this.heimMannschaft = heimMannschaft;
        this.gastMannschaft = gastMannschaft;
        this.ergebnis = ergebnis;
        this.urlDetail = urlDetail;
        this.genehmigt = genehmigt;
    }

    @Override
    public String toString() {
        return "Mannschaftspiel{" +
                "date='" + date + '\'' +
                ", heimMannschaft=" + heimMannschaft +
                ", gastMannschaft=" + gastMannschaft +
                ", ergebnis='" + ergebnis + '\'' +
                ", urlDetail='" + urlDetail + '\'' +
                ", genehmigt=" + genehmigt +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeimMannschaft(Mannschaft heimMannschaft) {
        this.heimMannschaft = heimMannschaft;
    }

    public void setGastMannschaft(Mannschaft gastMannschaft) {
        this.gastMannschaft = gastMannschaft;
    }

    public void setErgebnis(String ergebnis) {
        this.ergebnis = ergebnis;
    }

    public void setUrlDetail(String urlDetail) {
        this.urlDetail = urlDetail;
    }

    public void setGenehmigt(boolean genehmigt) {
        this.genehmigt = genehmigt;
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

    public boolean isGenehmigt() {
        return genehmigt;
    }
}
