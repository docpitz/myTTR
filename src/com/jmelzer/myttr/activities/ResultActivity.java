/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 30.12.13 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class ResultActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        LinearLayout layout = (LinearLayout) findViewById(R.id.result_view);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView resultText = new TextView(this);
        resultText.setWidth(300);

        int r = MyApplication.result - MyApplication.ttrValue;

        if (MyApplication.result > MyApplication.ttrValue) {
            imageView.setImageResource(R.drawable.smileygood);
            resultText.setText("Glückwunsch, du hast " + r + " Punkte dazu gewonnen!");
        } else if (MyApplication.result < MyApplication.ttrValue) {
            imageView.setImageResource(R.drawable.smileybad);
            resultText.setText("Schade, du hast " + r + " verloren!");
        } else {
            imageView.setImageResource(R.drawable.smileyok);
            resultText.setText("Deine Punkte sind gleich geblieben.");
        }
        layout.addView(imageView);
        layout.addView(resultText);

    }

}
