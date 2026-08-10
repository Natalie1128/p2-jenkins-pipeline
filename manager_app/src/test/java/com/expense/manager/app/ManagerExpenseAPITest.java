package com.expense.manager.app;

import com.expense.manager.api.EmployeeRequests;
import com.expense.manager.models.Expense;
import com.expense.manager.models.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/*
These tests require the employee backend to be running.
 */
@DisplayName("Manager Expense API")
public class ManagerExpenseAPITest {
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
                    .delete("http://127.0.0.1:9090/users/" + user.getId())
                    .then()
                    .statusCode(200);
        }
        TEST_EMPLOYEE = null;
        TEST_MANAGER = null;
    }

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://127.0.0.1:9090/expenses";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        initializeTestUsers();
        employeeRequests = new EmployeeRequests(TEST_MANAGER);
        assertTrue(employeeRequests.health(), "The manager Expense API test requires the employee backend to be running.");
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
    @DisplayName("GET /expenses returns all expenses")
    void get_expenses_ReturnsEverything() {
        assertDoesNotThrow(() -> {
            List<Expense> expenses = given()
                    .get()
                    .then()
                    .statusCode(200)
                    .extract().response().jsonPath().getList("$", Expense.class);
            assertNotNull(expenses);
        }, "GET /expenses does not return a list of expenses");

    }

    @Test
    @DisplayName("GET /expenses/{id} returns specific expense")
    void get_expensesId_getSpecificExpense() {
        Expense expectedExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "Test description", "2026-07-24"),
                "pending"
        );
        Expense actualExpense = given()
                .when()
                .get("/" + expectedExpense.getId())
                .then()
                .extract()
                .as(Expense.class);
        assertAll(
                () -> assertEquals(expectedExpense.getUser_id(), actualExpense.getUser_id(), "User ID should match"),
                () -> assertEquals(expectedExpense.getDescription(), actualExpense.getDescription(), "Description should match"),
                () -> assertEquals(expectedExpense.getDate(), actualExpense.getDate(), "Date should match"),
                () -> assertEquals(expectedExpense.getAmount(), actualExpense.getAmount(), "Amount should match")
        );
    }

    @Test
    @DisplayName("GET /expenses/{id} gets empty response when no matching expense exists")
    void get_expensesId_noExpenseGetNothing() {
        Expense deletedExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "Test description", "2026-07-24"),
                "pending"
        );
        employeeRequests.clearAll(); // delete the submitted expense
        String response = given()
                .when()
                .get("/" + deletedExpense.getId())
                .then()
                .statusCode(200)
                .extract().asString();
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("GET /expenses/status/{status} gets a valid list of expenses with given status")
    void get_expensesStatusStatus_getValidList() {
        // These expenses won't necessarily be read.
        // They serve to ensure that at least one expense of each type exists.
        Expense pendingExpense = new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "expense that will be pending", "2026-07-24");
        Expense approvedExpense = new Expense(-1, TEST_EMPLOYEE.getId(), 200.0F, "expense that will be approved", "2026-07-24");
        Expense deniedExpense = new Expense(-1, TEST_EMPLOYEE.getId(), 300.0F, "expense that will be denied", "2026-07-24");
        employeeRequests.submitExpense(pendingExpense, "pending");
        employeeRequests.submitExpense(approvedExpense, "approved");
        employeeRequests.submitExpense(deniedExpense, "denied");

        List<Expense> pendingExpenses = given()
                .when()
                .get("/status/pending")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getList("$", Expense.class);
        List<Expense> approvedExpenses = given()
                .when()
                .get("/status/approved")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getList("$", Expense.class);
        List<Expense> deniedExpenses = given()
                .when()
                .get("/status/denied")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getList("$", Expense.class);

        Map<String, List<Expense>> statusMap = new HashMap<>();
        statusMap.put("pending", pendingExpenses);
        statusMap.put("approved", approvedExpenses);
        statusMap.put("denied", deniedExpenses);
        for (String status: List.of("pending", "approved", "denied")) {
            List<Expense> expenses = statusMap.get(status);
            assertNotNull(expenses, "Should get a list of expenses");
            assertFalse(expenses.isEmpty(), "List should not be empty after expense with matching status added");
        }
    }

    @Test
    @DisplayName("GET /expenses/user/{userId} gets a valid list of expenses with given user ID")
    void get_expensesUserUserId_getValidList() {
        Expense expectedExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "Test description", "2026-07-24"),
                "pending"
        );
        given()
                .when()
                .get("/" + expectedExpense.getId())
                .then()
                .statusCode(200);
        List<Expense> returnedList = given()
                .when()
                .get("/user/" + TEST_EMPLOYEE.getId())
                .then()
                .extract().response().jsonPath().getList("$", Expense.class);
        assertNotNull(returnedList, "Should be able to retrieve expense by userId");
        assertEquals(1, returnedList.size(), "Retrieved unexpected expense");
    }

    @Test
    @DisplayName("GET /expenses/date/{date} gets a valid list of expenses with given user date")
    void get_expensesDateDate_getValidList() {
        Expense expectedExpense = employeeRequests.submitExpense(
                new Expense(-1, TEST_EMPLOYEE.getId(), 100.0F, "Test description", "2026-07-24"),
                "pending"
        );
        given()
                .when()
                .get("/" + expectedExpense.getId())
                .then()
                .extract()
                .as(Expense.class);
        List<Expense> returnedList = given()
                .when()
                .get("/date/2026-07-24")
                .then()
                .extract().response().jsonPath().getList("$", Expense.class);
        assertNotNull(returnedList, "Should be able to retrieve expense by userId");
        assertFalse(returnedList.isEmpty());
    }

}