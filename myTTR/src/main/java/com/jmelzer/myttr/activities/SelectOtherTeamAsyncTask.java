package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoDataException;

public class SelectOtherTeamAsyncTask extends BaseAsyncTask {

    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
    Class goBackToClass;

    public SelectOtherTeamAsyncTask(Activity activity,  Class goBackToClass) {
        super(activity, SelectOtherTeamActivity.class);
        this.goBackToClass = goBackToClass;
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, NoDataException {
        myTischtennisParser.getOtherTeams();
    }

    @Override
    protected boolean dataLoaded() {
        return MyApplication.otherTeams != null;
    }

    @Override
    protected void putExtra(Intent target) {
        super.putExtra(target);
        target.putExtra(SelectOtherTeamActivity.INTENT_BACK_TO, goBackToClass);

    }
}
