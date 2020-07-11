package de.ssp.service.mytischtennis.caller;

import android.content.Context;

import com.jmelzer.myttr.User;

import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorRealNameAndPoints;

public class ServiceCallerRealNameAndPoints extends MyTischtennisEnsureLoginCaller<Void, User>
{
    public ServiceCallerRealNameAndPoints(Context context, ServiceFinish<Void, User> serviceFinish)
    {
        super(context, "Lade eigene Spielerdaten", serviceFinish);
    }

    @Override
    protected ParserEvaluator<Void, User> getParserEvaluator() {
        return new ParserEvaluatorRealNameAndPoints();
    }
}
