package com.expense.manager.e2e.steps;
import com.expense.manager.e2e.context.TestContext;
import com.expense.manager.e2e.hooks.Hooks;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.description;

import io.cucumber.java.After;
import org.openqa.selenium.WebDriver;

import com.expense.manager.e2e.pages.EmployeeDashboardPage;
import com.expense.manager.e2e.pages.LoginPage;
import com.expense.manager.e2e.pages.ManagerDashboardPage;
import com.expense.manager.e2e.pages.SubmitExpensePage;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.chrome.ChromeDriver;

public class ViewPendingExpenseSteps {
    private WebDriver driver;
    private static final String LOGIN_URL = "http://localhost:5173/";
    private LoginPage loginPage;
    private EmployeeDashboardPage employeeDashboardPage;
    private SubmitExpensePage submitExpensePage;
    private ManagerDashboardPage managerDashboardPage;
    private TestContext context;

    public ViewPendingExpenseSteps(TestContext context) {
        this.context = context;
    }

    @Before(order = 1)
    public void setUpPages() {
        driver = Hooks.driver;

        loginPage = new LoginPage(driver);
        employeeDashboardPage = new EmployeeDashboardPage(driver);
        submitExpensePage = new SubmitExpensePage(driver);
        managerDashboardPage = new ManagerDashboardPage(driver);
    }

    @After(order = 1)
    public void tearDown() {
        // Driver is cleaned up by Hooks
    }

    @Given("an employee is logged in")
    public void an_employee_is_logged_in() {
        driver.get(LOGIN_URL);
        loginPage.login("alice", "password123");
    }
    @When("the employee submits a new expense")
    public void the_employee_submits_a_new_expense() {
        employeeDashboardPage.clickSubmitNewExpense();

        String expenseDescription = "E2E_" + System.currentTimeMillis();

        context.setExpenseDescription(expenseDescription);

        submitExpensePage.submitExpense(
            "50.00",
            expenseDescription ,
            "2026-07-27"
        );
    }
    @When("the employee logs out")
    public void the_employee_logs_out() {
        employeeDashboardPage.logout();
    }
    @When("the manager logs in")
    public void the_manager_logs_in() {
        loginPage.login("manager", "admin123");
    }
    @When("the manager navigates to the pending expenses page")
    public void the_manager_navigates_to_the_pending_expenses_page() {
        managerDashboardPage.clickFilterByPending();
    }
    @Then("the newly submitted expense should appear in the pending expenses list")
    public void the_newly_submitted_expense_should_appear_in_the_pending_expenses_list() {
        assertTrue(managerDashboardPage.findExpense(context.getExpenseDescription()));
        assertTrue(managerDashboardPage.verifyExpenseStatus(context.getExpenseDescription(), "PENDING"));
    }
}
