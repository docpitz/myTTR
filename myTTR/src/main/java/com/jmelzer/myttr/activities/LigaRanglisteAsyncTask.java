package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;

import static com.jmelzer.myttr.MyApplication.selectedOtherTeam;

public class LigaRanglisteAsyncTask extends BaseAsyncTask {

    public LigaRanglisteAsyncTask(Activity parent) {
        super(parent, LigaRanglisteActivity.class);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, NoDataException, ValidationException {
        if (selectedOtherTeam == null) {
            MyApplication.myTTLigen = myTischtennisParser.readLigaRanking(null);
        } else {
            if (selectedOtherTeam.getLiga() == null) {
                myTischtennisParser.readOtherTeam(selectedOtherTeam);
            }
            MyApplication.myTTLigen = myTischtennisParser.readLigaRanking(selectedOtherTeam.getLiga().getGroupId());
        }
    }


    @Override
    protected boolean dataLoaded() {
        return MyApplication.myTTLigen != null;
    }
}
