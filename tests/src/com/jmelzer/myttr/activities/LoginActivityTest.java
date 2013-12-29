package com.jmelzer.myttr.activities;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.jmelzer.myttr.activities.MyTTRActivityTest \
 * com.jmelzer.myttr.tests/android.test.InstrumentationTestRunner
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginActivityTest() {
        super("com.jmelzer.myttr", LoginActivity.class);
    }

}
