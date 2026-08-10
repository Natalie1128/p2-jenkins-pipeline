package com.expense.manager.unit.dao;

import com.expense.manager.dao.ExpenseDao;
import com.expense.manager.db.Database;
import com.expense.manager.models.Expense;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pending Expense DAO Tests")
public class ExpenseDaoTest {
        @Mock
        Database db;
        @Mock
        Connection connection;
        @Mock
        PreparedStatement ps;
        @Mock
        ResultSet rs;
        @InjectMocks
        ExpenseDao expenseDao;

        @BeforeEach
        void mockSetup() throws SQLException {
                when(db.getConnection())
                                .thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);
                when(ps.executeQuery())
                                .thenReturn(rs);
        }

        @Test
        @DisplayName("findByID with valid expense")
        void findById_valid_returnsExpense() throws SQLException {
                when(rs.next())
                                .thenReturn(true);
                when(rs.getInt("id"))
                                .thenReturn(1);
                when(rs.getInt("user_id"))
                                .thenReturn(2);
                when(rs.getFloat("amount"))
                                .thenReturn((float) 100.00);
                when(rs.getString("description"))
                                .thenReturn("test expense");
                when(rs.getString("date"))
                                .thenReturn("2026-07-22");
                Expense result = expenseDao.findById(1);

                assertEquals(1, result.getId());
                assertEquals(2, result.getUser_id());
                assertEquals(100.00F, result.getAmount());
                assertEquals("test expense", result.getDescription());
                assertEquals("2026-07-22", result.getDate());

        }

        @Test
        @DisplayName("findByID returns null with no expenses matching ID")
        void findById_invalid_returnsNull() throws SQLException {
                when(rs.next())
                                .thenReturn(false);

                assertNull(expenseDao.findById(1));
        }

        @Test
        @DisplayName("findAll gets all current expenses")
        void findAll_valid_returnExpenses() throws SQLException {
                // mock 3 current expenses
                when(rs.next())
                                .thenReturn(true)
                                .thenReturn(true)
                                .thenReturn(true)
                                .thenReturn(false);

                Expense expectedExpense1 = new Expense(1, 4, 100.00F, "expense 1 description", "2026-07-20");
                Expense expectedExpense2 = new Expense(2, 5, 200.00F, "expense 2 description", "2026-07-21");
                Expense expectedExpense3 = new Expense(3, 6, 300.00F, "expense 3 description", "2026-07-22");
                List<Expense> expectedExpenses = List.of(expectedExpense1, expectedExpense2, expectedExpense3);

                when(rs.getInt("id"))
                                .thenReturn(expectedExpense1.getId())
                                .thenReturn(expectedExpense2.getId())
                                .thenReturn(expectedExpense3.getId());
                when(rs.getInt("user_id"))
                                .thenReturn(expectedExpense1.getUser_id())
                                .thenReturn(expectedExpense2.getUser_id())
                                .thenReturn(expectedExpense3.getUser_id());
                when(rs.getFloat("amount"))
                                .thenReturn(expectedExpense1.getAmount())
                                .thenReturn(expectedExpense2.getAmount())
                                .thenReturn(expectedExpense3.getAmount());
                when(rs.getString("description"))
                                .thenReturn(expectedExpense1.getDescription())
                                .thenReturn(expectedExpense2.getDescription())
                                .thenReturn(expectedExpense3.getDescription());
                when(rs.getString("date"))
                                .thenReturn(expectedExpense1.getDate())
                                .thenReturn(expectedExpense2.getDate())
                                .thenReturn(expectedExpense3.getDate());

                List<Expense> actualExpenses = expenseDao.findAll();
                assertNotNull(actualExpenses);
                // sort results by ID
                actualExpenses.sort((a, b) -> {
                        return ((Integer) a.getId()).compareTo((Integer) b.getId());
                });
                assertEquals(expectedExpenses.size(), actualExpenses.size());
                for (int i = 0; i < actualExpenses.size(); i++) {
                        Expense expectedExpense = expectedExpenses.get(i);
                        Expense actualExpense = actualExpenses.get(i);
                        assertAll(
                                        () -> assertEquals(expectedExpense.getId(), actualExpense.getId()),
                                        () -> assertEquals(expectedExpense.getUser_id(), actualExpense.getUser_id()),
                                        () -> assertEquals(expectedExpense.getAmount(), actualExpense.getAmount()),
                                        () -> assertEquals(expectedExpense.getDescription(),
                                                        actualExpense.getDescription()),
                                        () -> assertEquals(expectedExpense.getDate(), actualExpense.getDate()));
                }
        }

        @Test
        @DisplayName("findAll returns empty list with no expenses")
        void findAll_noExpenses_returnsEmptyList() throws SQLException {
                when(rs.next()).thenReturn(false);
                List<Expense> result = expenseDao.findAll();
                assertEquals(0, result.size());
        }

        @Test
        @DisplayName("findByStatus gets expenses with matching status")
        void findByStatus_matchingExists_returnsMatching() throws SQLException {
                Expense expectedExpense1 = new Expense(1, 4, 100.00F, "expense 1 description", "2026-07-20");
                Expense expectedExpense2 = new Expense(2, 4, 200.00F, "expense 2 description", "2026-07-21");
                List<Expense> expectedExpenses = List.of(expectedExpense1, expectedExpense2);

                when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
                when(rs.getInt("id"))
                                .thenReturn(expectedExpense1.getId()).thenReturn(expectedExpense2.getId());
                when(rs.getInt("user_id"))
                                .thenReturn(expectedExpense1.getUser_id()).thenReturn(expectedExpense2.getUser_id());
                when(rs.getFloat("amount"))
                                .thenReturn(expectedExpense1.getAmount()).thenReturn(expectedExpense2.getAmount());
                when(rs.getString("description"))
                                .thenReturn(expectedExpense1.getDescription())
                                .thenReturn(expectedExpense2.getDescription());
                when(rs.getString("date"))
                                .thenReturn(expectedExpense1.getDate()).thenReturn(expectedExpense2.getDate());

                List<Expense> actualExpenses = expenseDao.findByStatus("pending");

                assertNotNull(actualExpenses);
                assertEquals(2, actualExpenses.size());
                actualExpenses.sort((a, b) -> {
                        return ((Integer) a.getId()).compareTo((Integer) b.getId());
                });

                for (int i = 0; i < actualExpenses.size(); i++) {
                        Expense expectedExpense = expectedExpenses.get(i);
                        Expense actualExpense = actualExpenses.get(i);
                        assertAll(
                                        () -> assertEquals(expectedExpense.getId(), actualExpense.getId()),
                                        () -> assertEquals(expectedExpense.getUser_id(), actualExpense.getUser_id()),
                                        () -> assertEquals(expectedExpense.getAmount(), actualExpense.getAmount()),
                                        () -> assertEquals(expectedExpense.getDescription(),
                                                        actualExpense.getDescription()),
                                        () -> assertEquals(expectedExpense.getDate(), actualExpense.getDate()));
                }
        }

        @Test
        @DisplayName("findByUserId gets expenses with matching user ID")
        void findByUserId_userHasExpenses_returnsMatching() throws SQLException {
                Expense expectedExpense1 = new Expense(1, 4, 100.00F, "expense 1 description", "2026-07-20");
                Expense expectedExpense2 = new Expense(2, 4, 200.00F, "expense 2 description", "2026-07-21");
                List<Expense> expectedExpenses = List.of(expectedExpense1, expectedExpense2);

                when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
                when(rs.getInt("id"))
                                .thenReturn(expectedExpense1.getId()).thenReturn(expectedExpense2.getId());
                when(rs.getInt("user_id"))
                                .thenReturn(expectedExpense1.getUser_id()).thenReturn(expectedExpense2.getUser_id());
                when(rs.getFloat("amount"))
                                .thenReturn(expectedExpense1.getAmount()).thenReturn(expectedExpense2.getAmount());
                when(rs.getString("description"))
                                .thenReturn(expectedExpense1.getDescription())
                                .thenReturn(expectedExpense2.getDescription());
                when(rs.getString("date"))
                                .thenReturn(expectedExpense1.getDate()).thenReturn(expectedExpense2.getDate());

                List<Expense> actualExpenses = expenseDao.findByUserId(4);

                assertNotNull(actualExpenses);
                assertEquals(2, actualExpenses.size());
                actualExpenses.sort((a, b) -> {
                        return ((Integer) a.getId()).compareTo((Integer) b.getId());
                });

                for (int i = 0; i < actualExpenses.size(); i++) {
                        Expense expectedExpense = expectedExpenses.get(i);
                        Expense actualExpense = actualExpenses.get(i);
                        assertAll(
                                        () -> assertEquals(expectedExpense.getId(), actualExpense.getId()),
                                        () -> assertEquals(expectedExpense.getUser_id(), actualExpense.getUser_id()),
                                        () -> assertEquals(expectedExpense.getAmount(), actualExpense.getAmount()),
                                        () -> assertEquals(expectedExpense.getDescription(),
                                                        actualExpense.getDescription()),
                                        () -> assertEquals(expectedExpense.getDate(), actualExpense.getDate()));
                }

        }

        @Test
        @DisplayName("findByDate gets expenses with matching date")
        void findByDate_expensesOnDate_returnsMatching() throws SQLException {
                when(rs.next())
                                .thenReturn(true).thenReturn(false);
                when(rs.getInt("id"))
                                .thenReturn(1);
                when(rs.getInt("user_id"))
                                .thenReturn(2);
                when(rs.getFloat("amount"))
                                .thenReturn((float) 100.00);
                when(rs.getString("description"))
                                .thenReturn("test expense");
                when(rs.getString("date"))
                                .thenReturn("2026-07-22");
                List<Expense> result = expenseDao.findByDate("2026-07-22");

                assertNotNull(result);
                assertEquals(1, result.size());
        }

}
