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
import android.os.Bundle;
import android.widget.EditText;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class PlayerDetailActivity extends Activity {
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
}
