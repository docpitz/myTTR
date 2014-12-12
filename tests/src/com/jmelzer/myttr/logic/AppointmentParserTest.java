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
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;

public class AppointmentParserTest extends TestCase {

    @SmallTest
    public void testRead() throws PlayerNotWellRegistered, IOException {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        AppointmentParser parser= new AppointmentParser();
        List<TeamAppointment> list = parser.read("TTG St. Augustin");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
        }
    }

}
