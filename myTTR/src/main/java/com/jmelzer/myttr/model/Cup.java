package com.jmelzer.myttr.model;

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
        cups.add(new Cup("andro WTTV-Cup",
                "https://wttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=WTTV&circuit=2020_Turnierserie"));
        cups.add(new Cup("Bavarian TT-Race", "https://bttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=ByTTV&circuit=2020_BTTR"));
        cups.add(new Cup("VR Cup",
                "https://httv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=HeTTV&circuit=VR-Cup_2020"));
        cups.add(new Cup("TTVN-Race",
                "https://ttvn.click-tt.de/cgi-bin/WebObjects/ClickNTTV.woa/wa/tournamentCalendar?federation=TTVN&circuit=TTVN-Race"));
        cups.add(new Cup("tt-megastore Pfalz Trophy",
                "https://pttv.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=PTTV&circuit=tt-megastore+Pfalz+Trophy+2020"));
        cups.add(new Cup("TTVR Rheinland Cup",
                "https://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=VOL%20R7&circuit=RheinlandCup2019"));
        cups.add(new Cup("Clickball Rheinland Cup",
                "https://ttvr.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?circuit=Clickball+Rheinland+Cup&federation=VOL+R7"));
        cups.add(new Cup("FTTB GO2019 Race",
                "https://fttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/tournamentCalendar?federation=FTTB&circuit=FTTB%20GO2019%20Race"));
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
