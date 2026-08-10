package com.expense.manager.app;

import com.expense.manager.api.EmployeeRequests;
import com.expense.manager.models.Expense;
import com.expense.manager.models.User;
import com.expense.manager.models.Approval;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Manager Approval API")
public class ManagerApprovalAPITest {
    static EmployeeRequests employeeRequests;
    public static User TEST_EMPLOYEE;
    public static User TEST_MANAGER;

    static void initializeTestUsers() {
        String employeeRequestBody = """
                {
                    "username": "John Employee",
                    "password": "password",
                    "role": "Employee"
                }
                """;
        String managerRequestBody = """
                {
                    "username": "John Manager",
                    "password": "password",
                    "role": "Manager"
                }
                """;
        TEST_EMPLOYEE = given()
                .contentType(ContentType.JSON)
                .body(employeeRequestBody)
                .when()
                .post("http://127.0.0.1:9090/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        TEST_MANAGER = given()
                .contentType(ContentType.JSON)
                .body(managerRequestBody)
                .when()
                .post("http://127.0.0.1:9090/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

    }
    static void deleteTestUsers() {
        for (User user: List.of(TEST_EMPLOYEE, TEST_MANAGER)) {
            given()
                    .when()
                    .delete("/users/" + user.getId())
                    .then()
                    .statusCode(200);
        }
        TEST_EMPLOYEE = null;
        TEST_MANAGER = null;
    }

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://127.0.0.1:9090";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        initializeTestUsers();
        employeeRequests = new EmployeeRequests(TEST_MANAGER);
        assertTrue(employeeRequests.health(), "The manager Approval API test requires the employee backend to be running.");
    }

    @AfterAll
    static void tearDown() {
        employeeRequests.clearAll();
        deleteTestUsers();
    }

    @AfterEach
    void cleanUp() {
        employeeRequests.clearAll();
    }

    @Test
    @DisplayName("GET /approvals gets a valid list of approvals")
    void get_approvals_validList() {
        assertDoesNotThrow(() -> {
            given()
                    .when()
                    .get("/approvals")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response().jsonPath().getList("$", Approval.class);
        }, "GET /approvals does not return a list of approvals");
    }

    @Test
    @DisplayName("GET /approvals/{id} gets the appropriate approval by ID when exists")
    void get_approvalsId_getMatchingApproval() {
        Expense submittedExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "API Test", "2026-07-27"), "pending"
        );
        Approval expectedApproval = employeeRequests.getApproval(submittedExpense.getId());
        Approval actualApproval = given()
                .when()
                .get("/approvals/" + expectedApproval.getId())
                .then()
                .statusCode(200)
                .extract().as(Approval.class);
        assertAll(
                () -> assertEquals(expectedApproval.getId(), actualApproval.getId(), "Approval IDs should match"),
                () -> assertEquals(expectedApproval.getExpense_id(), actualApproval.getExpense_id(), "Approval Expense IDs should match"),
                () -> assertEquals(expectedApproval.getStatus(), actualApproval.getStatus(), "Approval statuses should match"),
                () -> assertEquals(expectedApproval.getReviewer(), actualApproval.getReviewer(), "Approval reviewers should match"),
                () -> assertEquals(expectedApproval.getComment(), actualApproval.getComment(), "Approval comments should match")
        );
    }

    @Test
    @DisplayName("GET /approvals/{id} returns an empty response with no matching approval")
    void get_approvalsId_noMatchingApprovalGetsEmpty() {
        Expense submittedExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "API Test", "2026-07-27"), "pending"
        );
        employeeRequests.clearAll(); // delete the submitted expense
        String responseStr = given()
                .when()
                .get("/approvals/" + submittedExpense.getId())
                .then()
                .statusCode(200)
                .extract().asString();
        assertTrue(responseStr.isEmpty(), "Response should be empty");
    }

    @Test
    @DisplayName("PUT /approvals/id path is active")
    void put_approvalsID_updateApproval() {
        // Postman testing verified that the resulting approval is accurate...
        // But I'm unable to verify that here?
        Expense originalExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 1000.0F, "An expense for testing", "2026-07-20"), "pending"
        );
        Approval originalApproval = employeeRequests.getApproval(originalExpense.getId());
        String updateBody = """
                {
                  "status": "denied",
                  "comment": "this is a new comment.",
                  "review_date": "2026-07-27",
                  "reviewer": "%d"
                }
                """;
        updateBody = String.format(updateBody, TEST_MANAGER.getId());
        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/approvals/" + originalApproval.getId())
                .then()
                .statusCode(200);
    }

}
