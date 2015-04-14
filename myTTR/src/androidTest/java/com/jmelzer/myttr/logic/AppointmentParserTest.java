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
import com.jmelzer.myttr.TeamAppointment;

import java.io.IOException;
import java.util.List;

public class AppointmentParserTest extends BaseTestCase {

    @SmallTest
    public void testRead() throws Exception {

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

    @SmallTest
    public void testParseForumError() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {


        AppointmentParser parser= new AppointmentParser();
        String page = readFile("assets/appointment_error.html");
        List<TeamAppointment> list = parser.parse(page, "TSV RW Auerbach");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
        }
    }

    @SmallTest
    public void testParseNoAppointments() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {

        AppointmentParser parser = new AppointmentParser();
        String page = readFile("assets/no_appointment.html");
        List<TeamAppointment> list = parser.parse(page, "TTG St. Augustin");

        assertEquals(0, list.size());
    }
}


