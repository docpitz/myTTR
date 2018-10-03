package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

public class LigaRanglisteAsyncTask extends BaseAsyncTask {

    public LigaRanglisteAsyncTask(Activity parent) {
        super(parent, LigaRanglisteActivity.class);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
        MyApplication.myTTLigen = new MyTischtennisParser().readOwnLigaRanking();
    }


    @Override
    protected boolean dataLoaded() {
        return MyApplication.myTTLigen != null;
    }
}
