Feature: Generate Reports

  As a manager
  I want to generate expense reports
  So that I can review filtered expense data

  Background:
    Given the manager is logged in

  Scenario Outline: Generate filtered expense report
    When the manager selects "<status>" expense status
    And the manager selects "<month>" as the report month
    And the manager selects "<year>" as the report year
    And the manager exports the report
    Then the report should contain only matching expenses

    Examples:
      | status   | month      | year      |
      | ALL      | All Months | All Years |
      | PENDING  | All Months | All Years |
      | APPROVED | All Months | All Years |
      | DENIED   | All Months | All Years |
