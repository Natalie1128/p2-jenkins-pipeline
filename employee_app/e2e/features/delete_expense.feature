Feature: Delete Expense

  Scenario: Delete a pending expense
    Given the app is launched
    When I enter username "alice" and password "password123"
    And I click the login button
    And I click the submit new expense button
    And I enter amount "42.50" and description "Delete me" dated "2026-07-26"
    And I click the submit expense button
    And I delete the expense described as "Delete me"
    And I confirm the deletion
    Then the expense "Delete me" no longer appears in my expense list
