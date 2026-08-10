package com.expense.manager.e2e.steps;

import org.openqa.selenium.WebDriver;

import com.expense.manager.e2e.context.TestContext;
import com.expense.manager.e2e.hooks.Hooks;
import com.expense.manager.e2e.pages.EmployeeDashboardPage;
import com.expense.manager.e2e.pages.LoginPage;
import com.expense.manager.e2e.pages.ManagerDashboardPage;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.chrome.ChromeDriver;

public class ApproveExpenseSteps {
    private WebDriver driver;
    private LoginPage loginPage;
    private EmployeeDashboardPage employeeDashboardPage;
    private ManagerDashboardPage managerDashboardPage;
    private TestContext context;

    public ApproveExpenseSteps(TestContext context) {
        this.context = context;
    }

    @Before(order = 1)
    public void setUpPages() {
        driver = Hooks.driver;

        loginPage = new LoginPage(driver);
        employeeDashboardPage = new EmployeeDashboardPage(driver);
        managerDashboardPage = new ManagerDashboardPage(driver);
    }

    @io.cucumber.java.After(order = 1)
    public void tearDown() {
        // Driver is cleaned up by Hooks
    }

    @When("the manager opens the expense for review")
    public void the_manager_opens_the_expense_for_review() {
        managerDashboardPage.reviewExpense(context.getExpenseDescription());
    }

    @When("the manager approves the expense")
    public void the_manager_approves_the_expense() {
        managerDashboardPage.clickApprove();
    }
    @Then("the expense status should be {string} on the manager page")
    public void the_expense_status_should_be_on_the_manager_page(String status) {
        managerDashboardPage.verifyExpenseStatus(context.getExpenseDescription(), status);
    }
    @When("the manager logs out")
    public void the_manager_logs_out() {
        managerDashboardPage.logout();
    }
    @When("the employee logs in")
    public void the_employee_logs_in() {
        loginPage.login("alice", "password123");
    }
    @When("the employee navigates to expense history")
    public void the_employee_navigates_to_expense_history() {
        employeeDashboardPage.clickHistory();
    }
    @Then("the expense status should be {string} on the employee history page")
    public void the_expense_status_should_be_on_the_employee_history_page(String status) {
        employeeDashboardPage.verifyExpenseStatus(context.getExpenseDescription(), status);
    }
}
