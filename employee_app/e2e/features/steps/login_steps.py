from behave import given, when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


@given("the app is launched")
def step_launch_app(context):
    context.driver.get(context.base_url)


@when('I enter username "{username}" and password "{password}"')
def step_enter_credentials(context, username, password):
    context.driver.find_element(By.ID, "username").send_keys(username)
    context.driver.find_element(By.ID, "password").send_keys(password)


@when("I click the login button")
def step_click_login(context):
    context.driver.find_element(
        By.CSS_SELECTOR, "button[type='submit']"
    ).click()


@then("the employee dashboard is displayed")
def step_dashboard_displayed(context):
    WebDriverWait(context.driver, 10).until(
        EC.text_to_be_present_in_element(
            (By.TAG_NAME, "h1"), "Employee Expense Portal"
        )
    )


@then("an error message is displayed")
def step_error_displayed(context):
    WebDriverWait(context.driver, 10).until(
        EC.text_to_be_present_in_element(
            (By.TAG_NAME, "form"), "Username or password not valid."
        )
    )
