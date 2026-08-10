package com.expense.manager.app;

import com.expense.manager.models.Approval;
import com.expense.manager.models.Expense;
import com.expense.manager.models.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public abstract class BaseEmployeeAPITest {
    protected static User TEST_EMPLOYEE;
    protected static User TEST_MANAGER;
    protected static List<Expense> dirtyExpenses;
    protected static List<Approval> dirtyApprovals;
    protected static List<User> dirtyUsers;

    static void initializeUsers() {
        // create a new test employee and test manager
        String testEmployeeBody = """
				{
				    "username": "test employee",
				    "password": "password123",
				    "role": "Employee"
				}
				""";
        String testManagerBody = """
				{
				    "username": "test manager",
				    "password": "password456",
				    "role": "Manager"
				}
				""";

        TEST_EMPLOYEE = given()
                .contentType(ContentType.JSON)
                .body(testEmployeeBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .as(User.class);

        TEST_MANAGER = given()
                .contentType(ContentType.JSON)
                .body(testManagerBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .as(User.class);
    }

    static void deleteUsers() {
        if (TEST_EMPLOYEE != null) {
            given()
                    .delete("/users/" + TEST_EMPLOYEE.getId())
                    .then()
                    .statusCode(201);
            TEST_EMPLOYEE = null;
        }
        if (TEST_MANAGER != null) {
            given()
                    .delete("/users/" + TEST_MANAGER.getId())
                    .then()
                    .statusCode(201);
            TEST_MANAGER = null;
        }
    }

    static void deleteExpenses() {
        if (dirtyExpenses == null || dirtyExpenses.isEmpty()) {
            return;
        }
        for (Expense expense : dirtyExpenses) {
            given()
                    .delete("/expenses/" + expense.getId())
                    .then()
                    .statusCode(201);
        }
        dirtyExpenses = new ArrayList<>();
    }
    static Expense postTestExpense() {

        String expenseRequestBody = """
				{
					"user_id": %d,
					"amount": 999.99,
					"description": "test expense",
					"date": "2026-06-01"
				}
				""";
        expenseRequestBody = String.format(expenseRequestBody, TEST_EMPLOYEE.getId());

        Expense retExpense = given()
                .contentType(ContentType.JSON)
                .body(expenseRequestBody)
                .post("/expenses")
                .then()
                .statusCode(201)
                .extract().as(Expense.class);
        dirtyExpenses.add(retExpense);
        return retExpense;

    }

    static Approval postTestApproval(int expenseId) {
        return postTestApproval(expenseId, "pending");
    }

    /**
     * Posts a test approval with a given expense id that is used for multiple
     * tests.
     * This approval is added to dirtyApprovals.
     */
    static Approval postTestApproval(int expenseId, String status) {
        String approvalRequestBody = """
				{
					"expense_id": %d,
					"status": "%s",
					"reviewer": %d,
					"comment": "test approval comment",
					"review_date": "2026-06-02"
				}
				""";
        approvalRequestBody = String.format(approvalRequestBody, expenseId, status, TEST_MANAGER.getId());
        Approval newApproval = given()
                .contentType(ContentType.JSON)
                .body(approvalRequestBody)
                .post("/approvals")
                .then()
                .extract().as(Approval.class);
        dirtyApprovals.add(newApproval);
        return newApproval;
    }

    static void deleteApprovals() {
        if (dirtyApprovals == null || dirtyApprovals.isEmpty()) {
            return;
        }
        for (Approval approval : dirtyApprovals) {
            given()
                    .delete("/approvals/" + approval.getId())
                    .then();
        }
        dirtyApprovals = new ArrayList<>();
    }

    static void deleteDirtyUsers() {
        if (dirtyUsers == null || dirtyUsers.isEmpty()) {
            return;
        }
        for (User user : dirtyUsers) {
            given()
                    .delete("/users/" + user.getId())
                    .then();
        }
        dirtyUsers = new ArrayList<>();
    }

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://127.0.0.1:8080"; // change this when URI changes!
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        dirtyExpenses = new ArrayList<>();
        dirtyApprovals = new ArrayList<>();
        dirtyUsers = new ArrayList<>();
        initializeUsers();

    }

    @AfterAll
    static void teardown() {
        deleteUsers();
        RestAssured.reset();
    }

    @AfterEach
    void cleanup() {
        deleteApprovals();
        deleteExpenses();
        deleteDirtyUsers();
    }
}
