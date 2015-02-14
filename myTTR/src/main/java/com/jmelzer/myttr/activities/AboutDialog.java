package com.jmelzer.myttr.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import com.jmelzer.myttr.R;

/**
 */
public class AboutDialog extends BaseInfoDialog {

    public AboutDialog(Context context) {
        super(context);
        setTitle("Über myTTR");
    }

    /**
     * This is the standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);
        TextView tv = (TextView) findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv = (TextView) findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        tv.setLinkTextColor(Color.WHITE);
        Linkify.addLinks(tv, Linkify.ALL);
    }

}
