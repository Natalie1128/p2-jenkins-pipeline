package com.expense.manager.unit.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expense.manager.dao.ApprovalDao;
import com.expense.manager.db.Database;
import com.expense.manager.models.Approval;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find Approval Unit Tests")
public class FindApprovalTest {

        @Mock
        Database database;

        @Mock
        Connection connection;

        @Mock
        PreparedStatement statement;

        @Mock
        ResultSet resultSet;

        ApprovalDao approvalDao;

        @BeforeEach
        void setup() {
                approvalDao = new ApprovalDao(database);
        }

        @Test
        void findById_validId_returnsApproval() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true);

                when(resultSet.getInt("id"))
                                .thenReturn(1);
                when(resultSet.getInt("expense_id"))
                                .thenReturn(40);
                when(resultSet.getString("status"))
                                .thenReturn("approved");
                when(resultSet.getInt("reviewer"))
                                .thenReturn(7);
                when(resultSet.getString("comment"))
                                .thenReturn("Looks good");
                when(resultSet.getString("review_date"))
                                .thenReturn("2026-07-23");

                Approval result = approvalDao.findById(1);

                assertEquals(1, result.getId());
                assertEquals(40, result.getExpense_id());
                assertEquals("approved", result.getStatus());
                assertEquals(7, result.getReviewer());
                assertEquals("Looks good", result.getComment());
        }

        @Test
        void findById_invalidId_returnsNull() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(false);

                Approval result = approvalDao.findById(999);

                assertNull(result);
        }

        @Test
        void findByExpenseId_validExpenseId_returnsApproval() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true);

                when(resultSet.getInt("id"))
                                .thenReturn(1);
                when(resultSet.getInt("expense_id"))
                                .thenReturn(40);
                when(resultSet.getString("status"))
                                .thenReturn("pending");
                when(resultSet.getInt("reviewer"))
                                .thenReturn(0);
                when(resultSet.getString("comment"))
                                .thenReturn(null);
                when(resultSet.getString("review_date"))
                                .thenReturn(null);

                Approval result = approvalDao.findByExpenseId(40);

                assertEquals(1, result.getId());
                assertEquals(40, result.getExpense_id());
                assertEquals("pending", result.getStatus());
        }

        @Test
        void findByExpenseId_invalidExpenseId_returnsNull() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(false);

                Approval result = approvalDao.findByExpenseId(999);

                assertNull(result);
        }

        @Test
        void findAll_returnsAllApprovals() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true)
                                .thenReturn(true)
                                .thenReturn(false);

                when(resultSet.getInt("id"))
                                .thenReturn(1)
                                .thenReturn(2);

                when(resultSet.getInt("expense_id"))
                                .thenReturn(40)
                                .thenReturn(41);

                when(resultSet.getString("status"))
                                .thenReturn("approved")
                                .thenReturn("pending");

                List<Approval> result = approvalDao.findAll();

                assertEquals(2, result.size());
                assertEquals(1, result.get(0).getId());
                assertEquals(2, result.get(1).getId());
        }

        @Test
        void findPending_returnsPendingApprovals() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true)
                                .thenReturn(false);

                when(resultSet.getInt("id"))
                                .thenReturn(1);

                when(resultSet.getInt("expense_id"))
                                .thenReturn(40);

                when(resultSet.getString("status"))
                                .thenReturn("pending");

                List<Approval> result = approvalDao.findPending();

                assertEquals(1, result.size());
                assertEquals("pending", result.get(0).getStatus());
        }
}