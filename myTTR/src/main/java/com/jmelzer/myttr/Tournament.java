package com.jmelzer.myttr;

/**
 * Created by J. Melzer on 15.06.2017.
 */
public class Tournament {
    String name;
    String url;
    private String date;
    private String region;
    private String openFor;
    private String ageClass;
    private String info;
    private String infoUrl;

    public String getDate() {
        return date;
    }

    public String getRegion() {
        return region;
    }

    public String getOpenFor() {
        return openFor;
    }

    public String getAgeClass() {
        return ageClass;
    }

    public String getInfo() {
        return info;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setOpenFor(String openFor) {
        this.openFor = openFor;
    }

    public void setAgeClass(String ageClass) {
        this.ageClass = ageClass;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", region='" + region + '\'' +
                ", openFor='" + openFor + '\'' +
                ", ageClass='" + ageClass + '\'' +
                ", info='" + info + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                '}';
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }
}
