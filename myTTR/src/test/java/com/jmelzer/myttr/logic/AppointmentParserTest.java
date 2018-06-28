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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class AppointmentParserTest {

    @Test
    public void testParse() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {


        AppointmentParser parser = new AppointmentParser();
        String page = readFile("assets/index_e1.html");
        List<TeamAppointment> list = parser.parse(page, "TSV Krähenwinkel-Kaltenweide");

        assertEquals(3, list.size());
        assertNull("Heimspiel not found", list.get(0).isPlayAway());
        assertEquals("TTC Vinnhorst", list.get(0).getTeam2());
        assertEquals("24.01 11:00 Uhr", list.get(0).getDate());

        for (TeamAppointment teamAppointment : list) {
            System.out.println("teamAppointment = " + teamAppointment);
        }
    }

    @Test
    public void testParseStefanKoehler() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {


        AppointmentParser parser = new AppointmentParser();
        String page = readFile("assets/Stefan_Koehler_myTT.html");
        List<TeamAppointment> list = parser.parse(page, "SC 1904 Nürnberg e.V.");

        assertTrue(list.size() > 0);
        for (TeamAppointment teamAppointment : list) {
            System.out.println("teamAppointment = " + teamAppointment);
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

    @Test
    public void testParseNoAppointments() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {

        AppointmentParser parser = new AppointmentParser();
        String page = readFile("assets/no_appointment.html");
        List<TeamAppointment> list = parser.parse(page, "TTG St. Augustin");

        assertEquals(0, list.size());
    }

    @Test
    public void testParseSuccess() throws PlayerNotWellRegistered, IOException, NetworkException, LoginExpiredException {

        AppointmentParser parser = new AppointmentParser();
        String page = readFile("assets/appointment_working.html");
        List<TeamAppointment> list = parser.parse(page, "TTG St. Augustin");

        assertEquals(3, list.size());
        assertFalse(list.get(0).isPlayAway());
        assertTrue(list.get(0).isFoundTeam());
        assertFalse(list.get(0).getId1().equals(list.get(0).getId2()));
        assertEquals("Spfr. Leverkusen II", list.get(0).getTeam2());
        assertEquals("19.09 18:30 Uhr", list.get(0).getDate());

        assertTrue(list.get(1).isPlayAway());
        assertEquals("CTTF Bonn", list.get(1).getTeam1());
        assertEquals("25.09 19:30 Uhr", list.get(1).getDate());

        assertFalse(list.get(2).isPlayAway());
        assertEquals("TV Bergheim II", list.get(2).getTeam2());
        assertEquals("24.10 18:30 Uhr", list.get(2).getDate());

    }
}
