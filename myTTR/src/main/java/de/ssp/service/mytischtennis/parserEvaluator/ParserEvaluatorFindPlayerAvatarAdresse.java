package de.ssp.service.mytischtennis.parserEvaluator;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParserExtended;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;

import java.util.ArrayList;

public class ParserEvaluatorFindPlayerAvatarAdresse implements ParserEvaluator<String, String>
{
    protected String playersId;
    public ParserEvaluatorFindPlayerAvatarAdresse(String playersId)
    {
        this.playersId = playersId;
    }

    @Override
    public String evaluateParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NoClickTTException, NiceGuysException
    {
        ArrayList<Player> returnPlayerList = new ArrayList<>();
        MyTischtennisParserExtended parser = new MyTischtennisParserExtended();
        return parser.getPicUrl(playersId);
    }

    @Override
    public String getPostElement() {
        return playersId;
    }
}
