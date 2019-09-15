package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.provider.CalendarContract;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Adapter for the tabs in liga result.
 */
public class EventsTabsPagerAdapter extends FragmentPagerAdapter {
    List<CalendarContract.Events> events;
    Activity parentActivity;

    public EventsTabsPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        parentActivity = activity;
    }

    @Override
    public Fragment getItem(int index) {
        if (index == 1) {
            return new EventsTTRChartFragment();
        } else {
            return new EventsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
