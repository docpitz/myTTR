package com.jmelzer.myttr;

/**
 * Store info about an event.
 * User: jmelzer
 * Date: 12.12.14
 * Time: 13:31
 */
public class Event {
    String date;
    /** name of the event*/
    String event;
    String ak = "16";
//    String playCount;
//    short won;
    String bilanz;
    int ttr;
    long eventId;
    short sum = -1000;

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

    public String getBilanz() {
        return bilanz;
    }

    public void setBilanz(String bilanz) {
        this.bilanz = bilanz;
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
        return "Event{" +
                "date='" + date + '\'' +
                ", event='" + event + '\'' +
                ", ak='" + ak + '\'' +
                ", bilanz='" + bilanz + '\'' +
                ", ttr=" + ttr +
                ", eventId=" + eventId +
                ", sum=" + sum +
                '}';
    }
}
