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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.parser.TTRCalculator;

public class ManualEntriesActivity extends Activity {

    TTRCalculator calculator = new TTRCalculator();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_entries);

        LinearLayout layout = (LinearLayout) findViewById(R.id.manual_entries_view);

        //todo refactor to listview
        int i = 0;
        for (final Player player : MyApplication.players) {

            View ruler = new View(this);
            ruler.setBackgroundColor(Color.WHITE);
            layout.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

            LinearLayout layoutH = new LinearLayout(ManualEntriesActivity.this);
            layoutH.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvPlayer = new TextView(this);
            tvPlayer.setText(player.nameAndClub());
            tvPlayer.setWidth(300);
//            tvPlayer.setHeight(150);
            layoutH.addView(tvPlayer);

            EditText ttrText = new EditText(this);
            ttrText.setText("" + player.getTtrPoints());
            layoutH.addView(ttrText);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(i);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MyApplication.players.get(buttonView.getId()).setChecked(isChecked);
                }
            });
            layoutH.addView(checkBox);

            ImageButton button = new ImageButton(this);
//            button.setText(R.string.btn_edit_player);
            button.setImageResource(R.drawable.edit);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(30, 0, 0, 0);
            button.setLayoutParams(layoutParams);
//            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent target = new Intent(ManualEntriesActivity.this, PlayerDetailActivity.class);
                    MyApplication.actualPlayer = player;
                    startActivity(target);
                }
            });

            //todo add delete btn

            layoutH.addView(button);
            layout.addView(layoutH);
            i++;

        }

        Button buttonCalc = new Button(this);
        buttonCalc.setText("Berechnen");
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newV = MyApplication.ttrValue;
                System.out.println("newV = " + newV);
                for (Player player : MyApplication.players) {
                    newV += calculator.calcPoints(newV, player.getTtrPoints(), player.isChecked());
//                    System.out.println("newV = " + newV);
                }
                MyApplication.result = newV;
                Intent target = new Intent(ManualEntriesActivity.this, ResultActivity.class);
                startActivity(target);
            }
        });
        layout.addView(buttonCalc);
    }

    public void newplayer(final View view) {
        Intent target = new Intent(this, PlayerDetailActivity.class);
        MyApplication.actualPlayer = new Player();
        MyApplication.players.add(MyApplication.actualPlayer);
        startActivity(target);
    }
}
