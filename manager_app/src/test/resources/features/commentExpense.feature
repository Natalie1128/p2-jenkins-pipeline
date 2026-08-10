Feature: Manager adds a review comment to an expense

  Scenario: Manager adds a review comment while approving an expense
    Given an employee is logged in
    When the employee submits a new expense
    And the employee logs out
    And the manager logs in
    And the manager navigates to the pending expenses page
    And the manager opens the expense for review
    And the manager reviews the expense with comment "Looks good"
    And the manager approves the expense
    When the manager logs out
    And the employee logs in
    And the employee navigates to expense history
    Then the expense review comment should be "Looks good" on the employee history page