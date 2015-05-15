package com.jmelzer.myttr.activities;

import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.Mannschaft;
import com.jmelzer.myttr.MyApplication;

import java.util.List;

/**
 * Adapter for the tabs in liga result.
 */
public class EventsTabsPagerAdapter extends FragmentPagerAdapter {
    List<CalendarContract.Events> events;

    public EventsTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        EventsTTRChartFragment f = new EventsTTRChartFragment();

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        if (MyApplication.getSelectedLiga().getUrlGesamt() != null) {
            return 1;
        }
        return 2;
    }

}
