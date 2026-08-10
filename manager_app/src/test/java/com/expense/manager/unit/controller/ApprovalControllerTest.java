package com.expense.manager.unit.controller;

import com.expense.manager.controller.ApprovalController;
import com.expense.manager.dao.ApprovalDao;
import com.expense.manager.models.Approval;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Testing for the ApprovalController")
public class ApprovalControllerTest {

    @Mock
    ApprovalDao dao;

    @InjectMocks
    ApprovalController approvalController;


    @Test
    @DisplayName("Update Approval - Approve Expense")
    void updateApproval_approveExpense() throws SQLException {


        Approval approval = new Approval(
                1,
                10,
                "approved",
                5,
                "Approved for client travel.",
                "2026-07-23"
        );


        approvalController.updateApproval(10, approval);


        verify(dao).updateStatus(
                10,
                "approved",
                5,
                "Approved for client travel."
        );
    }


    @Test
    @DisplayName("Update Approval - Reject Expense")
    void updateApproval_rejectExpense() throws SQLException {


        Approval approval = new Approval(
                2,
                11,
                "denied",
                5,
                "Receipt was missing.",
                "2026-07-23"
        );


        approvalController.updateApproval(11, approval);

        verify(dao).updateStatus(
                11,
                "denied",
                5,
                "Receipt was missing."
        );
    }

    @Test
    @DisplayName("Get All Approvals")
    void getApprovals_returnsApprovals() throws SQLException {

        List<Approval> approvals = List.of(
                new Approval(
                        1,
                        10,
                        "pending",
                        0,
                        "",
                        null
                ),
                new Approval(
                        2,
                        11,
                        "approved",
                        5,
                        "Approved for client travel.",
                        "2026-07-23"
                )
        );

        when(dao.findAll()).thenReturn(approvals);

        List<Approval> result = approvalController.getApprovals();

        assertEquals(2, result.size());
        assertEquals(approvals, result);

        verify(dao).findAll();
    }


    @Test
    @DisplayName("Find Approval By ID")
    void findById_returnsApproval() throws SQLException {

        Approval approval = new Approval(
                1,
                10,
                "pending",
                0,
                "",
                null
        );

        when(dao.findById(1)).thenReturn(approval);

        Approval result = approvalController.findById(1);

        assertNotNull(result);
        assertEquals(approval, result);

        verify(dao).findById(1);
    }

    @Test
    @DisplayName("Find Approval By ID - Not Found")
    void findById_notFound() throws SQLException {

        when(dao.findById(999)).thenReturn(null);

        Approval result = approvalController.findById(999);

        assertNull(result);

        verify(dao).findById(999);
    }
}
