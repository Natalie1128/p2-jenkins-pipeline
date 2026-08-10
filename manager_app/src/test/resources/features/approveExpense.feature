Feature: Manager approves newly submitted expenses

  Scenario: Manager approves a newly submitted expense in the pending list
    Given an employee is logged in
    When the employee submits a new expense
    And the employee logs out
    And the manager logs in
    And the manager navigates to the pending expenses page
    And the manager opens the expense for review
    And the manager approves the expense
    Then the expense status should be "APPROVED" on the manager page
    When the manager logs out
    And the employee logs in
    And the employee navigates to expense history
    Then the expense status should be "APPROVED" on the employee history page