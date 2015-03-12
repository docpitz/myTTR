package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J. Melzer on 11.03.2015.
 * Represents a mannschafts player in click tt
 */
public class Spieler {
    public static class LigaErgebnisse {
        /** e.g. Oberliga Herren West 2  (Vorrunde) */
        String name;
        List<EinzelSpiel> spiele = new ArrayList<>();

        public LigaErgebnisse(String name) {
            this.name = name;
        }
        public void addSpiel(EinzelSpiel spiel) {
            spiele.add(spiel);
        }
        @Override
        public String toString() {
            return "LigaErgebnisse{" +
                    "name='" + name + '\'' +
                    ", spiele=" + spiele +
                    '}';
        }
    }
    public static class EinzelSpiel {
        String datum;
        String pos;
        String gegner;
        String ergebnis;
        String saetze;
        String gegnerMannschaft;

        public EinzelSpiel(String datum, String pos, String gegner, String ergebnis, String saetze, String gegnerMannschaft) {
            this.datum = datum;
            this.pos = pos;
            this.gegner = gegner;
            this.ergebnis = ergebnis;
            this.saetze = saetze;
            this.gegnerMannschaft = gegnerMannschaft;
        }

        public void setDatum(String datum) {
            this.datum = datum;
        }

        public String getDatum() {
            return datum;
        }

        public String getPos() {
            return pos;
        }

        public String getGegner() {
            return gegner;
        }

        @Override
        public String toString() {
            return "EinzelSpiel{" +
                    "datum='" + datum + '\'' +
                    ", pos='" + pos + '\'' +
                    ", gegner='" + gegner + '\'' +
                    ", ergebnis='" + ergebnis + '\'' +
                    ", saetze='" + saetze + '\'' +
                    ", gegnerMannschaft='" + gegnerMannschaft + '\'' +
                    '}';
        }

        public String getErgebnis() {
            return ergebnis;
        }

        public String getSaetze() {
            return saetze;
        }

    }
    public static class Bilanz {
        String kategorie;
        String ergebnis;

        public Bilanz(String kategorie, String ergebnis) {
            this.kategorie = kategorie;
            this.ergebnis = ergebnis;
        }

        public String getKategorie() {
            return kategorie;
        }

        public String getErgebnis() {
            return ergebnis;
        }

        @Override
        public String toString() {
            return "Bilanz{" +
                    "kategorie='" + kategorie + '\'' +
                    ", ergebnis='" + ergebnis + '\'' +
                    '}';
        }
    }

    public static class Einsatz {
        String kategorie;
        String ligaName;
        String url;

        Einsatz(String kategorie, String ligaName, String url) {
            this.kategorie = kategorie;
            this.ligaName = ligaName;
            this.url = url;
        }

        @Override
        public String toString() {
            return "Einsatz{" +
                    "kategorie='" + kategorie + '\'' +
                    ", ligaName='" + ligaName + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }

        public String getKategorie() {
            return kategorie;
        }

        public String getLigaName() {
            return ligaName;
        }

        public String getUrl() {
            return url;
        }
    }
    String meldung;
    List<Einsatz> einsaetze = new ArrayList<>();
    List<Bilanz> bilanzen = new ArrayList<>();
    List<LigaErgebnisse> ergebnisse = new ArrayList<>();

    public String getMeldung() {
        return meldung;
    }

    public void setMeldung(String meldung) {
        this.meldung = meldung;
    }

    public List<Einsatz> getEinsaetze() {
        return Collections.unmodifiableList(einsaetze);
    }

    public void addEinsatz(String kategorie, String ligaName, String url) {
        einsaetze.add(new Einsatz(kategorie, ligaName, url));
    }

    public List<Bilanz> getBilanzen() {
        return Collections.unmodifiableList(bilanzen);
    }

    public void addBilanz(String kategorie, String ergebnis) {
        bilanzen.add(new Bilanz(kategorie, ergebnis));
    }

    public List<LigaErgebnisse> getErgebnisse() {
        return Collections.unmodifiableList(ergebnisse);
    }

    public void addLigaErgebnisse(LigaErgebnisse ligaErgebnisse) {
        ergebnisse.add(ligaErgebnisse);
    }
}
