package de.ssp.service.mytischtennis.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorSearchPlayer;

public class ServiceCallerSearchPlayer extends MyTischtennisEnsureLoginCaller<SearchPlayer, List<Player>>
{
    private SearchPlayer searchPlayer;
    public ServiceCallerSearchPlayer(Context context, ServiceFinish<SearchPlayer, List<Player>> serviceFinish, SearchPlayer searchPlayer, boolean isViewLocked)
    {
        super(context, isViewLocked ? "Suche Spieler" : null, serviceFinish);
        this.searchPlayer = searchPlayer;
    }

    @Override
    protected ParserEvaluator<SearchPlayer, List<Player>> getParserEvaluator() {
        return new ParserEvaluatorSearchPlayer(searchPlayer);
    }
}
