package com.jmelzer.myttr.logic;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.TeamAppointment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import static com.jmelzer.myttr.logic.TestUtil.readFile;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class AppointmentParserTest {

    @Test
    public void testParse() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {


        AppointmentParser parser = new AppointmentParser();
        String page = readFile("assets/index_e1.html");
        List<TeamAppointment> list = parser.parse(page, "TSV Krähenwinkel-Kaltenweide");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            Log.d(Constants.LOG_TAG, "teamAppointment = " + teamAppointment);
        }
    }

    @Test
    public void testParseForumError() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {


        AppointmentParser parser = new AppointmentParser();
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
