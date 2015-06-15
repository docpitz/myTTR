package com.jmelzer.myttr.activities;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by J. Melzer on 13.05.2015.
 * Page for showing the chart
 */
public class EventsTTRChartFragment extends Fragment {
    private LineChart mChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.events_chart, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("TODO.");

        mChart.setHighlightEnabled(false);
        mChart.getAxisRight().setEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setBackgroundColor(Color.BLACK);
        mChart.getLegend().setEnabled(false);


        setData();

        TextView textView = (TextView) rootView.findViewById(R.id.selected_player);
        textView.setText(getActivity().getString(R.string.chart_text) + MyApplication.getStatistikTextForPlayer());

        return rootView;
    }

    private void setData() {
        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();
        int i = 0;
        int maxTTR = 0;
        int minTTR = 3000;
        List<Event> events = new ArrayList<>(MyApplication.events);
        Collections.reverse(events);
        for (Event event : events) {
            xVals.add((event.getDate()) + "");

            yVals.add(new Entry(event.getTtr(), i));
            maxTTR = Math.max(maxTTR, event.getTtr());
            minTTR = Math.min(minTTR, event.getTtr());
            i++;
        }
        Event lastEvent = MyApplication.events.get(0);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        try {
            Date lastDate = formatter.parse(lastEvent.getDate());
            Date p1 = DateUtils.addDays(lastDate, 1);
            xVals.add(formatter.format(p1));
            yVals.add(new Entry(lastEvent.getTtr() + lastEvent.getSum(), i));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        //add 20% of the diff to the max and min
        int diff = maxTTR - minTTR;
        diff = (int) ((float) diff * 0.2f);
        maxTTR += diff;
        minTTR -= diff;

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, MyApplication.getLoginUser().getRealName());
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(10f, 5f, 0f);
//        set1.setColor(Color.BLACK);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(2f);
//        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(0f);
//        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
        set1.setDrawCircles(false);
        set1.setColor(Color.WHITE);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(maxTTR);
        leftAxis.setAxisMinValue(minTTR);
        leftAxis.setStartAtZero(false);
        leftAxis.setTextColor(Color.WHITE);
//        mChart.getXAxis().setValues(Arrays.asList("1", "2"));
        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        leftAxis.setDrawGridLines(true);
        mChart.getAxisRight().setDrawGridLines(false);
//        mChart.getXAxis().setDrawGridLines(false);

        // set data
        mChart.setData(data);


    }
}
