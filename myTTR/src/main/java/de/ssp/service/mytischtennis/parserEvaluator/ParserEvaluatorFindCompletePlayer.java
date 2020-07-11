package de.ssp.service.mytischtennis.parserEvaluator;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class ParserEvaluatorFindCompletePlayer implements ParserEvaluator<List<Player>, List<Player>>
{
    protected List<Player> playerList;
    public ParserEvaluatorFindCompletePlayer(List<Player> playerList)
    {
        this.playerList = playerList;
    }

    @Override
    public List<Player> evaluateParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NoClickTTException, NiceGuysException
    {
        ArrayList<Player> returnPlayerList = new ArrayList<>();
        MyTischtennisParser parser = new MyTischtennisParser();
        for(Player player : playerList)
        {
            returnPlayerList.add(parser.completePlayerWithTTR(player));
        }
        return returnPlayerList;
    }

    @Override
    public List<Player> getPostElement() {
        return playerList;
    }
}
