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

import androidx.test.filters.SmallTest;

public class LoginManagerTest extends BaseTestCase {

    @SmallTest
    public void testlogin() throws Exception {
        LoginManager loginManager = new LoginManager();
        Assert.assertNotNull(loginManager.login("chokdee", "fuckyou123"));
        if (!offline) {
            try {
                loginManager.login("KKöhler", "pass123.");
                fail("PlayerNotWellRegistered");
            } catch (PlayerNotWellRegistered e) {
                //expexted
            }

            assertNull(loginManager.login("dasdad", "sdada."));
        }

    }
}
