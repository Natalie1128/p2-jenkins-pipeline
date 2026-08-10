from behave import when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select, WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import uuid

@when('I click "Or register"')
def step_click_or_register(context):
    context.driver.find_element(
        By.XPATH,
        "//a[text()='Or register']"
    ).click()


@then('I am directed to the register page')
def step_directed_to_register_page(context):
    WebDriverWait(context.driver, 10).until(
        EC.visibility_of_element_located(
            (By.XPATH, "//h2[text()='Create an account']")
        )
    )


@when('I enter a unique generated username and password "{password}"')
def step_enter_credentials(context, password):
    context.username = f"testuser_{uuid.uuid4().hex[:8]}"

    context.driver.find_element(
        By.ID,
        "username"
    ).send_keys(context.username)

    context.driver.find_element(
        By.ID,
        "password"
    ).send_keys(password)


@when('I select the role "Employee"')
def step_select_role_employee(context):
    role_dropdown = Select(
        context.driver.find_element(
            By.XPATH,
            "//select[@id='role']"
        )
    )

    role_dropdown.select_by_visible_text("Employee")


@when('I click the register button')
def step_click_register_button(context):
    context.driver.find_element(
        By.XPATH,
        "//button[text()='Register']"
    ).click()


@then('a register success message is displayed')
def step_success_message(context):
    WebDriverWait(context.driver, 10).until(
        EC.visibility_of_element_located(
            (By.XPATH, "//div[@id='success-msg']")
        )
    )

@then('a register error message is displayed')
def step_error_message(context):
    WebDriverWait(context.driver, 10).until(
        EC.visibility_of_element_located(
            (By.XPATH, "//div[@id='error-msg']")
        )
    )
