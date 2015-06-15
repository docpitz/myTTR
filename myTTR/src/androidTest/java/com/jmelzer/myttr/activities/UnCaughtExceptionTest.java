package com.jmelzer.myttr.activities;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.jmelzer.myttr.Constants;

/**
 * Created by J. Melzer on 20.05.2015.
 */
public class UnCaughtExceptionTest extends InstrumentationTestCase {

    public void testAddInformation() throws Exception {
        StringBuilder builder = new StringBuilder();
        new UnCaughtException(getInstrumentation().getContext()).addInformation(builder);
        assertTrue(builder.toString().contains("Device"));
        Log.d(Constants.LOG_TAG, builder.toString());
    }

    public void testANR() throws Exception {
//        Thread.setDefaultUncaughtExceptionHandler(null);
//
//        if (true) {
//            throw new NullPointerException();
//        }
        Thread testThread = new Thread() {
            public void run() {
                throw new RuntimeException("Expected!");
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(getInstrumentation().getContext()));

        testThread.start();
        testThread.join();


    }
}