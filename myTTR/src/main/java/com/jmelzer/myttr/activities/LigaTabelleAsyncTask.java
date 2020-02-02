package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;

import static com.jmelzer.myttr.MyApplication.getSelectedLiga;
import static com.jmelzer.myttr.MyApplication.selectedOtherTeam;
import static com.jmelzer.myttr.MyApplication.setSelectedLiga;

public class LigaTabelleAsyncTask extends BaseAsyncTask {
    MytClickTTWrapper clickTTWrapper = new MytClickTTWrapper();

    public LigaTabelleAsyncTask(Activity parent) {
        super(parent, LigaTabelleActivity.class);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, ValidationException, NoClickTTException, NoDataException {
        if (selectedOtherTeam != null) {
            if (selectedOtherTeam.getLiga() == null) {
                myTischtennisParser.readOtherTeam(selectedOtherTeam);
            }
            String fakedUrl = "https://www.mytischtennis.de/clicktt/DTTB/19-20/ligen/TTBL/gruppe/%s/tabelle/gesamt/";
            fakedUrl = String.format(fakedUrl, selectedOtherTeam.getLiga().getGroupId());
            selectedOtherTeam.getLiga().setUrl(fakedUrl);

            setSelectedLiga(selectedOtherTeam.getLiga());
        }

        clickTTWrapper.readLiga(MyApplication.saison, getSelectedLiga());

    }

    @Override
    protected boolean dataLoaded() {
        return getSelectedLiga().getMannschaften().size() > 0;
    }
}
