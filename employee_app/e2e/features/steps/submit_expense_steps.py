from behave import when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


@when("I click the submit new expense button")
def step_open_submit_form(context):
    button = WebDriverWait(context.driver, 10).until(
        EC.element_to_be_clickable(
            (By.XPATH, "//button[contains(text(), 'Submit New Expense')]")
        )
    )
    button.click()


@when('I enter amount "{amount}" and description "{description}" dated "{date}"')
def step_fill_expense_form(context, amount, description, date):
    context.expense_description = description

    context.driver.find_element(By.ID, "amount").send_keys(amount)
    context.driver.find_element(By.ID, "description").send_keys(description)

    year, month, day = date.split("-")
    context.driver.find_element(By.ID, "date").send_keys(month + day + year)


@when("I click the submit expense button")
def step_submit_expense(context):
    context.driver.find_element(
        By.XPATH, "//button[@type='submit']"
    ).click()


@then("the expense appears in my expense list")
def step_expense_appears(context):
    WebDriverWait(context.driver, 10).until(
        EC.text_to_be_present_in_element(
            (By.TAG_NAME, "body"), context.expense_description
        )
    )
