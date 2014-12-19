package com.jmelzer.myttr.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import com.jmelzer.myttr.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 */
public class ImpressumDialog extends BaseInfoDialog {

    public ImpressumDialog(Context context) {
        super(context);
    }

    /**
     * This is the standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.impressum);
        TextView tv = (TextView)findViewById(R.id.impressum);
        tv.setText(readRawTextFile(R.raw.impressum));


    }

}
