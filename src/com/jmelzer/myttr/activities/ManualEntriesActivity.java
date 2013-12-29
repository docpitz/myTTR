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
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;

public class ManualEntriesActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_entries);

        LinearLayout layout = (LinearLayout) findViewById(R.id.manual_entries_view);

        for (final Player player : MyApplication.players) {
            LinearLayout layoutH = new LinearLayout(ManualEntriesActivity.this);
            layoutH.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvPlayer = new TextView(this);
            tvPlayer.setText(player.visualize());
            tvPlayer.setWidth(400);
            tvPlayer.setHeight(200);

            layoutH.addView(tvPlayer);
            Button button = new Button(this);
            button.setText(R.string.btn_edit_player);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent target = new Intent(ManualEntriesActivity.this, PlayerDetailActivity.class);
                    MyApplication.actualPlayer = player;
                    startActivity(target);
                }
            });

            layoutH.addView(button);
            layout.addView(layoutH);

        }
    }
}
