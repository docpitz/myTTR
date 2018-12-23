package com.jmelzer.myttr.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jmelzer.myttr.R;

public class CalendarSelectionDialog extends Dialog {

    public CalendarSelectionDialog(Context context) {
        super(context);
        setTitle("Kalender auswählen");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);
    }
}
