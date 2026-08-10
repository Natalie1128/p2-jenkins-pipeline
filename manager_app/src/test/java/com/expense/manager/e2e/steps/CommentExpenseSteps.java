package com.expense.manager.e2e.steps;

import org.openqa.selenium.WebDriver;

import com.expense.manager.e2e.context.TestContext;
import com.expense.manager.e2e.hooks.Hooks;
import com.expense.manager.e2e.pages.EmployeeDashboardPage;
import com.expense.manager.e2e.pages.ManagerDashboardPage;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.chrome.ChromeDriver;

public class CommentExpenseSteps {
    private WebDriver driver;
    private ManagerDashboardPage managerDashboardPage;
    private EmployeeDashboardPage employeeDashboardPage;
    private TestContext context;

    public CommentExpenseSteps(TestContext context) {
        this.context = context;
    }

    @Before(order = 1)
    public void setUpPages() {
        driver = Hooks.driver;
        managerDashboardPage = new ManagerDashboardPage(driver);
        employeeDashboardPage = new EmployeeDashboardPage(driver);
    }

    @io.cucumber.java.After(order = 1)
    public void tearDown() {
        // Driver is cleaned up by Hooks
    }

    
    @When("the manager reviews the expense with comment {string}")
    public void the_manager_reviews_the_expense_with_comment(String comment) {
        managerDashboardPage.addComment(comment);
    }
    @Then("the expense review comment should be {string} on the employee history page")
    public void the_expense_review_comment_should_be_on_the_employee_history_page(String comment) {
        employeeDashboardPage.reviewExpense(context.getExpenseDescription());
        employeeDashboardPage.verifyComment(context.getExpenseDescription(), comment);
    }
}
