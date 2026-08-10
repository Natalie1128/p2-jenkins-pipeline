Feature: Edit Expense

  Scenario: Edit a newly submitted expense
    Given the app is launched
    When I enter username "alice" and password "password123"
    And I click the login button
    And I click the submit new expense button
    And I enter amount "42.50" and description "Edit me" dated "2026-07-26"
    And I click the submit expense button
    And I edit the expense described as "Edit me" to amount "99.99" description "Edited lunch" dated "2026-07-24"
    Then the expense list shows amount "99.99" and description "Edited lunch" and date "2026-07-24"
