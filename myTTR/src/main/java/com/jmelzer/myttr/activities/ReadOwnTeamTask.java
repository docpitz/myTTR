package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;

import static com.jmelzer.myttr.MyApplication.selectedMannschaft;
import static com.jmelzer.myttr.MyApplication.selectedOtherTeam;
import static com.jmelzer.myttr.MyApplication.setSelectedLiga;

public class ReadOwnTeamTask extends BaseAsyncTask {
    public ReadOwnTeamTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, NoDataException, ValidationException, NoClickTTException {
        if (selectedOtherTeam == null) {
            selectedMannschaft = myTischtennisParser.readOwnTeam();
        } else {
            if (selectedOtherTeam.getLiga() == null) {
                myTischtennisParser.readOtherTeam(selectedOtherTeam);
            }
            selectedMannschaft = selectedOtherTeam;
        }
        setSelectedLiga(selectedMannschaft.getLiga());
    }

    @Override
    protected boolean dataLoaded() {
        return selectedMannschaft != null;
    }

}
