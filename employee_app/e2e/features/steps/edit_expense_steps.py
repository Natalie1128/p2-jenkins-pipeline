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


@when('I edit the expense described as "{old_description}" to amount "{amount}" description "{description}" dated "{date}"')
def step_edit_expense(context, old_description, amount, description, date):
    row = WebDriverWait(context.driver, 10).until(
        EC.presence_of_element_located(
            (By.XPATH, f"//tr[td[contains(text(), '{old_description}')]]")
        )
    )

    row.find_element(By.XPATH, ".//button[contains(text(), 'Edit')]").click()
    pause()

    amount_field = WebDriverWait(context.driver, 10).until(
        EC.presence_of_element_located((By.ID, "amount"))
    )
    amount_field.clear()
    amount_field.send_keys(amount)
    pause()

    description_field = context.driver.find_element(By.ID, "description")
    description_field.clear()
    description_field.send_keys(description)
    pause()

    year, month, day = date.split("-")
    date_field = context.driver.find_element(By.ID, "date")
    date_field.send_keys(month + day + year)
    pause()

    context.driver.find_element(By.XPATH, "//button[@type='submit']").click()


@then('the expense list shows amount "{amount}" and description "{description}" and date "{date}"')
def step_list_shows_updates(context, amount, description, date):
    wait = WebDriverWait(context.driver, 10)

    wait.until(
        EC.text_to_be_present_in_element((By.TAG_NAME, "table"), description)
    )
    wait.until(
        EC.text_to_be_present_in_element((By.TAG_NAME, "table"), amount)
    )
    wait.until(
        EC.text_to_be_present_in_element((By.TAG_NAME, "table"), date)
    )
