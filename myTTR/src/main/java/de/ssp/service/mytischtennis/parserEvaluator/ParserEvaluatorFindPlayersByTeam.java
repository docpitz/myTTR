package de.ssp.service.mytischtennis.parserEvaluator;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;

import java.util.List;

public class ParserEvaluatorFindPlayersByTeam implements ParserEvaluator<String, List<Player>>
{
    protected String id;
    public ParserEvaluatorFindPlayersByTeam(String id)
    {
        this.id = id;
    }

    @Override
    public List<Player> evaluateParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NiceGuysException
    {
        return new MyTischtennisParser().readPlayersFromTeam(id);
    }

    @Override
    public String getPostElement() {
        return id;
    }
}
