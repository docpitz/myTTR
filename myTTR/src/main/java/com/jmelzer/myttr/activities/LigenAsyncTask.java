package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Saison;

/**
 * task for calling events activity.
 */
public class LigenAsyncTask extends BaseAsyncTask {

    MytClickTTWrapper wrapper = new MytClickTTWrapper();

    public LigenAsyncTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
        MyApplication.selectedVerband = wrapper.readTopLigen(Constants.ACTUAL_SAISON);
    }

    @Override
    protected boolean dataLoaded() {
        return MyApplication.selectedVerband != null &&
                MyApplication.selectedVerband.getLigaList().size() > 0;
    }

}