package com.jmelzer.myttr;

import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J. Melzer on 19.02.2015.
 * e.g. wttv
 */
public class Verband {
    public static Verband dttb = new Verband("DTTB", "http://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15", null);
    public static List<Verband> verbaende = new ArrayList<>();


    static {
        verbaende.add(dttb);
        verbaende.add(new Verband("Badischer TTV", "http://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+Bad.+14/15",
                "http://ttvbw.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar"));
        verbaende.add(new Verband("Bayerischer TTV", "http://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=V000+2014%2F15",
                "http://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=ByTTV"));
//        verbaende.add(new Verband("Berliner TTV", "http://bettv.tischtennislive.de/"));
        verbaende.add(new Verband("TTV Brandenburg", "http://ttvb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=VOL+Ost%2FNord+16%2F17",
                "http://ttvb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVB"));
        verbaende.add(new Verband("FTT Bremen", "http://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN+14/15",
                "http://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=FTTB"));
//        verbaende.add(new Verband("Hamburger TTV", "http://www.tt-maximus.de/pdpages/ttmaximus/herren_staffeln.php"));
        verbaende.add(new Verband("Hessischer TTV", "http://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=HTTV%2014/15",
                "http://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HeTTV"));
//        verbaende.add(new Verband("TTV Mecklenburg-Vorpommern", "http://www.tt-info.net"));
        verbaende.add(new Verband("TTV Niedersachsen", "http://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN%2014/15",
                "http://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=TTVN"));
        verbaende.add(new Verband("Pfälzischer TTV", "http://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=PTTV+14/15",
                "http://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=PTTV"));
        verbaende.add(new Verband("Rheinhessischer TTV", "http://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=RTTV+14/15",
                "http://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=RTTV"));
        verbaende.add(new Verband("TTV Rheinland", "http://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTVR+14/15",
                "http://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVR"));
        verbaende.add(new Verband("Saarländischer TTB", "http://sttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=STTB+14/15",
                "http://sttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=STTB"));
//        verbaende.add(new Verband("Sächsischer TTV", "http://sttv.tischtennislive.de/"));
        verbaende.add(new Verband("TTV Sachsen-Anhalt", "http://ttvsa.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVSA+14/15",
                null));
//        verbaende.add(new Verband("TTV Schleswig-Holstein", "http://ttvsh.tischtennislive.de/"));
        verbaende.add(new Verband("Südbadischer Tischtennis-Verband", "http://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+SbTTV+14/15",
                "http://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=SbTTV"));
        verbaende.add(new Verband("Thüringer TTV", "http://tttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTTV%2016/17",
                "http://tttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTTV"));
        verbaende.add(new Verband("TTV Württemberg-Hohenzollern", "http://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=SK+TTVWH+14/15",
                "http://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=TTVWH"));
        verbaende.add(new Verband("Westdeutscher TTV", "http://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/leaguePage?championship=WTTV%2014/15",
                "http://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/tournamentCalendar?federation=WTTV"));
    }

    String name;
    String url;
    //tournament url
    private String tUrl;

//    public static List<Verband> alleVerbaende = Arrays.asList(new Verband())

    List<Bezirk> bezirkList = new ArrayList<>();
    List<Liga> ligaList = new ArrayList<>();

    public Verband(String name, String url, String tUrl) {
        this.name = name;
        this.url = url;
        this.tUrl = tUrl;
    }

    public String gettUrl() {
        return tUrl;
    }
    public static List<Verband> verbaendeWithTournaments() {
        List<Verband> list = new ArrayList<>();
        list.add(new Verband("","",null));
        for (Verband verband : verbaende) {
            if (verband.gettUrl() != null)
                list.add(verband);
        }
        return list;
    }
    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(url);
    }

    public void setBezirkList(List<Bezirk> bezirkList) {
        this.bezirkList = bezirkList;
        for (Bezirk bezirk : bezirkList) {
            bezirk.setUrl(UrlUtil.safeUrl(getHttpAndDomain(), bezirk.getUrl()));
        }
    }

    public String getName() {
        return name;
    }

    /**
     * @return url with wrong year
     * @deprecated use getUrlFixed instead
     */
    public String getUrl() {
        return url;
    }

    public String getUrlFixed(Saison saison) {
        return replaceYear(url, saison);
    }

    String replaceYear(String url, Saison saison) {
        switch (saison) {
            case SAISON_2015:
                return url;
            case SAISON_2016:
                //silly workaround
                if (url.contains("rttv.click-tt.de")) {
                    return url.replace("RTTV+14/15", "RTTV+2015/2016");
                }
                return url.replace("14/15", "15/16").replace("2014%2F15", "2015%2F16");
            case SAISON_2017:
                //silly workaround
                if (url.contains("rttv.click-tt.de")) {
                    return url.replace("RTTV+14/15", "RTTV+2016/2017");
                }
                return url.replace("14/15", "16/17").replace("2014%2F15", "2016%2F17");
            case SAISON_2018:
                //silly workaround
                if (url.contains("rttv.click-tt.de")) {
                    return url.replace("RTTV+14/15", "RTTV+2017/2018");
                }
                return url.replace("14/15", "17/18").replace("2014%2F15", "2016%2F17");
            case SAISON_2014:
                return url.replace("14/15", "13/14").replace("2014%2F15", "2013%2F14");
            default:
                throw new IllegalArgumentException(saison + " undefined");
        }

    }

    public List<Bezirk> getBezirkList() {
        return Collections.unmodifiableList(bezirkList);
    }

    public List<Liga> getLigaList() {
        return Collections.unmodifiableList(ligaList);
    }

    public void clearLigaList() {
        ligaList.clear();
    }

    public void addAllLigen(List<Liga> ligen) {
        ligaList.clear();
        ligaList.addAll(ligen);
        for (Liga liga : ligen) {
            liga.setUrl(UrlUtil.safeUrl(getHttpAndDomain(), liga.getUrl()));
        }
    }
}
