package com.jmelzer.myttr.logic;

import android.content.Context;
import java.util.List;

public class TTRClubParser extends ClubParser
{
    Context context;
    public TTRClubParser(Context context)
    {
        this.context = context;
        readClubs();
    }

    public List<String> getClubs()
    {
        return clubNames;
    }

    synchronized void readClubs() {
        if (clubHashMap.isEmpty()) {
            int r = getContext().getResources().getIdentifier("raw/stopwords",
                    "raw",
                    "de.ssp.ttr_rechner");
            readStopwords(r);
            r = getContext().getResources().getIdentifier("raw/vereine",
                    "raw",
                    "de.ssp.ttr_rechner");
            readFile(r);

            readClubNames();
        }
    }

    @Override
    protected Context getContext() {
        return context.getApplicationContext();
    }
}
