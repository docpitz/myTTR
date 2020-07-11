package de.ssp.service.mytischtennis.parserEvaluator;

import android.content.Context;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.TTRClubParser;

public class ParserEvaluatorOwnClub implements ParserEvaluator<Void, Club>
{
    protected Context context;
    public ParserEvaluatorOwnClub(Context context)
    {
        this.context = context;
    }

    @Override
    public Club evaluateParser()
    {
        String ownClub = new MyTischtennisParser().getNameOfOwnClub();
        TTRClubParser clubParser = new TTRClubParser(context);
        return clubParser.getClubExact(ownClub);
    }

    @Override
    public Void getPostElement() {
        return null;
    }
}
