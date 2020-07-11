package de.ssp.service.mytischtennis.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;

import java.util.List;

import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorFindCompletePlayer;

public class ServiceCallerFindCompletePlayer extends MyTischtennisEnsureLoginCaller<List<Player>, List<Player>>
{
    protected List<Player> player;
    public ServiceCallerFindCompletePlayer(Context context, ServiceFinish<List<Player>, List<Player>> serviceFinish, List<Player> player)
    {
        super(context, "Suche TTR-Punkte von Spielern", serviceFinish);
        this.player = player;
    }

    @Override
    protected ParserEvaluator<List<Player>, List<Player>> getParserEvaluator() {
        return new ParserEvaluatorFindCompletePlayer(player);
    }
}
