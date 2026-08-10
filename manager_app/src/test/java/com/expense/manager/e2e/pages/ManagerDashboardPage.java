package com.expense.manager.e2e.pages;

import static org.mockito.Mockito.description;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ManagerDashboardPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ManagerDashboardPage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickFilterByPending() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'PENDING')]")
        )).click();
    }

    public boolean findExpense(String expenseDescription){
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//table/tbody/tr/td[4]")
        ));

        List<WebElement> descriptions = driver.findElements(
            By.xpath("//table/tbody/tr/td[4]")
        );

        for (WebElement cell : descriptions) {
            if (cell.getText().equals(expenseDescription)) {
                return true;
            }
        }

        return false;
    }    

    public void clickFilterByApproved() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'APPROVED')]")
        )).click();
    }

    public void clickFilterByDenied() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'DENIED')]")
        )).click();
    }
    
    public void reviewExpense(String expenseDescription) {
        WebElement expenseRow = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table/tbody/tr[td[4]='" + expenseDescription + "']")
            )
        );

        WebElement reviewButton = expenseRow.findElement(
            By.xpath(".//button[contains(text(),'Review')]")
        );

        wait.until(
            ExpectedConditions.elementToBeClickable(reviewButton)
        ).click();
    }

    public void clickApprove() {
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Approve')]")
            )
        ).click();
    }

    public void clickDeny() {
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Deny')]")
            )
        ).click();
    }

    public boolean verifyExpenseStatus(String expenseDescription, String expectedStatus) {
        By statusLocator = By.xpath(
            "//table/tbody/tr[td[4]='" + expenseDescription + "']/td[6]"
        );

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            statusLocator,
            expectedStatus
        ));

        return driver.findElement(statusLocator)
                    .getText()
                    .equals(expectedStatus);
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Logout')]")
        )).click();
    }

    public void addComment(String comment) {
        WebElement commentBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("decision-comment")
            )
        );

        commentBox.sendKeys(comment);
    }
}
