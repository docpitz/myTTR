package de.ssp.service.mytischtennis.parserEvaluator;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

public class ParserEvaluatorSearchPlayer implements ParserEvaluator<SearchPlayer, List<Player>>
{
    public static String ZU_VIELE_SPIELER_GEFUNDEN = "Es wurden zu viele Spieler gefunden.";
    private SearchPlayer searchPlayer;
    private String errorMessage;

    public ParserEvaluatorSearchPlayer(SearchPlayer searchPlayer)
    {
        this.searchPlayer = searchPlayer;
    }

    @Override
    public List<Player> evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> listPlayer = null;
        try {
            listPlayer = myTischtennisParser.findPlayer(searchPlayer);
        }
        catch(TooManyPlayersFound e)
        {
            throw new ValidationException(ZU_VIELE_SPIELER_GEFUNDEN);
        }
        return listPlayer;
    }

    @Override
    public SearchPlayer getPostElement() {
        return searchPlayer;
    }
}
