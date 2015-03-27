/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class ResultActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return !MyApplication.userIsEmpty();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.result);
        setTitle(MyApplication.getTitle());

        LinearLayout layout = (LinearLayout) findViewById(R.id.result_view);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView resultText = new TextView(this);
        resultText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        resultText.setWidth(300);

        int r = MyApplication.calcActualDiff();
        String text = "";

        if (r > 0) {
            imageView.setImageResource(R.drawable.smileygood);
            text += "Gl\u00FCckwunsch, du hast " + r + " Punkte dazu gewonnen!";
        } else if (r < 0) {
            imageView.setImageResource(R.drawable.smileybad);
            text += "Schade, du hast " + r + " verloren!";
        } else {
            imageView.setImageResource(R.drawable.smileyok);
            text += "Deine Punkte sind gleich geblieben.";
        }
        text += "\nDein neuer TTR Wert ist: " + MyApplication.result;
        resultText.setText(text);
        layout.addView(imageView);
        layout.addView(resultText);

    }

}
