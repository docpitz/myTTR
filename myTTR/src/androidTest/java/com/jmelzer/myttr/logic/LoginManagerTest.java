package com.jmelzer.myttr.logic;


import com.jmelzer.myttr.activities.LoginActivity;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginManagerTest {
    boolean offline = false;

    @Test
    public void testlogin() throws Exception {
        LoginManager loginManager = new LoginManager();
        Assert.assertNotNull(loginManager.login("chokdee", "fuckyou123"));
        if (!offline) {
            try {
                loginManager.login("KKöhler", "pass123.");
                fail("ValidationException");
            } catch (ValidationException e) {
                //expexted
            }

            try {
                loginManager.login("dasdad", "sdada.");
                fail();
            } catch (LoginException e) {
                //ok
            }
        }

    }
}
