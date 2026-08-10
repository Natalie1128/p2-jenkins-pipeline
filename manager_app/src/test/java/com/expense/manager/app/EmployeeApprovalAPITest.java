package com.expense.manager.app;

import com.expense.manager.models.Approval;
import com.expense.manager.models.Expense;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Employee Approval API")
public class EmployeeApprovalAPITest extends BaseEmployeeAPITest {
    @Test
    @DisplayName("All approvals GET path")
    void get_approvals_returnsSuccess() {
        given()
                .when()
                .get("/approvals")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("POST approval")
    void post_approval_getValidResponse() {

        Expense newExpense = postTestExpense();

        String approvalRequestBody = """
				{
					"expense_id": %d,
					"status": "pending",
					"reviewer": %d,
					"comment": "test approval comment",
					"review_date": "2026-06-02"
				}
				""";
        approvalRequestBody = String.format(approvalRequestBody, newExpense.getId(), TEST_MANAGER.getId());
        Approval newApproval = given()
                .contentType(ContentType.JSON)
                .body(approvalRequestBody)
                .post("/approvals")
                .then()
                .statusCode(201)
                .extract().as(Approval.class);
        assertNotNull(newApproval);
        dirtyApprovals.add(newApproval);
    }


    @Test
    @DisplayName("GET specific approval")
    void get_approvals_specificApproval() {
        Expense newExpense = postTestExpense();
        Approval newApproval = postTestApproval(newExpense.getId());
        Approval responseApproval = given()
                .get("/approvals/" + newApproval.getId())
                .then()
                .statusCode(200)
                .extract().as(Approval.class);
        assertEquals(newApproval.getId(), responseApproval.getId());
        assertEquals(newApproval.getComment(), responseApproval.getComment());
    }
    @Test
    @DisplayName("GET matching approval for expense")
    void get_approvalsExpense_getMatchingApproval() {
        Expense originalExpense = postTestExpense();
        Approval originalApproval = postTestApproval(originalExpense.getId());
        Approval responseApproval = given()
                .get("/approvals/expense/" + originalExpense.getId())
                .then()
                .statusCode(200)
                .extract().as(Approval.class);
        assertEquals(originalExpense.getId(), responseApproval.getExpense_id());
        assertEquals(originalApproval.getId(), responseApproval.getId());
    }
    @Test
    @DisplayName("Getting a non-existent approval gets 404")
    void get_approvals_invalidGets404() {
        Expense newExpense = postTestExpense();
        Approval newApproval = postTestApproval(newExpense.getId());
        deleteApprovals();
        deleteExpenses();
        given()
                .get("approvals/" + newApproval.getId())
                .then()
                .statusCode(404);
    }
}
