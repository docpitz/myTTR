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

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.SyncManager;

/**
 * will be aclled after the user press the notification for new points.
 */
public class NewPointsActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        int n = SyncManager.newTTRPoints;

        setContentView(R.layout.newpointsreceived);

        LinearLayout layout = (LinearLayout) findViewById(R.id.newpointsview);
        TextView resultText = new TextView(this);
        resultText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        resultText.setWidth(300);

        Log.i(Constants.LOG_TAG, "new points received " + n);

        String text = "";

        int r = n - MyApplication.getPoints();
        Log.i(Constants.LOG_TAG, "difference " + r);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (r > 0) {
            imageView.setImageResource(R.drawable.smileygood);
            text += "Gl\u00FCckwunsch, du hast " + r + " Punkte dazu gewonnen!";
        } else {
            imageView.setImageResource(R.drawable.smileybad);
            text += "Schade, du hast " + r + " verloren!";
        }

        resultText.setText(text);
        layout.addView(imageView);
        layout.addView(resultText);
        //reset
        SyncManager.notifcationSent = false;
//        MyTischtennisParser.debugCounter = 0;
        MyApplication.getLoginUser().setPoints(n);
    }
}
