package com.jmelzer.myttr.model;

import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J. Melzer on 04.03.2018.
 * Rheinland Cupe etc
 */

public class Cup {

    public static List<Cup> cups = new ArrayList<>();

    static {
        cups.add(new Cup("", null));
        cups.add(new Cup("andro WTTV-Cup", "http://wttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=WTTV&circuit=2018_Turnierserie"));
        cups.add(new Cup("Bavarian TT-Race", "https://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=ByTTV&circuit=2018_BTTR"));
        cups.add(new Cup("VR Cup",
                "http://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HeTTV&circuit=VR-Cup_2018"));
        cups.add(new Cup("TTVN-Race",
                "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=TTVN&circuit=TTVN-Race"));
        cups.add(new Cup("tt-megastore Pfalz Trophy",
                "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=PTTV&circuit=tt-megastore+Pfalz+Trophy+2018"));
        cups.add(new Cup("TTVR Rheinland Cup",
                "http://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=VOL%20R7&circuit=Rheinland%20Cup"));
        cups.add(new Cup("Clickball Rheinland Cup",
                "http://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?circuit=Clickball+Rheinland+Cup&federation=VOL+R7"));

    }


    private String cupName;
    private String cupUrl;

    public Cup(String cupName, String cupUrl) {
        this.cupName = cupName;
        this.cupUrl = cupUrl;
    }

    public static List<Cup> cups() {
        return Collections.unmodifiableList(cups);
    }

    public String getCupName() {
        return cupName;
    }

    public String getCupUrl() {
        return cupUrl;
    }

    public String getHttpAndDomain() {
        return UrlUtil.getHttpAndDomain(cupUrl);
    }
}
