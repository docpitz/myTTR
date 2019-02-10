package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;

public class LigaTabelleAsyncTask extends BaseAsyncTask {
    MytClickTTWrapper clickTTWrapper = new MytClickTTWrapper();

    public LigaTabelleAsyncTask(Activity parent) {
        super(parent, LigaTabelleActivity.class);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, ValidationException, NoClickTTException {
        clickTTWrapper.readLiga(MyApplication.saison, MyApplication.getSelectedLiga());
    }

    @Override
    protected boolean dataLoaded() {
        return MyApplication.getSelectedLiga().getMannschaften().size() > 0;
    }
}
