package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.jmelzer.myttr.R;

/**
 */
public class VersionCheckDialog extends BaseInfoDialog {

    public VersionCheckDialog(Context context) {
        super(context);
    }

    /**
     * This is the standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.versions_check);


    }

}
