package com.expense.manager.app;

import com.expense.manager.models.Expense;
import com.expense.manager.models.User;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Employee User API")
public class EmployeeUserAPITest extends BaseEmployeeAPITest {
    @Test
    @DisplayName("Health of API is OK")
    void get_health_isOkay() {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("All users GET path")
    void get_users_returnsSuccess() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Test login with valid credentials")
    void post_usersLogin_validCredentialsGetsLogin() {
        List<User> usersToTest = new ArrayList<>();
        usersToTest.add(TEST_EMPLOYEE);
        usersToTest.add(TEST_MANAGER);
        for (User user : usersToTest) {
            String loginRequestBody = """
					{
						"username": "%s",
						"password": "%s"
					}
					""";
            loginRequestBody = String.format(loginRequestBody, user.getUsername(), user.getPassword());
            User responseUser = given()
                    .contentType(ContentType.JSON)
                    .body(loginRequestBody)
                    .post("/users/login")
                    .then()
                    .statusCode(200)
                    .extract().as(User.class);
            assertEquals(responseUser.getId(), user.getId());
        }
    }

    @Test
    @DisplayName("Test login with invalid password")
    void post_usersLogin_invalidCredentialsNoLogin() {
        List<User> invalidCredentials = new ArrayList<>();
        invalidCredentials.add(new User(TEST_EMPLOYEE.getId(), TEST_EMPLOYEE.getUsername(), "invalid password",
                TEST_EMPLOYEE.getRole()));
        invalidCredentials.add(new User(TEST_EMPLOYEE.getId(), "invalid name", TEST_EMPLOYEE.getPassword(),
                TEST_EMPLOYEE.getRole()));
        invalidCredentials.add(new User(TEST_MANAGER.getId(), TEST_MANAGER.getUsername(), "invalid password",
                TEST_MANAGER.getRole()));
        invalidCredentials.add(new User(TEST_MANAGER.getId(), "invalid name", TEST_MANAGER.getPassword(),
                TEST_MANAGER.getRole()));
        for (User user : invalidCredentials) {
            String loginRequestBody = """
					{
						"username": "%s",
						"password": "%s"
					}
					""";
            loginRequestBody = String.format(loginRequestBody, user.getUsername(), user.getPassword());
            given()
                    .contentType(ContentType.JSON)
                    .body(loginRequestBody)
                    .post("/users/login")
                    .then()
                    .statusCode(404);
        }
    }

    @Test
    @DisplayName("GET specific user")
    void get_user_getSpecificUser() {
        List<User> expectedUsers = List.of(TEST_EMPLOYEE, TEST_MANAGER);
        for (User expectedUser : expectedUsers) {
            User actualUser = given()
                    .get("/users/" + expectedUser.getId())
                    .then()
                    .extract().as(User.class);
            assertEquals(expectedUser.getId(), actualUser.getId());
            assertEquals(expectedUser.getRole(), actualUser.getRole());
        }
    }

    @Test
    @DisplayName("Trying to GET specific user gets 404 when user doesn't exist")
    void get_user_nonExistent404() {
        String tempUserBody = """
				{
					"username": "test temp user",
					"password": "12345678",
					"role": "Employee"
				}
				""";
        User tempUser = given()
                .contentType(ContentType.JSON)
                .body(tempUserBody)
                .post("/users")
                .then()
                .extract().as(User.class);
        given()
                .delete("/users/" + tempUser.getId())
                .then();
        given()
                .get("/users/" + tempUser.getId())
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("POST new user")
    void post_user_getNewUser() {
        String newUserBody = """
				{
					"username": "temp test user",
					"password": "soelifhaks",
					"role": "Employee"
				}
				""";
        User newUser = given()
                .contentType(ContentType.JSON)
                .body(newUserBody)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);
        assertNotNull(newUser);
        assertEquals("temp test user", newUser.getUsername());
        dirtyUsers.add(newUser);
    }

    @Test
    @DisplayName("GET history for user")
    void get_expensesUserHistory_nonPendingExpenses() {
        Expense pendingExpense = postTestExpense();
        Expense approvedExpense = postTestExpense();
        Expense deniedExpense = postTestExpense();
        postTestApproval(pendingExpense.getId(), "pending");
        postTestApproval(approvedExpense.getId(), "approved");
        postTestApproval(deniedExpense.getId(), "denied");
        List<Expense> history = given()
                .get("/expenses/user/" + TEST_EMPLOYEE.getId() + "/history")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getList("$", Expense.class);
        assertEquals(2, history.size());
    }
}
