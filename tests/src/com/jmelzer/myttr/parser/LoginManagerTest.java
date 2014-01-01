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
import junit.framework.Assert;
import junit.framework.TestCase;

public class LoginManagerTest extends TestCase {

    @SmallTest
    public void testlogin() {
        LoginManager loginManager = new LoginManager();
        Assert.assertFalse(loginManager.login("error", "error"));
        Assert.assertTrue(loginManager.login("myttlogin", "mytpw"));

    }
}
