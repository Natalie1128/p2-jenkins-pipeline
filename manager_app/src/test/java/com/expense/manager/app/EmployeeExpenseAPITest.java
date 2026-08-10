package com.expense.manager.app;

import com.expense.manager.models.Expense;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Employee Expense API")
public class EmployeeExpenseAPITest extends BaseEmployeeAPITest {
    @Test
    @DisplayName("All expenses GET path")
    void get_expenses_returnsSuccess() {
        given()
                .get("/expenses")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("POST expense")
    void post_expense_getValidResponse() {

        String expenseRequestBody = """
				{
					"user_id": %d,
					"amount": 999.99,
					"description": "test expense",
					"date": "2026-06-01"
				}
				""";
        expenseRequestBody = String.format(expenseRequestBody, TEST_EMPLOYEE.getId());

        Expense newExpense = given()
                .contentType(ContentType.JSON)
                .body(expenseRequestBody)
                .post("/expenses")
                .then()
                .statusCode(201)
                .extract().as(Expense.class);
        dirtyExpenses.add(newExpense);
        assertNotNull(newExpense);
        assertEquals("test expense", newExpense.getDescription());
        assertEquals(TEST_EMPLOYEE.getId(), newExpense.getUser_id());
    }

    @Test
    @DisplayName("GET specific expense")
    void get_expenses_specificExpense() {
        Expense newExpense = postTestExpense();

        Expense responseExpense = given()
                .get("/expenses/" + newExpense.getId())
                .then()
                .statusCode(200)
                .extract().as(Expense.class);
        assertEquals(responseExpense.getId(), newExpense.getId());
    }
    @Test
    @DisplayName("Getting a non-existent expense gets 404")
    void get_expenses_invalidGets404() {
        Expense newExpense = postTestExpense();
        deleteExpenses();
        given()
                .get("expenses/" + newExpense.getId())
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("PUT correctly updates expense")
    void put_expenses_correctlyUpdatesExpense() {
        Expense newExpense = postTestExpense();
        String expenseUpdateRequestBody = """
				{
					"user_id": %d,
					"amount": 500.00,
					"description": "new description",
					"date": "2026-07-01"
				}
				""";
        expenseUpdateRequestBody = String.format(expenseUpdateRequestBody, TEST_EMPLOYEE.getId());
        Expense updatedExpense = given()
                .contentType(ContentType.JSON)
                .body(expenseUpdateRequestBody)
                .put("/expenses/" + newExpense.getId())
                .then()
                .statusCode(201)
                .extract().as(Expense.class);
        assertNotNull(updatedExpense);
        assertEquals("new description", updatedExpense.getDescription());
        assertEquals("2026-07-01", updatedExpense.getDate());
        assertEquals(500.00, updatedExpense.getAmount());
    }

    @Test
    @DisplayName("PUT gets 404 when updating non-existent expense")
    void put_expenses_nonexistentExpenseGets404() {
        Expense newExpense = postTestExpense();
        deleteExpenses();
        String expenseUpdateRequestBody = """
				{
					"user_id": %d,
					"amount": 500.00,
					"description": "new description",
					"date": "2026-07-01"
				}
				""";
        given()
                .contentType(ContentType.JSON)
                .body(expenseUpdateRequestBody)
                .put("/expenses/" + newExpense.getId())
                .then()
                .statusCode(404);
    }
    @Test
    @DisplayName("GET all expenses for a user")
    void get_expensesUser_getAllUserExpenses() {
        Expense expense1 = postTestExpense();
        Expense expense2 = postTestExpense();
        Expense expense3 = postTestExpense();
        List<Expense> expectedExpenses = List.of(expense1, expense2, expense3);
        List<Expense> actualExpenses = given()
                .get("/expenses/user/" + TEST_EMPLOYEE.getId())
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getList("$", Expense.class);
        assertEquals(expectedExpenses.size(), actualExpenses.size());
        for (int i = 0; i < actualExpenses.size(); i++) {
            Expense expectedExpense = expectedExpenses.get(i);
            Expense actualExpense = actualExpenses.get(i);
            assertEquals(expectedExpense.getId(), actualExpense.getId());
            assertEquals(expectedExpense.getDescription(), actualExpense.getDescription());
            assertEquals(expectedExpense.getDate(), actualExpense.getDate());
        }
    }
}
