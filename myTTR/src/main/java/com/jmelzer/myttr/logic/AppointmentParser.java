package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.TeamAppointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Pares mytischtennis.de for getting the next TeamAppointments
 * User: jmelzer
 */
public class AppointmentParser {

    private boolean redirectedToLogin(String page) {
        return page.contains("<title>Login");
    }

    public List<TeamAppointment> read(String clubName) throws NetworkException, LoginExpiredException {
        String url = "https://www.mytischtennis.de/community/index";
        String page = Client.getPage(url);
        if (redirectedToLogin(page)) {
            throw new LoginExpiredException();
        }
        return parse(page, clubName);
    }

    List<TeamAppointment> parse(String page, String clubName) {
        List<TeamAppointment> list = new ArrayList<TeamAppointment>();
        if (page == null) {
            return list;
        }

        final String toCheckError = "keine</span> Termine vorhanden";
        if (page.indexOf(toCheckError) > 0) {
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


        n = readTeamName(page, n, appointment, true);
        n = readTeamName(page, n, appointment, false);

        if (appointment.getTeam1().contains(clubName)) {
//            readTeamName(page, n, appointment);
            appointment.setPlayAway(false);
            appointment.setFoundTeam(true);

        } else if (appointment.getTeam2().contains(clubName)) {
            appointment.setFoundTeam(true);
            appointment.setPlayAway(true);
        } else {
            appointment.setPlayAway(null);
            appointment.setFoundTeam(false);
        }


        return appointment;
    }

    private int readTeamName(String page, int start, TeamAppointment appointment, boolean first) {
        String team = "teamId=";
        int n = page.indexOf(team, start);
        int end = page.indexOf("\">", n);
        String id = page.substring(n + team.length(), end);
        int n2 = page.indexOf("</a>", end);
        String name = page.substring(end + 2, n2);
        if (first) {
            appointment.setId1(id);
            appointment.setTeam1(name);
        } else {
            appointment.setId2(id);
            appointment.setTeam2(name);
        }
        return n2;
    }

}
