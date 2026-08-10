import os
import subprocess
import sys
import time

from selenium import webdriver

BASE_URL = "http://localhost:5173"
SLOW_MO = float(os.environ.get("SLOW_MO", "0"))

FEATURES_DIR = os.path.dirname(os.path.abspath(__file__))
EMPLOYEE_APP_DIR = os.path.dirname(os.path.dirname(FEATURES_DIR))
REPO_ROOT = os.path.dirname(EMPLOYEE_APP_DIR)
DB_DIR = os.path.join(EMPLOYEE_APP_DIR, "db")
DB_FILE = os.path.join(REPO_ROOT, "expenses_system_db.db")


def before_all(context):
    context.base_url = BASE_URL
    context.driver = webdriver.Chrome()
    context.driver.implicitly_wait(5)


def before_scenario(context, scenario):
    context.driver.delete_all_cookies()

    context.driver.get(context.base_url)

    context.driver.execute_script(
        "window.localStorage.clear();"
    )
    context.driver.execute_script(
        "window.sessionStorage.clear();"
    )

    context.driver.refresh()


def after_step(context, step):
    if SLOW_MO:
        time.sleep(SLOW_MO)


def after_all(context):
    if hasattr(context, "driver"):
        context.driver.quit()