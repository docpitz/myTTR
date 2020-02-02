/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */


package com.jmelzer.myttr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.ClubParser;
import com.jmelzer.myttr.logic.LoginManager;

import java.util.List;

public class EnterClubNameActivity extends BaseActivity {
    ClubParser clubParser = new ClubParser();
    Club selectedClub;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.enter_club_name);
        final EditText txtField = (EditText) findViewById(R.id.name);
        if (MyApplication.manualClub != null) {
            txtField.setText(MyApplication.manualClub);
        }
    }

    public void ok(final View view) {

        final EditText txtField = (EditText) findViewById(R.id.name);
        String clubName = txtField.getText().toString();
        selectedClub = clubParser.getClubExact(clubName);
        if (selectedClub == null) {
            final List<String> clubs = clubParser.getClubNameUnsharp(clubName);
            if (clubs.size() == 0) {
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setCancelable(false); // This blocks the 'BACK' button
                ad.setMessage("Der Verein wurde nicht gefunden. ");
                ad.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(clubs.toArray(new String[clubs.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedClub = clubParser.getClubExact(clubs.get(which));
                        txtField.setText(clubs.get(which));
                    }
                });
                AlertDialog dlg = builder.create();
                dlg.show();
            }
        }
        if (selectedClub != null) {
            Toast.makeText(this,
                    "Der Verein wurde auf " + selectedClub.getName() + " ge\u00E4ndert",
                    Toast.LENGTH_SHORT).show();
            MyApplication.manualClub = selectedClub.getName();
            new LoginManager().storeClub(selectedClub.getName());
            Intent target = new Intent(this, HomeActivity.class);
            startActivity(target);
        }

    }

    public void reset(View view) {
        selectedClub = null;
        MyApplication.manualClub = null;
        Toast.makeText(this,
                "Der Verein wurde auf 'automatische Ermittlung' zurückgesetzt",
                Toast.LENGTH_SHORT).show();
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(MyApplication.getAppContext());
        adapter.storeClub("");
        Intent target = new Intent(this, HomeActivity.class);
        startActivity(target);
    }
}
