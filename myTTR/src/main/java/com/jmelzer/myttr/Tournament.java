package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 15.06.2017.
 */
public class Tournament {
    private String name;
    private String url;
    private String date;
    private String region;
    private String openFor;
    private String ageClass;
    private String info;
    private String infoUrl;
    //from detail page
    private String location;
    private String contact;
    private String material;
    private String registrationInfo;

    private List<Competition> competitions = new ArrayList<>();
    private String email;
    private String ranglistenbezug;
    private String longDate;
    private String turnierArt;
    private String priceMoney;
    private String antragShortUrl;
    private String antragFullUrl;
    private String turnierhomepage;
    private String fullName;
    private String freePlaces;
    private boolean cup;

    public String getRanglistenbezug() {
        return ranglistenbezug;
    }

    public void setRanglistenbezug(String ranglistenbezug) {
        this.ranglistenbezug = ranglistenbezug;
    }

    public String getLongDate() {
        return longDate;
    }

    public void setLongDate(String longDate) {
        this.longDate = longDate;
    }

    public String getTurnierArt() {
        return turnierArt;
    }

    public void setTurnierArt(String turnierArt) {
        this.turnierArt = turnierArt;
    }

    public String getPriceMoney() {
        return priceMoney;
    }

    public void setPriceMoney(String priceMoney) {
        this.priceMoney = priceMoney;
    }

    public String getAntragShortUrl() {
        return antragShortUrl;
    }

    public void setAntragShortUrl(String antragShortUrl) {
        this.antragShortUrl = antragShortUrl;
    }

    public String getAntragFullUrl() {
        return antragFullUrl;
    }

    public void setAntragFullUrl(String antragFullUrl) {
        this.antragFullUrl = antragFullUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getRegistrationInfo() {
        return registrationInfo;
    }

    public void setRegistrationInfo(String registrationInfo) {
        this.registrationInfo = registrationInfo;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

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

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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
                ", location='" + location + '\'' +
                ", contact='" + contact + '\'' +
                ", material='" + material + '\'' +
                ", registrationInfo='" + registrationInfo + '\'' +
                ", ompetitions=" + competitions +
                ", email='" + email + '\'' +
                ", ranglistenbezug='" + ranglistenbezug + '\'' +
                ", longDate='" + longDate + '\'' +
                ", turnierArt='" + turnierArt + '\'' +
                ", priceMoney='" + priceMoney + '\'' +
                ", antragShortUrl='" + antragShortUrl + '\'' +
                ", antragFullUrl='" + antragFullUrl + '\'' +
                ", turnierhomepage='" + turnierhomepage + '\'' +
                '}';
    }

    public void setTurnierhomepage(String turnierhomepage) {
        this.turnierhomepage = turnierhomepage;
    }

    public String getTurnierhomepage() {
        return turnierhomepage;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void clearCompetitions() {
        competitions.clear();
    }

    public void addCompetition(Competition competition) {
        competitions.add(competition);
    }


    public void setFreePlaces(String freePlaces) {
        this.freePlaces = freePlaces;
    }

    public String getFreePlaces() {
        return freePlaces;
    }

    public boolean isCup() {
        return cup;
    }

    public void setCup(boolean b) {
        cup = b;
    }
}
