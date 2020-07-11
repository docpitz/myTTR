package de.ssp.service.mytischtennis.caller;

import android.content.Context;

import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluator;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorIsPremiumAccount;

public class ServiceCallerIsPremiumAccount extends MyTischtennisEnsureLoginCaller<Void, Boolean>
{
    public ServiceCallerIsPremiumAccount(Context context, ServiceFinish<Void, Boolean> serviceFinish)
    {
        super(context, "Suche nach Premium-Account", serviceFinish);
    }

    @Override
    protected ParserEvaluator<Void, Boolean> getParserEvaluator() {
        return new ParserEvaluatorIsPremiumAccount();
    }
}
