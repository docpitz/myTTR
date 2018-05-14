package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;

/**
 * Created by jmelzer on 14.05.2018.
 */
public class ClubListAsyncTask extends BaseAsyncTask {

    boolean actual = true;
    public ClubListAsyncTask(Activity parent, boolean actual) {
        super(parent, ClubListActivity.class);
        this.actual = actual;
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, NiceGuysException {
        MyApplication.clubPlayers = new MyTischtennisParser().getClubList(actual);
    }


    @Override
    protected boolean dataLoaded() {
        return MyApplication.clubPlayers != null;
    }
}
