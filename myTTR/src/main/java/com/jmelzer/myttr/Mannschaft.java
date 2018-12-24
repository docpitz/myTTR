package com.jmelzer.myttr;

import com.jmelzer.myttr.model.LigaPosType;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by J. Melzer on 19.02.2015.
 * Mannschaft in Liga.
 */
public class Mannschaft {


    private String vereinId;

    public String getSpielLokal(int nrSpielLokal) {
        return spielLokale.get(nrSpielLokal);
    }

    public void clearLokale() {
        spielLokale.clear();
    }


    private Liga liga;
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
    LigaPosType ligaPosTyp = LigaPosType.NOTHING;

    Map<Integer, String> spielLokale = new TreeMap<>();
    List<SpielerAndBilanz> spielerBilanzen = new ArrayList<>();
    List<Mannschaftspiel> spiele = new ArrayList<>();

    public Mannschaft(LigaPosType ligaPosType, String name, int position, int gamesCount, int win, int tied, int lose, String gameStatistic, String sum, String points, String url) {
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
        this.ligaPosTyp = ligaPosType;
    }

    public Mannschaft() {

    }

    public Mannschaft(String name) {
        this.name = name;
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

    public LigaPosType getLigaPosTyp() {
        return ligaPosTyp;
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
                "type='" + ligaPosTyp+ '\'' +
                ", name=" + name +
                ", position=" + position +
                ", gamesCount=" + gamesCount +
                ", win=" + win +
                ", tied=" + tied +
                ", lose=" + lose +
                ", gameStatistic='" + gameStatistic + '\'' +
                ", sum='" + sum + '\'' +
                ", points='" + points + '\'' +
                ", url='" + url + '\'' +
                ", vereinUrl='" + vereinUrl + '\'' +
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

        List<String> list = new ArrayList<>();
        for (int i = 0; i < spielLokale.size(); i++) {
            list.add(spielLokale.get(i+1));
        }
        return Collections.unmodifiableList(list);
    }

    public List<SpielerAndBilanz> getSpielerBilanzen() {
        return Collections.unmodifiableList(spielerBilanzen);
    }
    public void addSpielLokale(Map<Integer, String> integerStringMap) {
        spielLokale.putAll(integerStringMap);
    }
    public void addSpielLokal(String spielLokal) {
        spielLokale.put(1, spielLokal);
    }

    public void addBilanz(SpielerAndBilanz b) {
        spielerBilanzen.add(b);
    }

    public void setSpielerBilanzen(List<SpielerAndBilanz> spielerBilanzen) {
        this.spielerBilanzen = spielerBilanzen;
    }

    public void clearBilanzen() {
        spielerBilanzen.clear();
    }

    public void removeAllSpielLokale() {
        spielLokale.clear();
    }

    public List<Mannschaftspiel> getSpiele() {
        return spiele;
    }

    public void setSpiele(List<Mannschaftspiel> spiele) {
        this.spiele = spiele;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Liga getLiga() {
        return liga;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mannschaft that = (Mannschaft) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public void setVereinId(String vereinId) {
        this.vereinId = vereinId;
    }

    public String getVereinId() {
        return vereinId;
    }
}
