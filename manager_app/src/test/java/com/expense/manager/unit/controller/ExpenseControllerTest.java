package com.expense.manager.unit.controller;

import com.expense.manager.controller.ExpenseController;
import com.expense.manager.dao.ExpenseDao;
import com.expense.manager.models.Expense;
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
@DisplayName("Expense Controller Tests")
public class ExpenseControllerTest {

    @Mock
    ExpenseDao dao;

    @InjectMocks
    ExpenseController expenseController;

    @Test
    @DisplayName("getExpenses returns all submitted expenses")
    void getExpenses_returnsAllExpenses() throws SQLException {

        Expense expectedExpense1 = new Expense(1, 4, 100.00F,
                "expense 1 description", "2026-07-20");
        Expense expectedExpense2 = new Expense(2, 5, 200.00F,
                "expense 2 description", "2026-07-21");
        Expense expectedExpense3 = new Expense(3, 6, 300.00F,
                "expense 3 description", "2026-07-22");

        List<Expense> expectedExpenses = List.of(
                expectedExpense1,
                expectedExpense2,
                expectedExpense3);

        when(dao.findAll()).thenReturn(expectedExpenses);

        List<Expense> actualExpenses = expenseController.getExpenses();

        assertNotNull(actualExpenses);
        assertEquals(expectedExpenses.size(), actualExpenses.size());

        for (int i = 0; i < actualExpenses.size(); i++) {
            Expense expected = expectedExpenses.get(i);
            Expense actual = actualExpenses.get(i);

            assertAll(
                    () -> assertEquals(expected.getId(), actual.getId()),
                    () -> assertEquals(expected.getUser_id(), actual.getUser_id()),
                    () -> assertEquals(expected.getAmount(), actual.getAmount()),
                    () -> assertEquals(expected.getDescription(), actual.getDescription()),
                    () -> assertEquals(expected.getDate(), actual.getDate())
            );
        }

        verify(dao).findAll();
    }

    @Test
    @DisplayName("findByUserId returns expenses with matching user ID")
    void findByUserId_returnsMatchingExpenses() throws SQLException {

        Expense expectedExpense1 = new Expense(1, 4, 100.00F,
                "expense 1 description", "2026-07-20");
        Expense expectedExpense2 = new Expense(2, 4, 200.00F,
                "expense 2 description", "2026-07-21");

        List<Expense> expectedExpenses = List.of(
                expectedExpense1,
                expectedExpense2);

        when(dao.findByUserId(4)).thenReturn(expectedExpenses);

        List<Expense> actualExpenses = expenseController.findByUserId(4);

        assertNotNull(actualExpenses);
        assertEquals(expectedExpenses.size(), actualExpenses.size());

        for (int i = 0; i < actualExpenses.size(); i++) {
            Expense expected = expectedExpenses.get(i);
            Expense actual = actualExpenses.get(i);

            assertAll(
                    () -> assertEquals(expected.getId(), actual.getId()),
                    () -> assertEquals(expected.getUser_id(), actual.getUser_id()),
                    () -> assertEquals(expected.getAmount(), actual.getAmount()),
                    () -> assertEquals(expected.getDescription(), actual.getDescription()),
                    () -> assertEquals(expected.getDate(), actual.getDate())
            );
        }

        verify(dao).findByUserId(4);
    }

    @Test
    @DisplayName("findByDate returns expenses with matching date")
    void findByDate_returnsMatchingExpenses() throws SQLException {

        Expense expectedExpense = new Expense(
                1,
                4,
                100.00F,
                "expense 1 description",
                "2026-07-22");

        List<Expense> expectedExpenses = List.of(expectedExpense);

        when(dao.findByDate("2026-07-22")).thenReturn(expectedExpenses);

        List<Expense> actualExpenses =
                expenseController.findByDate("2026-07-22");

        assertNotNull(actualExpenses);
        assertEquals(1, actualExpenses.size());

        Expense actual = actualExpenses.get(0);

        assertAll(
                () -> assertEquals(expectedExpense.getId(), actual.getId()),
                () -> assertEquals(expectedExpense.getUser_id(), actual.getUser_id()),
                () -> assertEquals(expectedExpense.getAmount(), actual.getAmount()),
                () -> assertEquals(expectedExpense.getDescription(), actual.getDescription()),
                () -> assertEquals(expectedExpense.getDate(), actual.getDate())
        );

        verify(dao).findByDate("2026-07-22");
    }

    @Test
    @DisplayName("findById returns expense with matching ID")
    void findById_returnsExpense() throws SQLException {
        Expense expectedExpense = new Expense(
                1,
                4,
                100.00F,
                "expense 1 description",
                "2026-07-20"
        );

        when(dao.findById(1)).thenReturn(expectedExpense);

        Expense actualExpense = expenseController.findById(1);

        assertNotNull(actualExpense);

        assertAll(
                () -> assertEquals(expectedExpense.getId(), actualExpense.getId()),
                () -> assertEquals(expectedExpense.getUser_id(), actualExpense.getUser_id()),
                () -> assertEquals(expectedExpense.getAmount(), actualExpense.getAmount()),
                () -> assertEquals(expectedExpense.getDescription(), actualExpense.getDescription()),
                () -> assertEquals(expectedExpense.getDate(), actualExpense.getDate())
        );

        verify(dao).findById(1);
    }


    @Test
    @DisplayName("findByStatus returns expenses with matching status")
    void findByStatus_returnsMatchingExpenses() throws SQLException {
        Expense expectedExpense1 = new Expense(
                1,
                4,
                100.00F,
                "expense 1 description",
                "2026-07-20"
        );

        Expense expectedExpense2 = new Expense(
                2,
                5,
                200.00F,
                "expense 2 description",
                "2026-07-21"
        );

        List<Expense> expectedExpenses = List.of(
                expectedExpense1,
                expectedExpense2
        );

        when(dao.findByStatus("pending")).thenReturn(expectedExpenses);

        List<Expense> actualExpenses =
                expenseController.findByStatus("pending");

        assertNotNull(actualExpenses);
        assertEquals(expectedExpenses.size(), actualExpenses.size());

        for (int i = 0; i < actualExpenses.size(); i++) {
                Expense expected = expectedExpenses.get(i);
                Expense actual = actualExpenses.get(i);

                assertAll(
                        () -> assertEquals(expected.getId(), actual.getId()),
                        () -> assertEquals(expected.getUser_id(), actual.getUser_id()),
                        () -> assertEquals(expected.getAmount(), actual.getAmount()),
                        () -> assertEquals(expected.getDescription(), actual.getDescription()),
                        () -> assertEquals(expected.getDate(), actual.getDate())
                );
        }

        verify(dao).findByStatus("pending");
    }
}