import os
import time

from behave import when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

SLOW_MO = float(os.environ.get("SLOW_MO", "0"))


def pause():
    if SLOW_MO:
        time.sleep(SLOW_MO)


@when('I delete the expense described as "{description}"')
def step_delete_expense(context, description):
    row = WebDriverWait(context.driver, 10).until(
        EC.presence_of_element_located(
            (By.XPATH, f"//tr[td[contains(text(), '{description}')]]")
        )
    )
    row.find_element(By.XPATH, ".//button[contains(text(), 'Delete')]").click()
    pause()


@when("I confirm the deletion")
def step_confirm_deletion(context):
    confirm = WebDriverWait(context.driver, 10).until(
        EC.element_to_be_clickable(
            (By.XPATH, "//div[h2[contains(text(), 'Delete Expense')]]//button[contains(text(), 'Delete')]")
        )
    )
    confirm.click()
    pause()


@then('the expense "{description}" no longer appears in my expense list')
def step_expense_gone(context, description):
    WebDriverWait(context.driver, 10).until(
        EC.invisibility_of_element_located(
            (By.XPATH, f"//td[contains(text(), '{description}')]")
        )
    )
