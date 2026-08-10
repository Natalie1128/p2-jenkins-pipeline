Feature: Employee registration

    Scenario: Register with valid credentials
        Given the app is launched
        When I click "Or register"
        Then I am directed to the register page
        When I enter a unique generated username and password "password123"
        And I select the role "Employee"
        And I click the register button
        Then a register success message is displayed

    Scenario: Register with invalid credentials
        Given the app is launched
        When I click "Or register"
        Then I am directed to the register page
        When I enter username "alice" and password "password123"
        And I select the role "Employee"
        And I click the register button
        Then a register error message is displayed

