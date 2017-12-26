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
        /**
         * e.g. Oberliga Herren West 2  (Vorrunde)
         */
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

        public List<EinzelSpiel> getSpiele() {
            return Collections.unmodifiableList(spiele);
        }

        public String getName() {
            return name;
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

        public String getGegnerMannschaft() {
            return gegnerMannschaft;
        }

        public void setGegnerMannschaft(String gegnerMannschaft) {
            this.gegnerMannschaft = gegnerMannschaft;
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
                    ", clubName='" + ligaName + '\'' +
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

    String name;
    String clubName;
    String clubUrl;
    String position;
    // mytt click tt urls

    String mytTTClickTTUrl;
    Long personId;
    String head2head;

    // end: mytt click tt urls

    List<Einsatz> einsaetze = new ArrayList<>();
    List<Bilanz> bilanzen = new ArrayList<>();
    List<LigaErgebnisse> ergebnisse = new ArrayList<>();

    private boolean isOwnPlayer;

    public void setIsOwnPlayer(boolean isOwnPlayer) {
        this.isOwnPlayer = isOwnPlayer;
    }

    public boolean isOwnPlayer() {
        return isOwnPlayer;
    }

    public String getMytTTClickTTUrl() {
        return mytTTClickTTUrl;
    }

    public void setMytTTClickTTUrl(String mytTTClickTTUrl) {
        this.mytTTClickTTUrl = mytTTClickTTUrl;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getHead2head() {
        return head2head;
    }

    public void setHead2head(String head2head) {
        this.head2head = head2head;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubUrl() {
        return clubUrl;
    }

    public void setClubUrl(String clubUrl) {
        this.clubUrl = clubUrl;
    }

    public Spieler(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
