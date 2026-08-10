# Remove the old combined results if they exist
Remove-Item combined-allure-results -Recurse -Force -ErrorAction Ignore

# Create a new folder
New-Item -ItemType Directory combined-allure-results | Out-Null

# Copy Python results
Copy-Item employee_app\allure-results\* combined-allure-results\
Copy-Item employee_app\e2e\allure-results\* combined-allure-results\


# Copy Java results
Copy-Item manager_app\allure-results\* combined-allure-results\

# Open the report
allure serve combined-allure-results