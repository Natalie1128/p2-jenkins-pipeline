Feature: Employee login

  Scenario: Login with valid credentials
    Given the app is launched
    When I enter username "alice" and password "password123"
    And I click the login button
    Then the employee dashboard is displayed

  Scenario: Login with invalid credentials
    Given the app is launched
    When I enter username "ghost" and password "wrongpassword"
    And I click the login button
    Then an error message is displayed
