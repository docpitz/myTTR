package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 21.02.2015.
 * Shows the statistic of the mannschaft in a season.
 */
public class LigaMannschaftBilanzActivity extends BaseActivity {


    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.selectedMannschaft != null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.liga_mannschaft_bilanz);

        Mannschaft mannschaft = MyApplication.selectedMannschaft;

        TextView textView = (TextView) findViewById(R.id.textViewHeader);
        textView.setText("Bilanzen für die Mannschaft " + mannschaft.getName());

        List<String> groupList = new ArrayList<>();
        List<Mannschaft.SpielerBilanz> children = new ArrayList<>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);

        for (Mannschaft.SpielerBilanz spielerBilanz : mannschaft.getSpielerBilanzen()) {
            groupList.add(spielerBilanz.getPos() + " "  + spielerBilanz.getName());

        }
        children.addAll(mannschaft.getSpielerBilanzen());
        listView.setAdapter(new BilanzAdapter(this, groupList, children));
    }


    class BilanzAdapter extends BaseExpandableListAdapter {
        LayoutInflater layInflator;
        List<String> groupList;
        List<Mannschaft.SpielerBilanz> children;
        Context context;

        BilanzAdapter(Context context, List<String> groupList, List<Mannschaft.SpielerBilanz> children) {
            this.context = context;
            this.groupList = groupList;
            this.children = children;
            layInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                convertView = layInflator.inflate(R.layout.liga_spieler_result_header, null);
            }
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.groupName);
            lblListHeader.setText(headerTitle);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Mannschaft.SpielerBilanz childElem = (Mannschaft.SpielerBilanz) getChild(groupPosition, childPosition);
            //we can not reuse the view here
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.liga_mannschaft_bilanz_detail_row, null);

            TextView textView = (TextView) convertView.findViewById(R.id.einsaetze);
            textView.setText(childElem.getEinsaetze());
            List<String[]> posResults = childElem.getPosResults();
            TableLayout tableLayout = (TableLayout) convertView.findViewById(R.id.table);
            int i = 1;
            for (String[] posResult : posResults) {
                if (posResult != null && posResult.length == 2) {
                    TableRow row = (TableRow) layInflator.inflate(R.layout.tv_bilanz_row_template, null);
                    TextView txtView = (TextView) row.getVirtualChildAt(0);
                    txtView.setText("Position " + posResult[0] + " : ");
                    txtView = (TextView) row.getVirtualChildAt(1);
                    txtView.setText(posResult[1]);

                    tableLayout.addView(row);
                }
                i++;
            }
            textView = (TextView) convertView.findViewById(R.id.gesamt);
            textView.setText(childElem.getGesamt());

            //todo add links to spieler statistiken

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

}
