package com.jmelzer.myttr.logic;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;

/**
 */
public class BaseTestCase extends TestCase {

    protected void login() throws IOException {

        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("-", "-"));
    }
}
