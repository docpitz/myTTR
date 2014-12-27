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
public abstract class BaseInfoDialog extends Dialog {
    private static Context mContext = null;

    public BaseInfoDialog(Context context) {
        super(context);
        mContext = context;
    }


    public String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while (( line = buf.readLine()) != null) text.append(line + "\n");
        } catch (IOException e) {
            return null;
        } finally {
            try {
                in.close();
                buf.close();
            } catch (IOException e) {
            }
        }

        return text.toString();
    }
}
