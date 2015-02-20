package com.jmelzer.myttr;

/**
 * Created by J. Melzer on 19.02.2015.
 * Mannschaft in Liga.
 */
public class Mannschaft {
    String name;
    int position;
    int gamesCount;
    int win;
    int tied;
    int lose;
    /** 67:44 */
    String gameStatistic;
    /** +23 */
    String sum;
    String points;
    String url;

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
}
