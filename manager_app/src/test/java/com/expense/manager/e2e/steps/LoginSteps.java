package com.expense.manager.e2e.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.expense.manager.e2e.hooks.Hooks;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.expense.manager.e2e.pages.LoginPage;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {
    private WebDriver driver;
    private static final String LOGIN_URL = "http://localhost:5173/";
    private LoginPage loginPage;

    @Before(order = 1)
    public void setUpPages() {
        driver = Hooks.driver;
        loginPage = new LoginPage(driver);
    }

    @After(order = 1)
    public void tearDown() {
        // Driver is cleaned up by Hooks
    }

    @Given("the user is on the manager login page")
    public void the_user_is_on_the_manager_login_page() {
        driver.get(LOGIN_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        WebElement usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );

        assertTrue(usernameField.isDisplayed());
    }
    @When("the user enters username {string}")
    public void the_user_enters_username(String username) {
        loginPage.enterUsername(username);
    }
    @When("the user enters password {string}")
    public void the_user_enters_password(String password) {
        loginPage.enterPassword(password);
    }
    @When("the user clicks the login button")
    public void the_user_clicks_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("the login result should be {string}")
    public void the_login_result_should_be(String result) {
        if (result.equals("success")) {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/manager"));

            assertTrue(driver.getCurrentUrl().contains("/manager"));
        } else {
            WebElement error = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("p.text-red-600")));

            assertEquals("Username or password not valid.", error.getText());

            // Optional: verify failed login stays on login page
            assertTrue(driver.getCurrentUrl().equals(LOGIN_URL));
        }
    }
}
