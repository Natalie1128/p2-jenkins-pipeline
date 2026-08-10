Feature: Manager Login

  Scenario Outline: Manager login
    Given the user is on the manager login page
    When the user enters username "<username>"
    And the user enters password "<password>"
    And the user clicks the login button
    Then the login result should be "<result>"

    Examples:
      | username      | password      | result  |
      | manager       | admin123      | success |
      | manager       | wrongpassword | failure |
      | wrongUsername | admin123      | failure |