package de.ssp.service.mytischtennis.parserEvaluator;

import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginException;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.ValidationException;

public class ParserEvaluatorRealNameAndPoints implements ParserEvaluator<Void, User>
{
    class MyTischtennisExtendedParser extends MyTischtennisParser
    {
        private static final String ZUR_QTTR_HISTORIE = "zur TTR-Historie\">Q-TTR ";
        public int parsePoints(String page) throws PlayerNotWellRegistered {
            int point = super.parsePoints(page);
            if(point > 0)
            {
                return point;
            }
            else if(point < 0)
            {
                try
                {
                    int start = page.indexOf(ZUR_QTTR_HISTORIE);
                    if (start < 0) {
                        return -1;
                    }
                    start = start + ZUR_QTTR_HISTORIE.length();
                    int end = page.indexOf("</a>", start + 1);
                    if (end < 0) {
                        return -2;
                    }
                    try
                    {
                        return Integer.valueOf(page.substring(start, end));
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e(this.getClass().toString(), e.getMessage());
                        return -3;
                    }
                }
                catch (Exception e)
                {
                    Log.e(Constants.LOG_TAG, "error parsing page", e);
                }
            }
            return 0;
        }
    }

    @Override
    public User evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        User user = null;
        try
        {
            user = new MyTischtennisExtendedParser().getPointsAndRealName();
        }
        catch (PlayerNotWellRegistered | LoginException e)
        {
            // Sollte an der Stelle nicht auftreten da bereits eingeloggt
            new LoginManager().logout();
            throw new ValidationException(e.getMessage());
        }
        return user;
    }

    @Override
    public Void getPostElement() {
        return null;
    }
}
