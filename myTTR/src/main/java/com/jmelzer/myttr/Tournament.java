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

    private List<Competition> ompetitions = new ArrayList<>();
    private String email;

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

    public List<Competition> getOmpetitions() {
        return ompetitions;
    }

    public void setOmpetitions(List<Competition> ompetitions) {
        this.ompetitions = ompetitions;
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
                ", ompetitions=" + ompetitions +
                '}';
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
}
