package com.jmelzer.myttr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * e.g. wttv
 */
public class Verband {
    public static Verband dttb = new Verband("DTTB", "http://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15");
    //    static Map<String, String> nameMapping = new HashMap<>();
    public static List<Verband> verbaende = new ArrayList<>();

    static {
        verbaende.add(dttb);
        verbaende.add(new Verband("Badischer TTV", "http://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+Bad.+14/15"));
        verbaende.add(new Verband("Bayerischer TTV", "http://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=V000+2014%2F15"));
//        verbaende.add(new Verband("Berliner TTV", "http://bettv.tischtennislive.de/"));
//        verbaende.add(new Verband("TTV Brandenburg", "http://www.tt-info.net"));
        verbaende.add(new Verband("FTT Bremen", "http://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN+14/15"));
//        verbaende.add(new Verband("Hamburger TTV", "http://www.tt-maximus.de/pdpages/ttmaximus/herren_staffeln.php"));
        verbaende.add(new Verband("Hessischer TTV", "http://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=HTTV%2014/15"));
//        verbaende.add(new Verband("TTV Mecklenburg-Vorpommern", "http://www.tt-info.net"));
        verbaende.add(new Verband("TTV Niedersachsen", "http://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN%2014/15"));
//        verbaende.add(new Verband("Pfälzischer TTV", "http://www.tt-info.net/"));
        verbaende.add(new Verband("Rheinhessischer TTV", "http://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=RTTV+14/15"));
        verbaende.add(new Verband("TTV Rheinland", "http://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTVR+14/15"));
//        verbaende.add(new Verband("Saarländischer TTB", "http://www.sttb.de/index.php?id=tabellen"));
//        verbaende.add(new Verband("Sächsischer TTV", "http://sttv.tischtennislive.de/"));
        verbaende.add(new Verband("TTV Sachsen-Anhalt", "http://ttvsa.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVSA+14/15"));
//        verbaende.add(new Verband("TTV Schleswig-Holstein", "http://ttvsh.tischtennislive.de/"));
        verbaende.add(new Verband("Südbadischer Tischtennis-Verband", "http://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+SbTTV+14/15"));
//        verbaende.add(new Verband("Thüringer TTV", "http://www.tttv.info/index.php?option=com_content&view=category&layout=blog&id=3&Itemid=15"));
        verbaende.add(new Verband("TTV Württemberg-Hohenzollern", "http://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=SK+TTVWH+14/15"));
        verbaende.add(new Verband("Westdeutscher TTV", "http://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/leaguePage?championship=WTTV%2014/15"));
    }
//    static {
//        nameMapping.put("Baden", "Badischer TTV");
//        nameMapping.put("Bayern", "Bayerischer TTV");
//        nameMapping.put("Not Implemented", "Berliner TTV");
//        //http://www.tt-info.net/TTVB_men.html
//        nameMapping.put("Brandenburg", "TTV Brandenburg");
//        nameMapping.put("Bremen",  "FTT Bremen");
//        nameMapping.put("Not Implemented",  "Hamburger TTV");
//        nameMapping.put("Hessen", "Hessischer TTV");
//        nameMapping.put("Not Implemented", "TTV Mecklenburg-Vorpommern");
//        nameMapping.put("Niedersachsen",  "TTV Niedersachsen");
////        http://www.tt-info.net/
//        nameMapping.put("Not Implemented",  "Pfälzischer TTV");
//        nameMapping.put("Rheinhessen",  "Rheinhessischer TTV");
//    }


    String name;
    String url;

//    public static List<Verband> alleVerbaende = Arrays.asList(new Verband())

    List<Bezirk> bezirkList = new ArrayList<>();
    List<Liga> ligaList = new ArrayList<>();

    public Verband(String name, String url) {
        this.name = name;
        this.url = url;
    }
    public String getHttpAndDomain() {
        return url.substring(0, url.indexOf(".de")+3);
    }
    public void addBezirk(Bezirk d) {
        bezirkList.add(d);
    }

    public void addLiga(Liga l) {
        ligaList.add(l);
    }

    public void setBezirkList(List<Bezirk> bezirkList) {
        this.bezirkList = bezirkList;
        for (Bezirk bezirk : bezirkList) {
            bezirk.setVerband(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<Bezirk> getBezirkList() {
        return Collections.unmodifiableList(bezirkList);
    }

    public List<Liga> getLigaList() {
        return Collections.unmodifiableList(ligaList);
    }

    public void addAllLigen(List<Liga> ligen) {
        ligaList.clear();
        ligaList.addAll(ligen);
        for (Liga liga : ligen) {
            liga.setVerband(this);
        }
    }
}
