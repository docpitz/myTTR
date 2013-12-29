/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 29.12.13 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.parser.TTRPointParser;
import com.jmelzer.myttr.parser.ClubParser;

public class PlayerDetailActivity extends Activity {

    ClubParser clubParser = new ClubParser();
    TTRPointParser ttrPointParser = new TTRPointParser();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_detail);

        EditText editText = (EditText) findViewById(R.id.detail_firstname);
        editText.setText(MyApplication.actualPlayer.getFirstname());
        editText = (EditText) findViewById(R.id.detail_lastname);
        editText.setText(MyApplication.actualPlayer.getLastname());
        editText = (EditText) findViewById(R.id.detail_club);
        editText.setText(MyApplication.actualPlayer.getClub());
    }

    public void search(final View view) {
        String club = ((EditText) findViewById(R.id.detail_club)).getText().toString();
        Club verein = clubParser.getClubExact(club);
        if (verein == null) {
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
        }
    }
}
