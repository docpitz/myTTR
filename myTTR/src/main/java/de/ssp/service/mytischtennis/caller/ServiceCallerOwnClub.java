package de.ssp.service.mytischtennis.caller;

import android.content.Context;

import com.jmelzer.myttr.Club;

import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorOwnClub;

public class ServiceCallerOwnClub extends MyTischtennisEnsureLoginCaller<Void, Club>
{

    public ServiceCallerOwnClub(Context context, ServiceFinish<Void, Club> serviceFinish)
    {
        super(context, "Lade eigenen Verein", serviceFinish);
    }

    @Override
    protected ParserEvaluator<Void, Club> getParserEvaluator() {
        return new ParserEvaluatorOwnClub(context);
    }
}
