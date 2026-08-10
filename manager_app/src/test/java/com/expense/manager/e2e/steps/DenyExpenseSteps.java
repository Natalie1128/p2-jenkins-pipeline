package com.expense.manager.e2e.steps;

import org.openqa.selenium.WebDriver;

import com.expense.manager.e2e.context.TestContext;
import com.expense.manager.e2e.hooks.Hooks;
import com.expense.manager.e2e.pages.ManagerDashboardPage;

import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import org.openqa.selenium.chrome.ChromeDriver;

public class DenyExpenseSteps {
    private WebDriver driver;
    private ManagerDashboardPage managerDashboardPage;
    private TestContext context;

    public DenyExpenseSteps(TestContext context) {
        this.context = context;
    }

    @Before(order = 1)
    public void setUpPages() {
        driver = Hooks.driver;
        managerDashboardPage = new ManagerDashboardPage(driver);
    }

    @io.cucumber.java.After(order = 1)
    public void tearDown() {
        // Driver is cleaned up by Hooks
    }


    @When("the manager denies the expense")
    public void the_manager_denies_the_expense() {
        managerDashboardPage.clickDeny();
    }
}
