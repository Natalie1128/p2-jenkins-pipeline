package com.expense.manager.e2e.hooks;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    public static WebDriver driver;

    @Before(order = 0)
    public void setup() throws java.net.MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        if ("true".equals(System.getenv("HEADLESS"))) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
        }
        String seleniumUrl = System.getenv("SELENIUM_URL");
        if (seleniumUrl != null) {
            driver = new RemoteWebDriver(new URL(seleniumUrl), options);
        } else {
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
    }

    @After(order = 0)
    public void teardown() {
        if(driver != null) {
            driver.quit();
            driver = null;
        }
    }
}