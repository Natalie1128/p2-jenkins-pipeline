from behave import given, when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select, WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import uuid

@when('I register a new manager with username "{username}" and password "{password}"')
def step_register_new_manager(context, username, password):
    if username.lower() in ["new_manager", "unique", "generated"]:
        context.username = f"manager_{uuid.uuid4().hex[:8]}"
    else:
        context.username = username
    context.driver.find_element(
        By.ID,
        "username"
    ).send_keys(context.username)

    context.driver.find_element(
         By.ID,
         "password"
    ).send_keys(password)


@when('I select the role "Manager"')
def step_select_role_employee(context):
    role_dropdown = Select(
        context.driver.find_element(
            By.XPATH,
            "//select[@id='role']"
        )
    )

    role_dropdown.select_by_visible_text("Manager")


