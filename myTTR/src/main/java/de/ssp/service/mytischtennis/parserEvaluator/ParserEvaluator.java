package de.ssp.service.mytischtennis.parserEvaluator;

import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;

public interface ParserEvaluator<S, F>
{
    public F evaluateParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NoClickTTException, NiceGuysException;
    public S getPostElement();
}
