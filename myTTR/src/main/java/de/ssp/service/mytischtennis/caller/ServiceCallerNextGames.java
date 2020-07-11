package de.ssp.service.mytischtennis.caller;

import android.content.Context;

import de.ssp.service.mytischtennis.model.NextGame;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorNextGames;

public class ServiceCallerNextGames extends MyTischtennisEnsureLoginCaller<Void, NextGame[]>
{
    public ServiceCallerNextGames(Context context, ServiceFinish<Void, NextGame[]> serviceFinish)
    {
        super(context, "Lade Begegnungen", serviceFinish);
    }

    @Override
    protected ParserEvaluator<Void, NextGame[]> getParserEvaluator() {
        return new ParserEvaluatorNextGames(context);
    }
}
