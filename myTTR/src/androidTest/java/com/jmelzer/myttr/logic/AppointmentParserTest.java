/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.TeamAppointment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

public class AppointmentParserTest extends BaseTestCase {

    @SmallTest
    public void testRead() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {

        login();

        AppointmentParser parser= new AppointmentParser();
        List<TeamAppointment> list = parser.read("TTG St. Augustin");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
        }
    }

    @SmallTest
    public void testParse() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {


        AppointmentParser parser= new AppointmentParser();
        String page = readFile("assets/index_e1.html");
        List<TeamAppointment> list = parser.parse(page, "TSV Krähenwinkel-Kaltenweide");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
        }
    }
}


