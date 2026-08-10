package com.expense.manager.api;

import com.expense.manager.models.Approval;
import com.expense.manager.models.Expense;
import com.expense.manager.models.User;
import io.restassured.http.ContentType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class EmployeeRequests {
    private static final String employeeURI = "http://127.0.0.1:8080";
    private List<Expense> dirtyExpenses;
    private List<Approval> dirtyApprovals;
    private final User manager;
    private Map<Integer, Integer> expenseToApproval;

    public EmployeeRequests(User manager) {
        this.dirtyExpenses = new ArrayList<>();
        this.dirtyApprovals = new ArrayList<>();
        this.expenseToApproval = new HashMap<>();
        this.manager = manager;
    }

    public boolean health() {
        given()
                .when()
                .get(employeeURI +"/health")
                .then()
                .statusCode(200);
        return true;
    }

    public void clearAll() {
        for (Expense expense: this.dirtyExpenses) {
            given()
                    .when()
                    .delete(employeeURI + "/expenses/" + expense.getId())
                    .then()
                    .statusCode(201);
        }
        for (Approval approval: this.dirtyApprovals) {
            given()
                    .when()
                    .delete(employeeURI + "/approvals/" + approval.getId())
                    .then()
                    .statusCode(200);
        }
        dirtyExpenses = new ArrayList<>();
        dirtyApprovals = new ArrayList<>();
    }

    public Expense submitExpense(@NonNull Expense expense, String status) {
        String requestBodyExpense = """
				{
					"user_id": %d,
					"amount": %f,
					"description": "%s",
					"date": "%s"
				}
				""";
        requestBodyExpense = String.format(requestBodyExpense, expense.getUser_id(), expense.getAmount(), expense.getDescription(), expense.getDate());
        Expense newExpense = given()
                .contentType(ContentType.JSON)
                .body(requestBodyExpense)
                .when()
                .post(employeeURI + "/expenses")
                .then()
                .statusCode(201)
                .extract().as(Expense.class);
        this.dirtyExpenses.add(newExpense);

        String requestBodyApproval = """
                {
                    "expense_id": %d,
                    "status": "%s",
                    "reviewer": %d,
                    "comment": "",
                    "review_date": "%s"
                }
                """;
        Integer reviewer = (status.equals("pending")) ? null : manager.getId();
        String date = (status.equals("pending")) ? "" : "2026-07-25";
        requestBodyApproval = String.format(requestBodyApproval, newExpense.getId(), status, reviewer, date);
        Approval newApproval = given()
                .contentType(ContentType.JSON)
                .body(requestBodyApproval)
                .when()
                .post(employeeURI + "/approvals")
                .then()
                .statusCode(201)
                .extract().as(Approval.class);
        dirtyApprovals.add(newApproval);
        expenseToApproval.put(newExpense.getId(), newApproval.getId());
        return newExpense;
    }

    /**
     *
     * @param expenseId corresponding expense ID
     * @return updated approval for the given expense ID
     */
    public Approval getApproval(int expenseId) {
        int approvalId = expenseToApproval.get(expenseId);
        try {
            return given()
                    .when()
                    .get("/approvals/" + approvalId)
                    .then()
                    .extract()
                    .as(Approval.class);
        } catch (RuntimeException e) {
            return null;
        }
    }
}