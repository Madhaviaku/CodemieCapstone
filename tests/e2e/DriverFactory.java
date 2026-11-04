package com.library.catalog.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

public final class DriverFactory {

    private static WebDriver driver;

    private DriverFactory() {
    }

    public static void start() {
        if (driver != null) {
            return;
        }
        driver = createDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    }

    public static WebDriver driver() {
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been started.");
        }
        return driver;
    }

    public static void stop() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static WebDriver createDriver() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        String browser = System.getProperty("browser", "chrome");
        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1440,1100", "--disable-gpu");
            if (headless) {
                options.addArguments("--headless=new");
            }
            return new ChromeDriver(options);
        }
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--window-size=1440,1100", "--disable-gpu");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return new EdgeDriver(options);
    }
}
