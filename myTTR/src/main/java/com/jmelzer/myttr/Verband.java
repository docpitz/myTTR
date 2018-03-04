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
    public static Verband dttb = new Verband("DTTB",
            "https://www.mytischtennis.de/clicktt/DTTB/17-18/ligen",
            "https://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15",
            "https://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=DTTB");
    public static List<Verband> verbaende = new ArrayList<>();

    public static Verband WTTV = new Verband("Westdeutscher TTV",
            "https://www.mytischtennis.de/clicktt/WTTV/17-18/ligen",
            "https://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/leaguePage?championship=WTTV%2014/15",
            "https://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/tournamentCalendar?federation=WTTV");

    static {
        verbaende.add(dttb);
        verbaende.add(new Verband("Badischer TTV",
                "https://www.mytischtennis.de/clicktt/BaTTV/17-18/ligen",
                "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+Bad.+14/15",
                "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=BaTTV"));
        verbaende.add(new Verband("Bayerischer TTV",
                "https://www.mytischtennis.de/clicktt/ByTTV/17-18/ligen",
                "https://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=V000+2014%2F15",
                "https://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=ByTTV"));
//        verbaende.add(new Verband("Berliner TTV", "https://bettv.tischtennislive.de/"));
        verbaende.add(new Verband("TTV Brandenburg",
                "https://www.mytischtennis.de/clicktt/TTVB/17-18/ligen",
                "https://ttvb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=VOL+Ost%2FNord+16%2F17",
                "https://ttvb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVB"));
        verbaende.add(new Verband("FTT Bremen",
                "https://www.mytischtennis.de/clicktt/FTTB/17-18/ligen",
                "https://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN+14/15",
                "https://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=FTTB"));
        verbaende.add(new Verband("Hessischer TTV",
                "https://www.mytischtennis.de/clicktt/HeTTV/17-18/ligen",
                "https://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=HTTV%2014/15",
                "https://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HeTTV"));
        verbaende.add(new Verband("Hamburger TTV",
                "https://www.mytischtennis.de/clicktt/HaTTV/17-18/ligen",
                "http://hattv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=HaTTV+15%2F16",
                "http://hattv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HaTTV"));
        verbaende.add(new Verband("TTV Mecklenburg-Vorpommern",
                "https://www.mytischtennis.de/clicktt/TTVMV/17-18/ligen",
                "http://ttvmv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTVMV%2016/17",
                "http://ttvmv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVMV"));
        verbaende.add(new Verband("TTV Niedersachsen",
                "https://www.mytischtennis.de/clicktt/TTVN/17-18/ligen",
                "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN%2014/15",
                "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=TTVN"));
        verbaende.add(new Verband("Pfälzischer TTV",
                "https://www.mytischtennis.de/clicktt/PTTV/17-18/ligen",
                "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=PTTV+14/15",
                "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=PTTV"));
        verbaende.add(new Verband("Rheinhessischer TTV",
                "https://www.mytischtennis.de/clicktt/RTTV/17-18/ligen",
                "https://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=RTTV+14/15",
                "https://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=RTTV"));
        verbaende.add(new Verband("TTV Rheinland",
                "https://www.mytischtennis.de/clicktt/TTVR/17-18/ligen",
                "https://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTVR+14/15",
                "https://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVR"));
        verbaende.add(new Verband("Saarländischer TTB",
                "https://www.mytischtennis.de/clicktt/STTB/17-18/ligen",
                "https://sttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=STTB+14/15",
                "https://sttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=STTB"));
        verbaende.add(new Verband("TTV Sachsen-Anhalt",
                "https://www.mytischtennis.de/clicktt/TTVSA/17-18/ligen",
                "https://ttvsa.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVSA+14/15",
                null));
//        verbaende.add(new Verband("TTV Schleswig-Holstein", "https://ttvsh.tischtennislive.de/"));
        verbaende.add(new Verband("Südbadischer Tischtennis-Verband",
                "https://www.mytischtennis.de/clicktt/SbTTV/17-18/ligen",
                "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+SbTTV+14/15",
                "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=SbTTV"));
        verbaende.add(new Verband("Thüringer TTV",
                "https://www.mytischtennis.de/clicktt/TTTV/17-18/ligen",
                "https://tttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTTV%2016/17",
                "https://tttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTTV"));
        verbaende.add(new Verband("TTV Württemberg-Hohenzollern",
                "https://www.mytischtennis.de/clicktt/TTVWH/17-18/ligen",
                "https://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=SK+TTVWH+14/15",
                "https://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=TTVWH"));
        verbaende.add(WTTV);
    }

    String name;
    String url;
    String myTTClickTTUrl;
    //tournament url
    private String tUrl;


//    public static List<Verband> alleVerbaende = Arrays.asList(new Verband())

    List<Bezirk> bezirkList = new ArrayList<>();
    List<Liga> ligaList = new ArrayList<>();

    public Verband(String name, String myTTClickTTUrl, String url, String tUrl) {
        this(name, url, tUrl);
        this.myTTClickTTUrl = myTTClickTTUrl;
    }

    public Verband(String name, String url, String tUrl) {
        this.name = name;
        this.url = url;
        this.tUrl = tUrl;
    }

    public String getMyTTClickTTUrl() {
        return myTTClickTTUrl;
    }

    public String gettUrl() {
        return tUrl;
    }

    public static List<Verband> verbaendeWithTournaments() {
        List<Verband> list = new ArrayList<>();
        list.add(new Verband("", "", null));
        for (Verband verband : verbaende) {
            if (verband.gettUrl() != null)
                list.add(verband);
        }
        return list;
    }

    public String getHttpAndDomain(Saison saison) {
        if (isMyTTUrl(saison))
            return UrlUtil.getHttpAndDomain(myTTClickTTUrl);
        else
            return UrlUtil.getHttpAndDomain(url);
    }

    private boolean isMyTTUrl(Saison saison) {
        return saison != null && myTTClickTTUrl != null && Saison.SAISON_2018 == saison;
    }

    public void setBezirkList(List<Bezirk> bezirkList, Saison saison) {
        this.bezirkList = bezirkList;
        for (Bezirk bezirk : bezirkList) {
            bezirk.setUrl(UrlUtil.safeUrl(getHttpAndDomain(saison), bezirk.getUrl()));
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
        if (isMyTTUrl(saison))
            return myTTClickTTUrl;
        else
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
                return url.replace("14/15", "17/18").replace("2014%2F15", "2016%2F17").
                        replace("2016/17", "2017/18").replace("16%2F17", "17%2F18");
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

    public void addAllLigen(List<Liga> ligen, Saison saison) {
        ligaList.clear();
        ligaList.addAll(ligen);
        for (Liga liga : ligen) {
            liga.setUrl(UrlUtil.safeUrl(getHttpAndDomain(saison), liga.getUrl()));
        }
    }
}
