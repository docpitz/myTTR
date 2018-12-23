package com.jmelzer.myttr.activities;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CalendarExportIT {

    @Rule
    public ActivityTestRule<CalendarExportActivity> mActivityRule = new ActivityTestRule<>(
            CalendarExportActivity.class);

    @Test
    public void test() {

    }
}
