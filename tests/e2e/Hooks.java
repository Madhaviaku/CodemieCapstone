package com.library.catalog.e2e;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void startBrowser() {
        DriverFactory.start();
    }

    @After
    public void closeBrowser() {
        DriverFactory.stop();
    }
}
