/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */


package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.SyncManager;

public class EnterTimeActivity extends BaseActivity {

    String[] texts = new String[]{
            "Alle 5 Minuten überprüfen",
            "Alle 10 Minuten überprüfen",
            "Alle 30 Minuten überprüfen",
            "Alle 60 Minuten überprüfen",
            "Täglich überprüfen",
            "Wöchentlich überprüfen",
            "Nie überprüfen"};



    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return !MyApplication.userIsEmpty();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }
        final MyApplication app = ((MyApplication)getApplication());
        int idx = app.getTimerSetting();
        if (idx > texts.length-1)
            idx = texts.length-1;

        setContentView(R.layout.enter_timer);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        final TextView textView = (TextView) findViewById(R.id.textView1);
        seekBar.setProgress(idx);
        seekBar.setMax(texts.length - 1);
        textView.setText(texts[idx]);
        seekBar.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        textView.setText(texts[progresValue]);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,

                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        app.storeTimerSetting(seekBar.getProgress());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //restartTime
        stopService(new Intent(this, SyncManager.class));
        startService(new Intent(this, SyncManager.class));
    }
}
