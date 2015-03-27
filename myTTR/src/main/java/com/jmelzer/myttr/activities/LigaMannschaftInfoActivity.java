package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.util.UrlUtil;

import java.util.List;

/**
 * Created by J. Melzer on 04.03.2015.
 * Show the Detail of the mannschaft.
 */
public class LigaMannschaftInfoActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.selectedMannschaft != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

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
        final ListView listview = (ListView) findViewById(R.id.spiellokale_row);
        final SpiellokalAdapter adapter = new SpiellokalAdapter(this,
                android.R.layout.simple_list_item_1,
                m.getSpielLokale());
        listview.setAdapter(adapter);
    }

    private static class ViewHolder {
        ImageView arrow;
        TextView text;
    }

    class SpiellokalAdapter extends ArrayAdapter<String> {
        private LayoutInflater layoutInflater;

        public SpiellokalAdapter(Context context, int resource, List<String> list) {
            super(context, resource, list);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.liga_spiellokal_row, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.name);
                holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String s = getItem(position);

            holder.text.setText(s);

            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMap(s);
                }
            });
            return convertView;
        }
    }

    public void showMap(String lokal) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway%2C+CA");
//        intent.setData(uri);
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(UrlUtil.formatAddressToGoogleMaps(lokal)));
        startActivity(intent);
    }
}
