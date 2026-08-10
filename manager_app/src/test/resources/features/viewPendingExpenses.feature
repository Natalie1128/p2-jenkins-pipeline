Feature: Manager views newly submitted expenses

  Scenario: Manager sees a newly submitted expense in the pending list
    Given an employee is logged in
    When the employee submits a new expense
    And the employee logs out
    And the manager logs in
    And the manager navigates to the pending expenses page
    Then the newly submitted expense should appear in the pending expenses list