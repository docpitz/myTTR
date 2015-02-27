package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * Represents a Liga like TTBL, 2. Bundesliga etc.
 */
public class Liga {
    String sex;
    String name;
    String url;

    List<Mannschaft> mannschaften = new ArrayList<>();
    List<Mannschaftspiel> spieleVorrunde = new ArrayList<>();
    List<Mannschaftspiel> spieleRueckrunde = new ArrayList<>();
    private String urlRR;
    private String urlVR;

    public Liga(String name, String url, String sex) {
        this.name = name;
        this.url = url;
        this.sex = sex;
    }

    public Liga() {

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<Mannschaft> getMannschaften() {
        return mannschaften;
    }

    public void addMannschaft(Mannschaft m) {
        mannschaften.add(m);
    }

    public List<Mannschaftspiel> getSpieleVorrunde() {
        return spieleVorrunde;
    }

    public List<Mannschaftspiel> getSpieleRueckrunde() {
        return spieleRueckrunde;
    }

    public void addSpiel(Mannschaftspiel s, boolean vorrunde) {
        if (vorrunde) {
            spieleVorrunde.add(s);
        } else {
            spieleRueckrunde.add(s);
        }
    }

    @Override
    public String toString() {
        return "Liga{" +
                "sex='" + sex + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", spieleVorrunde=" + spieleVorrunde +
                ", spieleRueckrunde=" + spieleRueckrunde +
                ", urlRR='" + urlRR + '\'' +
                ", urlVR='" + urlVR + '\'' +
                ", mannschaften=" + mannschaften +
                '}';
    }

    public void setUrlRR(String urlRR) {
        this.urlRR = urlRR;
    }

    public String getUrlRR() {
        return urlRR;
    }

    public void setUrlVR(String urlVR) {
        this.urlVR = urlVR;
    }

    public String getUrlVR() {
        return urlVR;
    }

    public List<Mannschaftspiel> getSpieleFor(String mannschaft, boolean vr) {
        if (vr) {
            if (mannschaft != null) {
                return filterSpiele(spieleVorrunde, mannschaft);
            } else {
                return getSpieleVorrunde();
            }
        } else {
            if (mannschaft != null) {
                return filterSpiele(spieleRueckrunde, mannschaft);
            } else {
                return getSpieleRueckrunde();
            }
        }

    }


    private List<Mannschaftspiel> filterSpiele(List<Mannschaftspiel> spiele, String mannschaft) {
        List<Mannschaftspiel> retList = new ArrayList<>();
        for (Mannschaftspiel mannschaftspiel : spiele) {
            if (mannschaftspiel.getGastMannschaft().getName().equals(mannschaft) ||
                    mannschaftspiel.getHeimMannschaft().getName().equals(mannschaft)) {
                retList.add(mannschaftspiel);
            }
        }
        return retList;
    }

    public void clearSpiele(boolean vorrunde) {
        if (vorrunde) {
            spieleVorrunde.clear();
        } else {
            spieleRueckrunde.clear();
        }
    }
}
