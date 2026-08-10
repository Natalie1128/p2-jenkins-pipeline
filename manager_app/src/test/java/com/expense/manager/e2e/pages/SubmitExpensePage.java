package com.expense.manager.e2e.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SubmitExpensePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SubmitExpensePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterAmount(String amount) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("amount")
        )).sendKeys(amount);
    }

    public void enterDescription(String description) {
        driver.findElement(By.id("description")).sendKeys(description);
    }

    public void enterDate(String date) {
        // Converts yyyy-MM-dd -> MMddyyyy
        String[] parts = date.split("-");
        String formattedDate = parts[1] + parts[2] + parts[0];

        driver.findElement(By.id("date")).sendKeys(formattedDate);
    }

    public void submitExpense() {
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    public boolean expenseAppears(String description) {
        return wait.until(
                ExpectedConditions.textToBePresentInElementLocated(
                        By.tagName("body"),
                        description
                )
        );
    }

    public void submitExpense(String amount, String description, String date) {
        enterAmount(amount);
        enterDescription(description);
        enterDate(date);
        submitExpense();
    }
}
