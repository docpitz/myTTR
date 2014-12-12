package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.TeamAppointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Pares mytischtennis.de for getting the next TeamAppointments
 * User: jmelzer
 */
public class AppointmentParser {


    public List<TeamAppointment> read(String clubName) {
        String url = "http://www.mytischtennis.de/community/index";
        String page = Client.getPage(url);
        return read(page, clubName);
    }

    private List<TeamAppointment> read(String page, String clubName) {
        List<TeamAppointment> list = new ArrayList<TeamAppointment>();
        if (page == null) {
            return list;
        }

        final String toCheck = "zu meiner Mannschaft";
        String tr = "<tr>";
        String trClosed = "</tr>";

        int start = page.indexOf(toCheck);
        if (start > 0) {
            int n = page.indexOf(tr, start);
            while (n > 0) {
                list.add(readApp(page, n, clubName));
                n = page.indexOf(trClosed, n);
                n = page.indexOf(tr, n);
            }
        }
        return list;

    }

    private TeamAppointment readApp(String page, int startPoint, String clubName) {
        TeamAppointment appointment = new TeamAppointment();
        String td = "<td>";
        String tdClosed = "</td>";
        int n = page.indexOf(td, startPoint);
        int n2 = page.indexOf(tdClosed, n);
        appointment.setDate(page.substring(n + td.length(), n2));


        n = readTeamName(page, n, appointment);

        if (appointment.getTeam().contains(clubName)) {
            readTeamName(page, n, appointment);
            appointment.setPlayAway(false);
        } else {
            appointment.setPlayAway(true);
        }


        return appointment;
    }

    private int readTeamName(String page, int start, TeamAppointment appointment) {
        String team = "teamId=";
        int n = page.indexOf(team, start);
        int end = page.indexOf("\">", n);
        appointment.setId(page.substring(n + team.length(), end));
        int n2 = page.indexOf("</a>", end);
        appointment.setTeam(page.substring(end + 2, n2));
        return n2;
    }

}
