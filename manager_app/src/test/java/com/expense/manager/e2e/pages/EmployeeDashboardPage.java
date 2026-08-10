package com.expense.manager.e2e.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EmployeeDashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public EmployeeDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickSubmitNewExpense() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Submit New Expense')]")
        )).click();
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Logout')]")
        )).click();
    }

    public void clickHistory() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'History')]")
        )).click();
    }

    public boolean verifyExpenseStatus(String expenseDescription, String expectedStatus) {
        WebElement expenseRow = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table/tbody/tr[td[3]='" + expenseDescription + "']")
            )
        );
        String status = expenseRow.findElement(
            By.xpath("./td[5]")
        ).getText();

        return status.equals(expectedStatus);
    }

    public void reviewExpense(String expenseDescription) {
        WebElement expenseRow = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table/tbody/tr[td[3]='" + expenseDescription + "']")
            )
        );

        WebElement reviewButton = expenseRow.findElement(
            By.xpath(".//button[contains(text(),'Review')]")
        );

        wait.until(ExpectedConditions.elementToBeClickable(reviewButton)).click();
    }

    public String getReviewComment(String expenseDescription) {
        // reviewExpense(expenseDescription);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//p[text()='Review Comment']/following-sibling::div")
        )).getText();
    }

    public boolean verifyComment(String expenseDescription, String expectedComment){
        return getReviewComment(expenseDescription).equals(expectedComment);
    }

}