/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.logic;


import junit.framework.Assert;

import java.io.IOException;

import androidx.test.filters.SmallTest;

public class LoginManagerTest extends BaseTestCase {

    @SmallTest
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
