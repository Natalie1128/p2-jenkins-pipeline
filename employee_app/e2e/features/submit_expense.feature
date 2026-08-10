Feature: Submit Expense

  Scenario: Submit a new expense
    Given the app is launched
    When I enter username "alice" and password "password123"
    And I click the login button
    And I click the submit new expense button
    And I enter amount "42.50" and description "Team lunch" dated "2026-07-24"
    And I click the submit expense button
    Then the expense appears in my expense list
