package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

/**
 * Created by J. Melzer on 04.03.2015.
 * Show the Detail of the mannschaft.
 */
public class LigaMannschaftInfoAction extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_info);
        final Mannschaft m = MyApplication.selectedMannschaft;
        if (m == null) {
            return;
        }
        ((TextView) findViewById(R.id.textViewMName)).setText(m.getName());
        ((TextView) findViewById(R.id.textViewKName)).setText(m.getKontakt());
        final ImageButton mailBtn = (ImageButton) findViewById(R.id.imageButton);
        mailBtn.setVisibility(m.getMailTo() != null ? View.VISIBLE : View.INVISIBLE);
        mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", m.getMailTo(), null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                startActivity(Intent.createChooser(intent, "Wähle eine Email App aus:"));
            }
        });
    }

}
