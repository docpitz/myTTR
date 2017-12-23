package com.jmelzer.myttr;

import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Mannschaft in Liga.
 */
public class Mannschaft {


    public static class SpielerBilanz {
        String pos;
        String name;
        String einsaetze;
        List<String[]> posResults = new ArrayList<>(6);
        String gesamt;

        public SpielerBilanz(String pos, String name, String einsaetze) {
            this.pos = pos;
            this.name = name;
            this.einsaetze = einsaetze;
        }

        public SpielerBilanz(String pos, String name, String einsaetze, List<String[]> posResults, String gesamt) {
            this.pos = pos;
            this.name = name;
            this.einsaetze = einsaetze;
            this.posResults = posResults;
            this.gesamt = gesamt;
        }

        public String getPos() {
            return pos;
        }

        public String getName() {
            return name;
        }

        public String getEinsaetze() {
            return einsaetze;
        }

        public List<String[]> getPosResults() {
            return posResults;
        }

        public String getGesamt() {
            return gesamt;
        }

        @Override
        public String toString() {
            return "SpielerBilanz{" +
                    "pos='" + pos + '\'' +
                    ", name='" + name + '\'' +
                    ", einsaetze='" + einsaetze + '\'' +
                    ", gesamt='" + gesamt + '\'' +
                    '}';
        }
    }

    public void clearLokale() {
        spielLokale.clear();
    }

    String name;
    int position;
    int gamesCount;
    int win;
    int tied;
    int lose;
    /**
     * 67:44
     */
    String gameStatistic;
    /**
     * +23
     */
    String sum;
    String points;
    String url;
    String vereinUrl;

    String kontakt;
    String kontaktNr;
    String kontaktNr2;
    private String mailTo;

    List<String> spielLokale = new ArrayList<>();
    List<SpielerBilanz> spielerBilanzen = new ArrayList<>();

    public Mannschaft(String name, int position, int gamesCount, int win, int tied, int lose, String gameStatistic, String sum, String points, String url) {
        this.name = name;
        this.position = position;
        this.gamesCount = gamesCount;
        this.win = win;
        this.tied = tied;
        this.lose = lose;
        this.gameStatistic = gameStatistic;
        this.sum = sum;
        this.points = points;
        this.url = url;
    }

    public Mannschaft() {

    }

    public Mannschaft(String n) {
        this.name = n;
    }

    public String getKontaktNr() {
        return kontaktNr;
    }

    public void setKontaktNr(String kontaktNr) {
        this.kontaktNr = kontaktNr;
    }

    public String getKontaktNr2() {
        return kontaktNr2;
    }

    public void setKontaktNr2(String kontaktNr2) {
        this.kontaktNr2 = kontaktNr2;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public int getGamesCount() {
        return gamesCount;
    }

    public int getWin() {
        return win;
    }

    public int getTied() {
        return tied;
    }

    public int getLose() {
        return lose;
    }

    public String getGameStatistic() {
        return gameStatistic;
    }

    public String getSum() {
        return sum;
    }

    public String getPoints() {
        return points;
    }

    public String getUrl() {
        return url;
    }

    public String getKontakt() {
        return kontakt;
    }

    public void setKontakt(String kontakt) {
        this.kontakt = kontakt;
    }

    @Override
    public String toString() {
        return "Mannschaft{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", gamesCount=" + gamesCount +
                ", win=" + win +
                ", tied=" + tied +
                ", lose=" + lose +
                ", gameStatistic='" + gameStatistic + '\'' +
                ", sum='" + sum + '\'' +
                ", points='" + points + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVereinUrl() {
        return vereinUrl;
    }

    public void setVereinUrl(String vereinUrl) {
        this.vereinUrl = vereinUrl;
    }

    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(url);
    }

    public List<String> getSpielLokale() {
        return Collections.unmodifiableList(spielLokale);
    }

    public List<SpielerBilanz> getSpielerBilanzen() {
        return Collections.unmodifiableList(spielerBilanzen);
    }

    public void addSpielLokal(String spielLokal) {
        spielLokale.add(spielLokal);
    }

    public void addBilanz(SpielerBilanz b) {
        spielerBilanzen.add(b);
    }

    public void clearBilanzen() {
        spielerBilanzen.clear();
    }

    public void removeAllSpielLokale() {
        spielLokale.clear();
    }
}
