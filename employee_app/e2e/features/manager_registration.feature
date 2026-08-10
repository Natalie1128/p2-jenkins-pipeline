Feature: Manager Registration and Login

  Scenario: New manager registration and login directs to manager dashboard
    Given the app is launched
    When I click "Or register"
    Then I am directed to the register page
    When I register a new manager with username "new_manager" and password "password123"
    And I select the role "Manager"
    And I click the register button
    Then a register success message is displayed
