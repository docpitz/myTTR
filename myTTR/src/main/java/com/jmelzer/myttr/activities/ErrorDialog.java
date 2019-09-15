package com.jmelzer.myttr.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jmelzer.myttr.R;

/**
 * Created by cicgfp on 07.01.2018.
 */

public class ErrorDialog extends Dialog {
    String msg;
    String url;
    int duration = -1;

    public ErrorDialog(@NonNull Context context, String msg, int duration) {
        super(context);
        setTitle("myTTR ");
        this.msg = msg;
        this.duration = duration;
    }

    public ErrorDialog(@NonNull Context context, String msg, String url) {
        super(context);
        setTitle("myTTR Fehler");
        this.msg = msg;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.error_dlg);

        TextView tv = findViewById(R.id.content_text);
        tv.setText(msg);

        Button ok = findViewById(R.id.buttonOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button buttonVisit = findViewById(R.id.buttonVisit);
        buttonVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(browserIntent);
            }
        });

        if (duration > 0) {
            buttonVisit.setVisibility(View.INVISIBLE);
            ok.setVisibility(View.INVISIBLE);
//            try {
//                Thread.sleep(duration);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            this.dismiss();

        }
    }
}
