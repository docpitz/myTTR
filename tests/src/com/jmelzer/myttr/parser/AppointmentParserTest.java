/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.parser;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.logic.AppointmentParser;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;

public class AppointmentParserTest extends TestCase {

    @SmallTest
    public void testRead() throws PlayerNotWellRegistered {
        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou"));

        AppointmentParser parser= new AppointmentParser();
        List<TeamAppointment> list = parser.read("TTG St. Augustin");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            System.out.println("teamAppointment = " + teamAppointment);
        }
    }

}
