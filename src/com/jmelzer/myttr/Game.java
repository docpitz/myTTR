package com.jmelzer.myttr;

/**
 * TODO
 * User: jmelzer
 * Date: 12.12.14
 * Time: 13:31
 */
public class Game {
    String date;
    String event;
    String ak = "16";
    short playCount;
    short won;
    int ttr;
    long eventId;
    short sum;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public short getPlayCount() {
        return playCount;
    }

    public void setPlayCount(short playCount) {
        this.playCount = playCount;
    }

    public short getWon() {
        return won;
    }

    public void setWon(short won) {
        this.won = won;
    }

    public int getTtr() {
        return ttr;
    }
    public String getTtrAsString() {
        return "" + ttr;
    }

    public void setTtr(int ttr) {
        this.ttr = ttr;
    }

    public short getSum() {
        return sum;
    }

    public void setSum(short sum) {
        this.sum = sum;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Game{");
        sb.append("date='").append(date).append('\'');
        sb.append(", event='").append(event).append('\'');
        sb.append(", ak='").append(ak).append('\'');
        sb.append(", playCount=").append(playCount);
        sb.append(", won=").append(won);
        sb.append(", ttr=").append(ttr);
        sb.append(", eventId=").append(eventId);
        sb.append(", sum=").append(sum);
        sb.append('}');
        return sb.toString();
    }
}
