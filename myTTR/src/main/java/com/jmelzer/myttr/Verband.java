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
    public static Verband dttb = new Verband("DTTB", "https://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=DTTB+14/15",
            "https://dttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=DTTB", null, null);
    public static List<Verband> verbaende = new ArrayList<>();


    static {
        verbaende.add(dttb);
        verbaende.add(new Verband("Badischer TTV", "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+Bad.+14/15",
                "https://ttvbw.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar",
                null, null));
        verbaende.add(new Verband("Bayerischer TTV", "https://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=V000+2014%2F15",
                "https://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=ByTTV",
                "http://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=ByTTV&circuit=2017_BTTR", "Bavarian TT-Race"));
//        verbaende.add(new Verband("Berliner TTV", "https://bettv.tischtennislive.de/"));
        verbaende.add(new Verband("TTV Brandenburg", "https://ttvb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=VOL+Ost%2FNord+16%2F17",
                "https://ttvb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVB",
                null, null));
        verbaende.add(new Verband("FTT Bremen", "https://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN+14/15",
                "https://fttb.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=FTTB",
                null, null));
//        verbaende.add(new Verband("Hamburger TTV", "https://www.tt-maximus.de/pdpages/ttmaximus/herren_staffeln.php"));
        verbaende.add(new Verband("Hessischer TTV", "https://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=HTTV%2014/15",
                "https://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HeTTV",
                "http://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HeTTV&circuit=VR-Cup_2017", "VR Cup"));
//        verbaende.add(new Verband("TTV Mecklenburg-Vorpommern", "https://www.tt-info.net"));
        verbaende.add(new Verband("TTV Niedersachsen", "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVN%2014/15",
                "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=TTVN",
                "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=TTVN&circuit=TTVN-Race", "TTVN-Race"));
        verbaende.add(new Verband("Pfälzischer TTV", "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=PTTV+14/15",
                "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=PTTV",
                "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=PTTV&circuit=tt-megastore%20Pfalz%20Trophy",
                "tt-megastore Pfalz Trophy"));
        verbaende.add(new Verband("Rheinhessischer TTV", "https://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=RTTV+14/15",
                "https://rttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=RTTV",
                null, null));
        verbaende.add(new Verband("TTV Rheinland", "https://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTVR+14/15",
                "https://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVR",
                "http://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTVR&circuit=TTVR+Rheinland+Cup+2017",
                "TTVR Rheinland Cup"));
        verbaende.add(new Verband("Saarländischer TTB", "https://sttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=STTB+14/15",
                "https://sttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=STTB",
                null, null));
//        verbaende.add(new Verband("Sächsischer TTV", "https://sttv.tischtennislive.de/"));
        verbaende.add(new Verband("TTV Sachsen-Anhalt", "https://ttvsa.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/leaguePage?championship=TTVSA+14/15",
                null, null, null));
//        verbaende.add(new Verband("TTV Schleswig-Holstein", "https://ttvsh.tischtennislive.de/"));
        verbaende.add(new Verband("Südbadischer Tischtennis-Verband", "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=VSK+SbTTV+14/15",
                "https://ttvbw.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=SbTTV",
                null, null));
        verbaende.add(new Verband("Thüringer TTV", "https://tttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/leaguePage?championship=TTTV%2016/17",
                "https://tttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=TTTV",
                null, null));
        verbaende.add(new Verband("TTV Württemberg-Hohenzollern", "https://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/leaguePage?championship=SK+TTVWH+14/15",
                "https://ttvwh.click-tt.de/cgi-bin/WebObjects/ClickTTVBW.woa/wa/tournamentCalendar?federation=TTVWH",
                null, null));
        verbaende.add(new Verband("Westdeutscher TTV", "https://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/leaguePage?championship=WTTV%2014/15",
                "https://wttv.click-tt.de/cgi-bin/WebObjects/ClickWTTV.woa/wa/tournamentCalendar?federation=WTTV",
                "http://wttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=WTTV&circuit=2017_Turnierserie",
                "andro WTTV-Cup"));
    }

    String name;
    String url;
    //tournament url
    private String tUrl;
    private String cupUrl;
    private String cupName;

//    public static List<Verband> alleVerbaende = Arrays.asList(new Verband())

    List<Bezirk> bezirkList = new ArrayList<>();
    List<Liga> ligaList = new ArrayList<>();

    public Verband(String name, String url, String tUrl, String cupUrl, String cupName) {
        this.name = name;
        this.url = url;
        this.tUrl = tUrl;
        this.cupUrl = cupUrl;
        this.cupName = cupName;
    }

    public String gettUrl() {
        return tUrl;
    }

    public static List<Verband> verbaendeWithTournaments() {
        List<Verband> list = new ArrayList<>();
        list.add(new Verband("", "", null, null, null));
        for (Verband verband : verbaende) {
            if (verband.gettUrl() != null)
                list.add(verband);
        }
        return list;
    }

    public static List<Verband> cups() {
        List<Verband> list = new ArrayList<>();
        list.add(new Verband("", "", null, null, null));
        for (Verband verband : verbaende) {
            if (verband.cupUrl != null)
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

    public String getCupUrl() {
        return cupUrl;
    }

    public String getCupName() {
        return cupName;
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
                return url.replace("14/15", "17/18").replace("2014%2F15", "2016%2F17").
                        replace("2016/17", "2017/18");
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
