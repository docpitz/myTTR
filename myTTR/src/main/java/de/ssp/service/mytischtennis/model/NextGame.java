package de.ssp.service.mytischtennis.model;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.TeamAppointment;

import java.util.ArrayList;

public class NextGame
{
    public static NextGame[] convertFromTeamAppointment(TeamAppointment[] teamAppointments, Club ownClub)
    {
        ArrayList<NextGame> nextGames = new ArrayList<>();
        for (TeamAppointment teamAppointment: teamAppointments)
        {
            nextGames.add(new NextGame(teamAppointment, ownClub));
        }
        return nextGames.toArray(new NextGame[nextGames.size()]);
    }


    protected TeamAppointment teamAppointment;
    protected Club ownClub;
    public NextGame(TeamAppointment teamAppointment, Club ownClub)
    {
        this.teamAppointment = teamAppointment;
        this.ownClub = ownClub;
    }

    public String getGegner()
    {
        if(isHeimspiel())
        {
            return teamAppointment.getTeam2();
        }
        return teamAppointment.getTeam1();
    }

    public String getGegnerId()
    {
        if(isHeimspiel())
        {
            return teamAppointment.getId2();
        }
        return teamAppointment.getId1();
    }

    public TeamAppointment getTeamAppointment()
    {
        return teamAppointment;
    }


    protected boolean isHeimspiel()
    {
        return teamAppointment.getTeam1().contains(ownClub.getName());

    }
}
