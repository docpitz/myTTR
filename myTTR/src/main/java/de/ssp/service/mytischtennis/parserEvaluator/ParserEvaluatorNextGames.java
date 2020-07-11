package de.ssp.service.mytischtennis.parserEvaluator;

import android.content.Context;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.logic.AppointmentParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.ValidationException;

import java.util.List;

import de.ssp.service.mytischtennis.model.NextGame;

public class ParserEvaluatorNextGames implements ParserEvaluator<Void, NextGame[]>
{
    protected Context context;

    public ParserEvaluatorNextGames(Context context)
    {
        this.context = context;
    }

    @Override
    public NextGame[] evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getNameOfOwnClub();
        NextGame[] nextGames = new NextGame[]{};
        if (name != null) {
            AppointmentParser appointmentParser = new AppointmentParser();
            List<TeamAppointment> teamAppointmentList = appointmentParser.read(name);
            ParserEvaluatorOwnClub parserEvaluatorOwnClub = new ParserEvaluatorOwnClub(context);
            Club club = parserEvaluatorOwnClub.evaluateParser();
            TeamAppointment[] teamAppointments = teamAppointmentList.toArray(new TeamAppointment[teamAppointmentList.size()]);
            nextGames = NextGame.convertFromTeamAppointment(teamAppointments, club);
        } else {
            String errorMessage = "Konnte den Namen deines Vereins nicht ermitteln. Wahrscheinlich ein Fehler bei mytischtennis.de." +
                    "Du kannst ihn aber in den Einstellungen selbst eingeben.";
            throw new ValidationException(errorMessage);

        }
        return nextGames;
    }


    @Override
    public Void getPostElement()
    {
        return null;
    }
}
