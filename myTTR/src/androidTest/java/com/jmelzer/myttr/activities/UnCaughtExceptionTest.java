package com.jmelzer.myttr.activities;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.jmelzer.myttr.Constants;

/**
 * Created by J. Melzer on 20.05.2015.
 *
 */
public class UnCaughtExceptionTest extends InstrumentationTestCase {


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
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException());

        testThread.start();
        testThread.join();


    }
}