package com.expense.manager.unit.dao;

import com.expense.manager.dao.ApprovalDao;
import com.expense.manager.db.Database;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApprovalDao unit Tests")
public class ApprovalDaoTest {

        @Mock
        Database database;

        @Mock
        Connection connection;

        @Mock
        PreparedStatement ps;

        @InjectMocks
        ApprovalDao approvalDao;

        @Test
        @DisplayName("Update Status - Approve Expense Returns True")
        void updateStatus_approveExpense_returnsTrue() throws SQLException {

                when(database.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeUpdate())
                                .thenReturn(1);

                boolean result = approvalDao.updateStatus(
                                10,
                                "approved",
                                5,
                                "Approved for client travel.");

                assertTrue(result);

                verify(ps).setString(1, "approved");
                verify(ps).setInt(2, 5);
                verify(ps).setString(3, "Approved for client travel.");
                verify(ps).setInt(5, 10);
        }

        @Test
        @DisplayName("Update Status - Reject Expense Returns True")
        void updateStatus_rejectExpense_returnsTrue() throws SQLException {

                when(database.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeUpdate())
                                .thenReturn(1);

                boolean result = approvalDao.updateStatus(
                                11,
                                "denied",
                                5,
                                "Receipt was missing.");

                assertTrue(result);

                verify(ps).setString(1, "denied");
                verify(ps).setInt(2, 5);
                verify(ps).setString(3, "Receipt was missing.");
                verify(ps).setInt(5, 11);
        }

        @Test
        @DisplayName("Update Status - Expense Not Found Returns False")
        void updateStatus_expenseNotFound_returnsFalse() throws SQLException {

                when(database.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeUpdate())
                                .thenReturn(0);

                boolean result = approvalDao.updateStatus(
                                999,
                                "approved",
                                5,
                                "No such expense.");

                assertFalse(result);
        }

        @Test
        @DisplayName("Update Status - Database Connection Throws SQLException")
        void updateStatus_connectionFailure_throwsSQLException() throws SQLException {

                when(database.getConnection())
                                .thenThrow(new SQLException("Database connection failed"));

                SQLException exception = assertThrows(
                                SQLException.class,
                                () -> approvalDao.updateStatus(
                                                10,
                                                "approved",
                                                5,
                                                "Approved for client travel."));

                assertEquals("Database connection failed", exception.getMessage());
        }

        @Test
        @DisplayName("Add Comment To Reviewed Expense - Reviewed Expense Returns True")
        void addComment_reviewedExpense_returnsTrue() throws SQLException {

                when(database.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeUpdate())
                                .thenReturn(1);

                boolean result = approvalDao.addCommentToReviewedExpense(
                                10,
                                "Adding a note after review.");

                assertTrue(result);

                verify(ps).setString(1, "Adding a note after review.");
                verify(ps).setInt(2, 10);
        }

        @Test
        @DisplayName("Add Comment To Reviewed Expense - Pending Or Missing Returns False")
        void addComment_pendingOrMissing_returnsFalse() throws SQLException {

                when(database.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeUpdate())
                                .thenReturn(0);

                boolean result = approvalDao.addCommentToReviewedExpense(
                                999,
                                "No matching reviewed expense.");

                assertFalse(result);
        }

        @Test
        @DisplayName("Add Comment To Reviewed Expense - Database Connection Throws SQLException")
        void addComment_connectionFailure_throwsSQLException() throws SQLException {

                when(database.getConnection())
                                .thenThrow(new SQLException("Database connection failed"));

                SQLException exception = assertThrows(
                                SQLException.class,
                                () -> approvalDao.addCommentToReviewedExpense(
                                                10,
                                                "Adding a note after review."));

                assertEquals("Database connection failed", exception.getMessage());
        }
}
