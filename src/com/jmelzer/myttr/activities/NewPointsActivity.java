/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */

/*
* Author: J. Melzer
* Date: 08.03.14 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;

/**
 * will be aclled after the user press the notification for new points.
 */
public class NewPointsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpointsreceived);

        LinearLayout layout = (LinearLayout) findViewById(R.id.newpointsview);
        TextView resultText = new TextView(this);
        resultText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        resultText.setWidth(300);

        int n = getIntent().getIntExtra("points", 0);

        String text = "Dein neuer TTR Wert ist: " + n;
        resultText.setText(text);
        layout.addView(resultText);
    }
}
