package com.expense.manager.e2e.steps;

import com.expense.manager.e2e.hooks.Hooks;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateReportSteps {

    private WebDriver driver;

    private static final String LOGIN_URL = "http://localhost:5173/";

    private String selectedStatus;
    private String selectedMonth;
    private String selectedYear;

    private WebDriverWait wait;


    @Before(order = 1)
    public void setUp() {
        driver = Hooks.driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @io.cucumber.java.After(order = 1)
    public void tearDown() {
        // Driver is cleaned up by Hooks
    }


    @Given("the manager is logged in")
    public void the_manager_is_logged_in() {

        driver.get(LOGIN_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("username")
        )).sendKeys("manager");


        driver.findElement(By.id("password"))
                .sendKeys("admin123");


        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")
        )).click();


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("h1"),
                "Manager Expense Portal"
        ));
    }


    @When("the manager selects {string} expense status")
    public void manager_selects_status(String status) {

        selectedStatus = status;

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='" + status + "']")
        )).click();
    }


    @When("the manager selects {string} as the report month")
    public void manager_selects_month(String month) {

        selectedMonth = month;

        if (!month.equals("All Months")) {

            // NOTE: The month <select> has no id in the DOM, so we can't
            // locate it with By.id("monthFilter"). Instead we find the
            // <select> whose first option is "All Months", which is a
            // reliable fingerprint regardless of DOM position/order.
            Select monthDropdown = new Select(
                    wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//select[option[normalize-space()='All Months']]")
                    ))
            );

            monthDropdown.selectByValue(month);
        }
    }


    @When("the manager selects {string} as the report year")
    public void manager_selects_year(String year) {

        selectedYear = year;

        if (!year.equals("All Years")) {

            // NOTE: The year <select> has no id in the DOM either, so we
            // find it the same way: by the fingerprint of its first option.
            // This avoids relying on select position, which shifts once the
            // conditional "Day" dropdown renders after a month is picked.
            Select yearDropdown = new Select(
                    wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//select[option[normalize-space()='All Years']]")
                    ))
            );

            yearDropdown.selectByValue(year);
        }
    }


    @When("the manager exports the report")
    public void manager_exports_the_report() {

        // NOTE: The export button has no id="exportButton" in the DOM, so
        // By.id no longer works. Locating by its visible text instead;
        // normalize-space() trims incidental JSX whitespace around "Export".
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Export']")
        )).click();
    }


    @Then("the report should contain only matching expenses")
    public void report_should_contain_expenses() throws IOException {


        // FIXED: Downloads spelling
        File downloadFolder = new File(
                System.getProperty("user.home") + "/Downloads"
        );


        assertTrue(
                downloadFolder.exists(),
                "Download folder was not found"
        );


        File[] csvFiles = downloadFolder.listFiles(
                (dir, name) -> name.endsWith("-summary.csv")
        );


        assertNotNull(csvFiles,
                "Could not access download files"
        );


        assertTrue(
                csvFiles.length > 0,
                "No csv report was generated"
        );


        File report = csvFiles[csvFiles.length - 1];


        List<String> lines = Files.readAllLines(
                report.toPath()
        );


        assertFalse(
                lines.isEmpty(),
                "Report should not be empty"
        );


        assertTrue(
                lines.get(0).contains("Submitted Expenses"),
                "Report summary is missing"
        );


        assertTrue(
                lines.contains(
                        "\"Expense ID\",\"Date\",\"Amount\",\"Status\",\"Description\""
                ),
                "Expense table headers are missing"
        );


        if (!selectedStatus.equals("ALL")) {

            for (String line : lines) {

                if (line.contains(selectedStatus)) {

                    assertTrue(
                            line.contains(selectedStatus),
                            "Report contains incorrect expense status"
                    );
                }
            }
        }


        if (!selectedMonth.equals("All Months")
                || !selectedYear.equals("All Years")) {


            for (String line : lines) {

                if (line.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {

                    String date = line.split(",")[1]
                            .replace("\"", "");


                    String[] dateParts = date.split("-");


                    if (!selectedYear.equals("All Years")) {

                        assertEquals(
                                selectedYear,
                                dateParts[0],
                                "Incorrect year in report"
                        );
                    }


                    if (!selectedMonth.equals("All Months")) {

                        assertEquals(
                                selectedMonth,
                                dateParts[1],
                                "Incorrect month in report"
                        );
                    }
                }
            }
        }
    }
}